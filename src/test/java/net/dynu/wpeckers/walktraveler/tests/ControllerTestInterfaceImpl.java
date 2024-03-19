package net.dynu.wpeckers.walktraveler.tests;

import lombok.AllArgsConstructor;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.filters.RequestResponseLoggingInterceptor;
import org.junit.Before;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

@AllArgsConstructor
public class ControllerTestInterfaceImpl extends IntegrationTestInterface {

    private int port;
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Before
    public void init () {
        restTemplate.getRestTemplate().getInterceptors().add(new RequestResponseLoggingInterceptor());
    }

    @Override
    public Long createPoint(PointEntity point) {
        return 1L;
    }

    @Override
    public PointEntity readPoint(Long pointId) {
        return null;
    }

    @Override
    public List<PointEntity> readPoints() {
        return null;
    }

    @Override
    public Long createUser(UserEntity user) {
        return null;
    }

}
