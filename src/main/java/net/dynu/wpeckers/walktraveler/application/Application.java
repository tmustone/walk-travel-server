package net.dynu.wpeckers.walktraveler.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
// Followings are required because application is in different package path
@ComponentScan({"net.dynu.wpeckers.walktraveler"})
@EntityScan("net.dynu.wpeckers.walktraveler.database.model")
@EnableJpaRepositories("net.dynu.wpeckers.walktraveler.database.repository")
@EnableScheduling
@Configuration
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
