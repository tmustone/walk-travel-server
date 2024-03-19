package net.dynu.wpeckers.walktraveler.controller;

import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.tests.ControllerTestInterfaceImpl;
import net.dynu.wpeckers.walktraveler.tests.FullIntegrationTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes={net.dynu.wpeckers.walktraveler.application.Application.class})
@Slf4j
public class ControllerFullIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test() {
        ControllerTestInterfaceImpl repositoryTestInterface = new ControllerTestInterfaceImpl(port, restTemplate);
        FullIntegrationTests test = new FullIntegrationTests(repositoryTestInterface);
        test.test();
    }

}
