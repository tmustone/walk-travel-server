package net.dynu.wpeckers.walktraveler.controller;

import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.ReadUsersResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes={net.dynu.wpeckers.walktraveler.application.Application.class})
@Slf4j
public class UserControllerITTest extends ControllerTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testUserLogin() {
        this.login(restTemplate, port, email, password);
    }

    @Test
    public void testReadUsers() {
        String sessionId = this.login(restTemplate, port, email, password);
        ResponseEntity<ReadUsersResponse> response4 = restTemplate.exchange(url("/users/users", port), HttpMethod.GET, new HttpEntity(getHeaders(sessionId)), ReadUsersResponse.class);
        log.info("Read users response : " + response4);
        Assert.assertTrue("Read users should always return more than 0 users!",response4.getBody().getUsers().size() > 0);
    }

}
