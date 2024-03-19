package net.dynu.wpeckers.walktraveler.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.service.PointService;
import net.dynu.wpeckers.walktraveler.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
@Profile({ "default", "int", "sys"})
@Slf4j
@RequiredArgsConstructor
public class TestingComponent {

    private final PointService pointService;
    private final UserService userService;

    @PostConstruct
    public void postConstruct() {
        UserEntity user = new UserEntity();
        user.setEmail("tommi_mustonen@hotmail.com");
        user.setRegisterDate(new Date());
        user.setLatitude("60.2517865");
        user.setLongitude("25.0999617");
        Long userId = this.userService.create(user);
        log.info("Created test user with ID {}" , userId);

        PointEntity point = new PointEntity();
        point.setLatitude("60.2517527");
        point.setLongitude("25.0997910999");
        point.setTitle("title");
        point.setDescription("description");
        point.setPointStatus(PointStatus.CREATED);
        point.setUser(user);
        Long pointId = pointService.create(point);
        log.info("Created test point with ID {}", pointId);
    }

}
