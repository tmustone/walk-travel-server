package net.dynu.wpeckers.walktraveler.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.PointModel;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.ReadPointResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.ReadPointsRequest;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.ReadPointsResponse;
import net.dynu.wpeckers.walktraveler.service.PointService;
import net.dynu.wpeckers.walktraveler.service.SessionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/points")
@Api(value="Points", description="Points API")
@RequiredArgsConstructor
public class PointController extends ControllerBase {

    private final PointService pointService;
    private final SessionService sessionService;

    @ApiOperation(value = "Read a point by ID")
    @RequestMapping(value = "/point/{id}", method = RequestMethod.GET)
    public @ResponseBody ReadPointResponse readPoint(
            @RequestHeader(value = "sessionId", required = false) String sessionId,
            @PathVariable Long id) throws SessionTimeoutException {
        log.info(" === readPoint({}}) ===", id);
        UserEntity user = sessionService.validateSession(sessionId);
        PointModel point = converter.convert(pointService.read(id));
        if (point == null) {
            return new ReadPointResponse(Status.NOTFOUND, "Point not found with ID " + id, null);
        } else if (user.getEmail().equals(point.getUserEmail()) == false) {
            return new ReadPointResponse(Status.NOTFOUND, "Point not found with ID " + id + " for this user", null);
        } else {
            return new ReadPointResponse(point);
        }
    }

    @ApiOperation(value = "Read active user points")
    @RequestMapping(value = "/activepoints", method = RequestMethod.POST, produces = "application/json")
    public ReadPointsResponse readActivePoints(
            @RequestHeader(value = "sessionId", required = false) String sessionId,
            @RequestBody ReadPointsRequest request) throws SessionTimeoutException {
        log.info(" === readPoints({},{},{}) ===", request.getUserLongitude(), request.getUserLatitude());
        UserEntity currentUser = sessionService.validateSession(sessionId);
        List<PointModel> points = converter.convertPoints(pointService.readByUserEmailAndPointStatus(currentUser.getEmail(), PointStatus.CREATED));
        log.info("Read " + points.size() + " points successfully!");
        return new ReadPointsResponse(Status.OK, "Read " + points.size() + " points successfully!", points);
    }

    @ApiOperation(value = "Read all user points")
    @RequestMapping(value = "/points", method = RequestMethod.POST, produces = "application/json")
    public ReadPointsResponse readUserPoints(
            @RequestHeader(value = "sessionId", required = false) String sessionId,
            @RequestBody ReadPointsRequest request) throws SessionTimeoutException {
        log.info(" === readPoints({},{},{}) ===",  request.getUserLongitude(), request.getUserLatitude());
        UserEntity currentUser = sessionService.validateSession(sessionId);
        List<PointModel> points = converter.convertPoints(pointService.readByUserEmail(currentUser.getEmail()));
        log.info("Read " + points.size() + " points successfully!");
        return new ReadPointsResponse(Status.OK, "Read " + points.size() + " points successfully!", points);
    }
}
