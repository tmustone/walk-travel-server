package net.dynu.wpeckers.walktraveler.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.authentication.api.enums.MessageStatus;
import net.dynu.wpeckers.authentication.api.messaging.user.LogoutResponse;
import net.dynu.wpeckers.authentication.api.messaging.user.ValidateSessionResponse;
import net.dynu.wpeckers.common.Common;
import net.dynu.wpeckers.walktraveler.configuration.AuthenticationConfiguration;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.UserInfoResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.*;
import net.dynu.wpeckers.walktraveler.service.GameService;
import net.dynu.wpeckers.walktraveler.service.MailService;
import net.dynu.wpeckers.walktraveler.service.SessionService;
import net.dynu.wpeckers.walktraveler.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
@Api(value="Users", description="User API")
@RequiredArgsConstructor
public class UserController extends ControllerBase {

    private final UserService userService;
    private final AuthenticationConfiguration authenticationServiceClient;
    private final SessionService sessionService;
    private final GameService gameService;
    private final MailService mailService;

    @ApiOperation(value = "Read a user with ID")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public @ResponseBody ReadUserResponse readUser(
            @RequestHeader(value = "sessionId", required = false) String sessionId,
            @PathVariable String id) throws SessionTimeoutException {
        log.info(" === readUser(" + id + ")===");
        sessionService.validateSession(sessionId);
        Long userId = Common.parseLong(id);
        if (userId == null) {
            return new ReadUserResponse(Status.ERROR, "Invalid user ID \"" + id + "\"", null);
        } else {
            UserModel user = converter.convert(userService.read(userId));
            if (user == null) {
                return new ReadUserResponse(Status.NOTFOUND, "User not found with id \"" + id + "\"", null);
            } else {
                return new ReadUserResponse(Status.OK, "Read user \"" + id + "\" successfully!", user);
            }
        }
    }

    @ApiOperation(value = "Read all users")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public @ResponseBody ReadUsersResponse readUsers(@RequestHeader(value = "sessionId", required = false) String sessionId) throws SessionTimeoutException {
        sessionService.validateSession(sessionId);
        log.info(" === readUsers()===");
        List<UserModel> users = new LinkedList<>();
        for (UserEntity user : userService.readAll()) {
            users.add(converter.convert(user));
        }
        log.info("Read " + users.size() + " users successfully!");
        return new ReadUsersResponse(Status.OK, "Read " + users.size() + " users successfully!", users);
    }

    @ApiOperation(value = "Read all online users")
    @RequestMapping(value = "/onlineusers", method = RequestMethod.GET)
    public @ResponseBody ReadUsersResponse readOnlineUsers(@RequestHeader(value = "sessionId", required = false) String sessionId) throws SessionTimeoutException {
        sessionService.validateSession(sessionId);
        log.info(" === readOnlineUsers()===");
        List<UserModel> users = converter.convertUsers(new LinkedList<>(sessionService.getLoggedInUsers().values()));
        users = gameService.populateCollectCounts(users);
        log.info("Read " + users.size() + " users successfully!");
        return new ReadUsersResponse(Status.OK, "Read " + users.size() + " online users successfully!", users);
    }

    @ApiOperation(value = "Read user info")
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public @ResponseBody UserInfoResponse readUserInfo(@RequestHeader(value = "sessionId", required = false) String sessionId) throws SessionTimeoutException {
        UserEntity user = sessionService.validateSession(sessionId);
        UserInfoResponse response = new UserInfoResponse();
        response.setMessage("User info request success!");
        response.setStatus(Status.OK);
        response.setUser(converter.convert(user));
        return response;
    }

    @ApiOperation(value = "Update user position")
    @RequestMapping(value = "/position", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody UpdatePositionResponse updatePosition(@RequestHeader(value = "sessionId", required = false) String sessionId, @RequestBody UpdatePositionRequest request) throws SessionTimeoutException {
        log.info(" === updatePosition()===");
        UserEntity user = sessionService.validateSession(sessionId);
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        gameService.updateUserPosition(user, sessionId);

        // Calculate and collected points
        List<PointEntity> collectedPoints = gameService.collectPoints(user, user.getLongitude(), user.getLatitude());

        UpdatePositionResponse response = new UpdatePositionResponse();
        response.setMessage("Position updated successfully!");
        response.setStatus(Status.OK);
        response.setLongitude(request.getLongitude());
        response.setLatitude(request.getLatitude());
        response.setCollectedPoints(converter.convertPoints(collectedPoints));
        return response;
    }

    @ApiOperation(value = "Fast login user")
    @RequestMapping(value = "/fastlogin", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody FastLoginUserResponse fastLogin(@RequestBody FastLoginUserRequest request) {
        FastLoginUserResponse response = new FastLoginUserResponse();
        UserEntity user = userService.readAndLoginUserByFastLoginSecret(request.getFastLoginSecret());
        if (user != null) {
            String sessionId = UUID.randomUUID().toString();
            sessionService.login(sessionId, user);
            response.setMessage("User logged in successfully!");
            response.setStatus(Status.OK);
            response.setUser(converter.convert(user));
            response.setFastLoginSecret(request.getFastLoginSecret());
            response.setSessionId(sessionId);
            log.info("User {} logged in successfully with fast login secret {}!", user.getEmail(), request.getFastLoginSecret());
        } else {
            response.setMessage("User not found with given fast login secret!");
            response.setStatus(Status.NOTFOUND);
            log.warn("User not found with given login secret {} from database!", request.getFastLoginSecret());
        }
        return response;
    }

    @ApiOperation(value = "Register user")
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody RegisterUserResponse registerOrLoginUser(@RequestBody RegisterUserRequest request) {
        RegisterUserResponse response = new RegisterUserResponse();
        String fastLoginSecret = UUID.randomUUID().toString();
        UserEntity user = userService.readByEmail(request.getEmail());
        if (user != null) {
            user.setFastLoginSecret(fastLoginSecret);
            user.setFastLoginSecretDate(new Date());
            userService.update(user);
            log.info("User {} fast login secret updated successfully!", user.getEmail());

            mailService.sendWelcomeMail(request.getEmail(), fastLoginSecret, request.getServiceBaseUrl());

            response.setMessage("User fast login secret updated successfully!");
            response.setStatus(Status.OK);
            response.setEmail(request.getEmail());
        } else {
            user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setRegisterDate(new Date());
            user.setFastLoginSecret(fastLoginSecret);
            user.setFastLoginSecretDate(new Date());
            user.setCreatedDate(new Date());
            user.setModifiedDate(new Date());
            Long userId = userService.create(user);
            log.info("User {} with e-mail {} fast login secret updated successfully!", userId, user.getEmail());
        }
        mailService.sendWelcomeMail(request.getEmail(), fastLoginSecret, request.getServiceBaseUrl());
        log.info("Sent welcome mail to {}" , request.getEmail());
        return response;
    }

    @ApiOperation(value = "Logout")
    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody LogoutUserResponse fastLogin(@RequestHeader(value = "sessionId", required = false) String sessionId, @RequestBody LogoutUserRequest request) throws SessionTimeoutException {
        LogoutUserResponse response = new LogoutUserResponse();
        UserEntity user = sessionService.validateSession(sessionId);
        sessionService.logout(sessionId, user);
        log.info("User {} logged out from from session {}", user.getEmail(), sessionId);
        response.setMessage("User logged out successfully from session " + sessionId);
        response.setStatus(Status.OK);
        response.setSessionId(sessionId);
        return response;
    }
}
