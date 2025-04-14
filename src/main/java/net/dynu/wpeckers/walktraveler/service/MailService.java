package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;
    private final Environment environment;

    public void sendMail(String to, String subject, String message, boolean useHTML) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("info@wpeckers.dynu.net");
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message);
        mail.setSentDate(new Date());
        emailSender.send(mail);
    }

    private String getEnv() {
        String env = Arrays.asList(this.environment.getActiveProfiles()).toString().toUpperCase();
        return env;
    }

    public void sendWelcomeMail(String to, String loginSecret, String baseUrl) {

        String title = "Welcome to Walktraveller";
        String message = "Welcome to Walktraveller\n\n";
        message += "Welcome to Walktraveller. Use following link to login\n\n";
        message += "" + baseUrl + "/?s=" + loginSecret + "\n\n";
        message += "Have fun!\n\n";
        this.sendMail(to, title, message, true);
    }
}
