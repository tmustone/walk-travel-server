package net.dynu.wpeckers.walktraveler.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.map.ReadMapDataResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.map.ReadPointDataResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.PointTemplateModel;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UserModel;
import net.dynu.wpeckers.walktraveler.service.GameService;
import net.dynu.wpeckers.walktraveler.service.PointService;
import net.dynu.wpeckers.walktraveler.service.PointTemplateService;
import net.dynu.wpeckers.walktraveler.service.SessionService;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/datas")
@Api(value="Datas", description="Direct data API")
@RequiredArgsConstructor
public class DataController extends ControllerBase {

    private final SessionService sessionService;
    private final PointService pointService;
    private final GameService gameService;
    private final PointTemplateService pointTemplateService;

    @ApiOperation(value = "Read map data")
    @RequestMapping(value = "/map", method = RequestMethod.GET)
    public @ResponseBody ReadMapDataResponse readMapData(@RequestHeader(value = "sessionId", required = false) String sessionId) throws SessionTimeoutException {
        UserEntity user = sessionService.validateSession(sessionId);
        ReadMapDataResponse response = new ReadMapDataResponse();
        List<UserModel> onlineUsers = converter.convertUsers(new LinkedList<>(sessionService.getLoggedInUsers().values()));
        gameService.populateCollectCounts(onlineUsers);
        response.setOnlineUsers(onlineUsers);
        response.setPoints(converter.convertPoints(pointService.readLatestPointsForUser(user.getEmail())));
        response.setMessage("Read " + response.getOnlineUsers().size() + " online users and " + response.getPoints().size() + " points!");
        response.setStatus(Status.OK);
        return response;
    }

    @ApiOperation(value = "Read point data")
    @RequestMapping(value = "/points", method = RequestMethod.GET)
    public @ResponseBody ReadPointDataResponse readPointData(@RequestHeader(value = "sessionId", required = false) String sessionId) throws SessionTimeoutException {
        UserEntity user = sessionService.validateSession(sessionId);

        // Read already collected points
        List<PointEntity> pointEntities = pointService.readByUserEmailAndPointStatus(user.getEmail(), PointStatus.COLLECTED);
        log.debug("Collected points by user: " + pointEntities);
        Map<String,Long> pointTitleToCountMap = new HashMap<>();
        for (PointEntity point : pointEntities) {
            Long currentCount = pointTitleToCountMap.get(point.getTitle());
            if (currentCount == null) {
                currentCount = 0L;
            }
            pointTitleToCountMap.put(point.getTitle(),  currentCount + 1);
        }

        log.debug("Collected points in map: " + pointTitleToCountMap);

        ReadPointDataResponse response = new ReadPointDataResponse();
        List<PointTemplateModel> pointTemplates = converter.convertPointTemplates(pointTemplateService.readAll());
        log.debug("Templates : " + pointTemplates);
        for (PointTemplateModel pointTemplateModel : pointTemplates) {
            Long currentCount = pointTitleToCountMap.get(pointTemplateModel.getTitle());
            pointTemplateModel.setCollectedCount(currentCount != null ? currentCount.intValue() : 0);
        }
        response.setPointTemplates(pointTemplates);
        response.setMessage("Read " + pointTemplates.size() + " point templates!");
        response.setStatus(Status.OK);


        return response;
    }
}
