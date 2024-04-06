package net.dynu.wpeckers.walktraveler.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.database.model.PointTemplateEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.service.PointService;
import net.dynu.wpeckers.walktraveler.service.PointTemplateService;
import net.dynu.wpeckers.walktraveler.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
@Profile({"default"})
@Slf4j
@RequiredArgsConstructor
public class TestingComponent {

    private final PointService pointService;
    private final PointTemplateService pointTemplateService;
    private final UserService userService;

    @PostConstruct
    public void postConstruct() {
        String email = "tommi_mustonen@hotmail.com";
        UserEntity user = this.userService.readByEmail(email);
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setRegisterDate(new Date());
            user.setLatitude("60.2517865");
            user.setLongitude("25.0999617");
            Long userId = this.userService.create(user);
            log.info("Created test user with ID {}", userId);

            PointEntity point = new PointEntity();
            point.setLatitude("60.2517527");
            point.setLongitude("25.0997910999");
            point.setTitle("title");
            point.setDescription("description");
            point.setPointStatus(PointStatus.CREATED);
            point.setUser(user);
            Long pointId = pointService.create(point);
            log.info("Created test point with ID {}", pointId);

            PointTemplateEntity pointTemplate = new PointTemplateEntity();
            pointTemplate.setTitle("VESIMIES");
            pointTemplate.setDescription("VESIMIES on vettä rakastava sininen hahmo");
            pointTemplate.setWeight(20);
            pointTemplate.setColorCode("#0000ff");
            pointTemplateService.create(pointTemplate);

            PointTemplateEntity pointTemplate2 = new PointTemplateEntity();
            pointTemplate2.setTitle("TULIMIES");
            pointTemplate2.setDescription("Tulimies rakastaa lämpöä ja hiiltä");
            pointTemplate2.setWeight(30);
            pointTemplate2.setColorCode("yellow");
            pointTemplateService.create(pointTemplate2);

            PointTemplateEntity pointTemplate3 = new PointTemplateEntity();
            pointTemplate3.setTitle("HAVUMIES");
            pointTemplate3.setDescription("Havumies viihtyy parhaiten kuusi ja mäntymetsissä ja syö havun neulasia");
            pointTemplate3.setWeight(50);
            pointTemplate3.setColorCode("#00ff00");
            pointTemplateService.create(pointTemplate3);

        } else {
            log.info("Test user already exists in database!");
        }
    }
}
