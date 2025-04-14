package net.dynu.wpeckers.walktraveler.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "game")
@Data
public class GameConfiguration {

    private int pointRespawnDelaySeconds;
    private int pointMinAgeSeconds;
    private int pointMaxAgeSeconds;

    @PostConstruct
    public void init() {
        log.info("Game configuration:");
        log.info("pointRespawnDelaySeconds : {}", this.pointRespawnDelaySeconds);
        log.info("pointMinAgeSeconds : {}", this.pointMinAgeSeconds);
        log.info("pointMaxAgeSeconds : {}", this.pointMaxAgeSeconds);
    }

}
