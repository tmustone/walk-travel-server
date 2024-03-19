package net.dynu.wpeckers.walktraveler.tests;

import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.*;
import org.junit.Assert;

import java.util.List;

@Slf4j
public class FullIntegrationTests {

    private IntegrationTestInterface integrationTestInterface;

    public FullIntegrationTests(IntegrationTestInterface integrationTestInterface) {
        this.integrationTestInterface = integrationTestInterface;
    }

    public void test() {
        UserEntity user = new UserEntity();
        user.setEmail("tommi_mustonen@hotmail.com");
        Long userId = integrationTestInterface.createUser(user);
        log.info("Created user : " + userId);
        Assert.assertTrue("Returned id should be greater then zero", userId != null && userId > 0);

        List<PointEntity> points = integrationTestInterface.readPoints();
        Assert.assertNotNull("Returned point list should not be null", points);
/*
        Point point = new Point();
        point.setTitle("My first ticket");
        point.setDescription("Description for my first ticket!");
        point.setCreatedDate(new Date());
        point.setModifiedDate(new Date());
        point.setCollectedDate(new Date());
        point.setLongitude("longitude");
        point.setLatitude("latitude");
        point.setUser(user);
        Long ticketId = integrationTestInterface.createPoint(point);
        log.info("Created ticket : "+ ticketId);
        Assert.assertTrue("Returned id should be greater then zero", ticketId != null && ticketId.longValue() > 0);
*/

    }
}
