package net.dynu.wpeckers.walktraveler.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.duny.wpeckers.authentication.client.AuthenticationClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "authentication")
@Data
public class AuthenticationConfiguration {

    private String serviceName;
    private String url;

    @PostConstruct
    public void init() {
        log.info("Authentication configuration:");
        log.info("url : {}", this.url);
        log.info("serviceName: {}", this.serviceName);
    }

    @Bean
    public AuthenticationClient getAuthenticationClient() {
        return new AuthenticationClient(this.url);
    }

    public String getServiceName() {
        return this.serviceName;
    }

}
