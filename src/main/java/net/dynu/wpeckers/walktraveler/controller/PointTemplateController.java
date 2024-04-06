package net.dynu.wpeckers.walktraveler.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.common.Common;
import net.dynu.wpeckers.walktraveler.database.model.PointTemplateEntity;
import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.CreatePointTemplateRequest;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.CreatePointTemplateResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.PointTemplateModel;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.ReadPointTemplateResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.ReadPointTemplatesResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.UpdatePointTemplateRequest;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.UpdatePointTemplateResponse;
import net.dynu.wpeckers.walktraveler.service.PointTemplateService;
import net.dynu.wpeckers.walktraveler.service.SessionService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pointtemplates")
@Api(value="Point templates", description="Point template API")
@RequiredArgsConstructor
public class PointTemplateController extends ControllerBase {

    private final SessionService sessionService;
    private final PointTemplateService pointTemplateService;

    @ApiOperation(value = "Read a point template with ID")
    @RequestMapping(value = "/pointtemplate/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ReadPointTemplateResponse readUser(@RequestHeader(value = "sessionId", required = false) String sessionId, @PathVariable String id) throws SessionTimeoutException {
        sessionService.validateSession(sessionId);
        Long pointTemplateId = Common.parseLong(id);
        ReadPointTemplateResponse response = new ReadPointTemplateResponse();
        if (pointTemplateId == null) {
            response.setStatus(Status.ERROR);
            response.setMessage("Invalid point template ID \"" + id + "\"");
        } else {
            PointTemplateModel pointTemplate = converter.convert(pointTemplateService.read(pointTemplateId));
            if (pointTemplate == null) {
                response.setStatus(Status.NOTFOUND);
                response.setMessage("Point template not found with id \"" + id + "\"");
            } else {
                response.setStatus(Status.OK);
                response.setMessage("Read point template \"" + id + "\" successfully!");
                response.setPointTemplate(pointTemplate);
            }
        }
        return response;
    }

    @ApiOperation(value = "Read all point templates")
    @RequestMapping(value = "/pointtemplates", method = RequestMethod.GET)
    public @ResponseBody ReadPointTemplatesResponse readUsers(@RequestHeader(value = "sessionId", required = false) String sessionId) throws SessionTimeoutException {
        sessionService.validateSession(sessionId);
        ReadPointTemplatesResponse response = new ReadPointTemplatesResponse();
        List<PointTemplateModel> pointTemplateModels = new LinkedList<>();
        for (PointTemplateEntity entity : pointTemplateService.readAll()) {
            pointTemplateModels.add(converter.convert(entity));
        }
        response.setPointTemplates(pointTemplateModels);
        response.setMessage("Read " + pointTemplateModels.size() + " point templates successfully!");
        response.setStatus(Status.OK);
        return response;
    }


    @ApiOperation(value = "Create point template")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody CreatePointTemplateResponse readUserInfo(@RequestHeader(value = "sessionId", required = false) String sessionId, @RequestBody CreatePointTemplateRequest request) throws SessionTimeoutException {
        sessionService.validateSession(sessionId);
        PointTemplateEntity entity = pointTemplateService.create(converter.convert(request.getPointTemplate()));
        CreatePointTemplateResponse response = new CreatePointTemplateResponse();
        if (entity == null) {
            response.setMessage("Failed to create point template to server!");
            response.setStatus(Status.ERROR);
        } else {
            response.setPointTemplateModel(converter.convert(entity));
            response.setStatus(Status.OK);
            response.setMessage("Created new point template " + entity.getPointTemplateId() + " successfully!");
        }
        return response;
    }

    @ApiOperation(value = "Update point template")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody UpdatePointTemplateResponse readUserInfo(@RequestHeader(value = "sessionId", required = false) String sessionId, @RequestBody UpdatePointTemplateRequest request) throws SessionTimeoutException {
        sessionService.validateSession(sessionId);
        PointTemplateEntity entity = pointTemplateService.update(converter.convert(request.getPointTemplate()));
        UpdatePointTemplateResponse response = new UpdatePointTemplateResponse();
        if (entity == null) {
            response.setMessage("Failed to update point template to server!");
            response.setStatus(Status.ERROR);
        } else {
            response.setPointTemplateModel(converter.convert(entity));
            response.setStatus(Status.OK);
            response.setMessage("Updated new point template " + entity.getPointTemplateId() + " successfully!");
        }
        return response;
    }
}
