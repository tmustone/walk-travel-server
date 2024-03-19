package net.dynu.wpeckers.walktraveler.controller;

import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.rest.enums.Status;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.*;
import org.junit.Assert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ControllerTestBase {

    protected String email = "tommi_mustonen@hotmail.com";
    protected String password = "tommi";
    protected String name = "Tommi Mustonen";

    protected Map<String,String> emailToSessionIdMap = new HashMap<>();

    protected String url(String path, int port) {
        return "http://localhost:" + port + path;
    }

    protected MultiValueMap<String,String> getHeaders(String sessionId) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("sessionId", Arrays.asList(sessionId));
        return new MultiValueMapAdapter<>(headers);
    }

    protected String getSessionId(TestRestTemplate restTemplate, int port) {
        ResponseEntity<ReadSessionResponse> response0 = restTemplate.exchange(url("session/get", port), HttpMethod.GET, HttpEntity.EMPTY, ReadSessionResponse.class);
        String sessionId = response0.getBody().getSessionId();
        log.info("Session ID : " + sessionId);
        return sessionId;
    }

    protected String login(TestRestTemplate restTemplate, int port, String email, String password) {
        if (!emailToSessionIdMap.containsKey(email)) {
            String sessionId = this.getSessionId(restTemplate, port);
            LoginUserRequest request3 = new LoginUserRequest();
            request3.setSessionId("session-id");
            ResponseEntity<LoginUserResponse> response3 = restTemplate.exchange(url("/users/login", port), HttpMethod.POST, new HttpEntity(request3, getHeaders(sessionId)), LoginUserResponse.class);
            log.info("Login Response : " + response3);
            Assert.assertEquals("Login for new user should always work!", Status.OK, response3.getBody().getStatus());
            emailToSessionIdMap.put(email, sessionId);
            return sessionId;
        } else {
            String sessionId = emailToSessionIdMap.get(email);
            log.info("USING EXISTING SESSION " + sessionId);
            return sessionId;
        }
    }
}
