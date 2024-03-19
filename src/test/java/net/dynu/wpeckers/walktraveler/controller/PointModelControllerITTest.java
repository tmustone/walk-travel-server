package net.dynu.wpeckers.walktraveler.controller;

import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.ReadPointResponse;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.ReadPointsRequest;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.ReadPointsResponse;
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
public class PointModelControllerITTest extends ControllerTestBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void readPoint() {
        String sessionId = this.login(restTemplate, port, email, password);
        ResponseEntity<ReadPointResponse> response = restTemplate.exchange(url("/points/point/0", port), HttpMethod.POST, new HttpEntity(getHeaders(sessionId)), ReadPointResponse.class);
        log.info("Response : " + response);
    }

    @Test
    public void readPoints() {
        String sessionId = this.login(restTemplate, port, email, password);
        ReadPointsRequest request = new ReadPointsRequest();
        request.setUserLatitude("latitude");
        request.setUserLongitude("longitude");
        ResponseEntity<ReadPointsResponse> response = restTemplate.exchange(url("/points/points", port), HttpMethod.POST, new HttpEntity(request, getHeaders(sessionId)), ReadPointsResponse.class);
        log.info("Response : " + response);
    }

    @Test
    public void readActivePoints() {
        String sessionId = this.login(restTemplate, port, email, password);
        ReadPointsRequest request = new ReadPointsRequest();
        request.setUserLatitude("latitude");
        request.setUserLongitude("longitude");
        ResponseEntity<ReadPointsResponse> response = restTemplate.exchange(url("/points/activepoints", port), HttpMethod.POST, new HttpEntity(request, getHeaders(sessionId)), ReadPointsResponse.class);
        log.info("Response : " + response);
    }
}