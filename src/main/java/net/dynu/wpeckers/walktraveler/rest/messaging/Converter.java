package net.dynu.wpeckers.walktraveler.rest.messaging;

import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointTemplateEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.PointModel;
import net.dynu.wpeckers.walktraveler.rest.messaging.pointtemplate.PointTemplateModel;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UserModel;

import java.util.LinkedList;
import java.util.List;

public class Converter {
    public UserModel convert(UserEntity user) {
        if (user == null) {
            return null;
        }
        UserModel u = new UserModel();
        u.setEmail(user.getEmail());
        u.setLastLoginDate(user.getLastLoginDate());
        u.setRegisterDate(user.getRegisterDate());
        u.setLatitude(user.getLatitude());
        u.setLongitude(user.getLongitude());
        u.setFastLoginSecret(user.getFastLoginSecret());
        u.setFastLoginSecretDate(user.getFastLoginSecretDate());
        return u;
    }

    public List<UserModel> convertUsers(List<UserEntity> users) {
        List<UserModel> userList = new LinkedList<>();
        if (users != null) {
            for (UserEntity user : users) {
                userList.add(convert(user));
            }
        }
        return userList;
    }

    public PointModel convert(PointEntity point) {
        if (point == null) {
            return null;
        }
        PointModel p = new PointModel();
        p.setPointId(point.getPointId());
        p.setTitle(point.getTitle());
        p.setDescription(point.getDescription());
        p.setPointStatus(point.getPointStatus());
        p.setLongitude(point.getLongitude());
        p.setLatitude(point.getLatitude());
        p.setTerminationDate(point.getTerminationDate());
        p.setWeight(point.getWeight());
        p.setColorCode(point.getColorCode());
        p.setTotalAgeSeconds(point.getTotalAgeSeconds());
        return p;
    }

    public List<PointModel> convertPoints(List<PointEntity> points) {
        if (points == null) {
            return null;
        }
        List<PointModel> list = new LinkedList<PointModel>();
        for (PointEntity p : points) {
            list.add(convert(p));
        }
        return list;
    }

    public PointTemplateModel convert(PointTemplateEntity pointTemplate) {
        if (pointTemplate == null) {
            return null;
        }
        PointTemplateModel m = new PointTemplateModel();
        m.setPointTemplateId(pointTemplate.getPointTemplateId());
        m.setTitle(pointTemplate.getTitle());
        m.setDescription(pointTemplate.getDescription());
        m.setWeight(pointTemplate.getWeight());
        m.setColorCode(pointTemplate.getColorCode());
        return m;
    }

    public PointTemplateEntity convert(PointTemplateModel pointTemplate) {
        if (pointTemplate == null) {
            return null;
        }
        PointTemplateEntity e = new PointTemplateEntity();
        e.setPointTemplateId(pointTemplate.getPointTemplateId());
        e.setTitle(pointTemplate.getTitle());
        e.setDescription(pointTemplate.getDescription());
        e.setWeight(pointTemplate.getWeight());
        e.setColorCode(pointTemplate.getColorCode());
        return e;
    }

    public List<PointTemplateModel> convertPointTemplates(List<PointTemplateEntity> list) {
        if (list == null) {
            return null;
        }
        List<PointTemplateModel> result = new LinkedList<>();
        for (PointTemplateEntity template : list) {
            result.add(convert(template));
        }
        return result;
    }
}
