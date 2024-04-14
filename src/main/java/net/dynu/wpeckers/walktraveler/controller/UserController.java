package net.dynu.wpeckers.walktraveler.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.authentication.api.enums.MessageStatus;
import net.dynu.wpeckers.authentication.api.messaging.user.ValidateSessionResponse;
import net.dynu.wpeckers.common.Common;
import net.dynu.wpeckers.walktraveler.configuration.AuthenticationConfiguration;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.UserInfoResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.LoginUserRequest;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.LoginUserResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.ReadUserResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.ReadUsersResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UpdatePositionRequest;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UpdatePositionResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UserModel;
import net.dynu.wpeckers.walktraveler.service.GameService;
import net.dynu.wpeckers.walktraveler.service.PointService;
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
        List<UserModel> users = converter.convertUsers(sessionService.getLoggedInUsers());
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
    public @ResponseBody UpdatePositionResponse loginUser(@RequestHeader(value = "sessionId", required = false) String sessionId, @RequestBody UpdatePositionRequest request) throws SessionTimeoutException {
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

    @ApiOperation(value = "Login user")
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody LoginUserResponse loginUser(@RequestBody LoginUserRequest request) {
        log.info(" === loginUser({})===", request.getSessionId());
        ValidateSessionResponse validateSessionResponse = this.authenticationServiceClient.getAuthenticationClient().validateSession(request.getSessionId());
        log.info("AuthenticationService response : " + validateSessionResponse);

        LoginUserResponse response = new LoginUserResponse();
        if (validateSessionResponse.getMessageStatus() == MessageStatus.OK) {
            String email = validateSessionResponse.getSession().getUserEmail();
            if (validateSessionResponse.getSession().getServiceName().equals(authenticationServiceClient.getServiceName())) {
                UserEntity user = userService.login(email);
                user.setLastLoginDate(new Date());
                userService.update(user);
                sessionService.login(request.getSessionId(), user);
                response.setUser(converter.convert(user));
                response.setMessage("User " + email + " successfully logged in to service!");
                response.setStatus(Status.OK);
            } else {
                log.warn("User {} does not have permission to service: Service {} VS {}!", email, validateSessionResponse.getSession().getServiceName(), authenticationServiceClient.getServiceName());
                response.setMessage("User " + email + " has not permission to this service with this session!");
                response.setStatus(Status.ERROR);
            }
        } else {
            response.setMessage("Login failed for user failed : " + response.getMessage());
            response.setStatus(Status.ERROR);
        }
        return response;
    }
}
