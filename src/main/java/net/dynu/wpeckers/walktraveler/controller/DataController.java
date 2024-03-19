package net.dynu.wpeckers.walktraveler.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.authentication.api.enums.MessageStatus;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import net.dynu.wpeckers.walktraveler.rest.messaging.map.ReadMapDataResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.PointModel;
import net.dynu.wpeckers.walktraveler.service.PointService;
import net.dynu.wpeckers.walktraveler.service.SessionService;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/datas")
@Api(value="Datas", description="Direct data API")
@RequiredArgsConstructor
public class DataController extends ControllerBase {

    private final SessionService sessionService;
    private final PointService pointService;

    @ApiOperation(value = "Read map data")
    @RequestMapping(value = "/map", method = RequestMethod.GET)
    public @ResponseBody ReadMapDataResponse readMapData(@RequestHeader(value = "sessionId", required = false) String sessionId) throws SessionTimeoutException {
        UserEntity user = sessionService.validateSession(sessionId);
        ReadMapDataResponse response = new ReadMapDataResponse();
        response.setOnlineUsers(converter.convertUsers(sessionService.getLoggedInUsers()));
        response.setPoints(converter.convertPoints(pointService.readLatestPointsForUser(user.getEmail())));
        response.setMessage("Read " + response.getOnlineUsers().size() + " online users and " + response.getPoints().size() + " points!");
        response.setMessageStatus(MessageStatus.OK);
        return response;
    }
}
