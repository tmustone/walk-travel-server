package net.dynu.wpeckers.walktraveler.tests;

import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;

import java.util.List;

public abstract class IntegrationTestInterface {

    public abstract Long createPoint(PointEntity point);
    public abstract PointEntity readPoint(Long pointId);
    public abstract List<PointEntity> readPoints();
    public abstract Long createUser(UserEntity user);
}
