package net.dynu.wpeckers.walktraveler.configuration;

import net.duny.wpeckers.authentication.client.AuthenticationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationConfiguration {

    public static final String SERVICE_NAME = "walktraveller-int.wpeckers.dynu.net";

    @Bean
    public AuthenticationClient getAuthenticationClient() {
        return new AuthenticationClient(AuthenticationClient.BASE_URL_INT);
    }

    public String getServiceName() {
        return SERVICE_NAME;
    }

}
