package net.dynu.wpeckers.walktraveler.tests;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.service.PointService;
import net.dynu.wpeckers.walktraveler.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class RepositoryTestInterfaceImpl extends IntegrationTestInterface {

    private PointService pointService;
    private UserService userService;

    @Override
    public Long createPoint(PointEntity point) {
        return pointService.create(point);
    }

    @Override
    public PointEntity readPoint(Long ticketId) {
        return pointService.read(ticketId);
    }

    @Override
    public List<PointEntity> readPoints() {
        List<PointEntity> result = new ArrayList<>();
        pointService.readAll().forEach(result::add);
        return result;
    }

    @Override
    public Long createUser(UserEntity user) {
        return userService.create(user);
    }

}
