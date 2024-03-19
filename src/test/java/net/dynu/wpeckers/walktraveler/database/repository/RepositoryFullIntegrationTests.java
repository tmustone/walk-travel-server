package net.dynu.wpeckers.walktraveler.database.repository;

import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.service.PointService;
import net.dynu.wpeckers.walktraveler.service.UserService;
import net.dynu.wpeckers.walktraveler.tests.FullIntegrationTests;
import net.dynu.wpeckers.walktraveler.tests.RepositoryTestInterfaceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes={net.dynu.wpeckers.walktraveler.application.Application.class})
@Slf4j
public class RepositoryFullIntegrationTests {

    @Autowired
    private PointService pointService;
    @Autowired
    private UserService userService;

    @Test
    public void test() {
        RepositoryTestInterfaceImpl repositoryTestInterface = new RepositoryTestInterfaceImpl(pointService, userService);
        FullIntegrationTests fullIntegrationTests = new FullIntegrationTests(repositoryTestInterface);
        fullIntegrationTests.test();
    }
}
