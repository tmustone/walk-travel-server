package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.database.model.PointTemplateEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
@EnableAsync
@RequiredArgsConstructor
public class GameService {

    private final int MIN_AGE_SECONDS = 10;
    private final int MAX_AGE_SECONDS = 3*60;

    private final SessionService sessionService;
    private final PointService pointService;
    private final PointTemplateService pointTemplateService;

    private Random random = new Random();
    private long lastShownSessions;


    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void runGame() {
        Date now = new Date();
        int pointsOnline = 5;
        List<UserEntity> onlineUsers = sessionService.getLoggedInUsers();
        if (onlineUsers.size() == 0) {
            return;
        }

        if (lastShownSessions < System.currentTimeMillis()) {
            List<UserEntity> users = sessionService.getLoggedInUsers();
            log.info(" === ACTIVE SESSIONS === ");
            for (UserEntity user : users) {
                log.info("\t{} {} {} {} {}", user.getUserId(), user.getEmail(), user.getLatitude(), user.getLongitude(), user.getLastLoginDate());
            }
            this.lastShownSessions = System.currentTimeMillis() + 30*1000;
        }

        for (UserEntity user : onlineUsers) {
            long onlineSeconds = user.getLastLoginDate() == null ? -1 : (System.currentTimeMillis() - user.getLastLoginDate().getTime())/1000;
            log.debug("Online user : email={}, onlineSeconds={}" , user.getEmail(), onlineSeconds);
            List<PointEntity> points =  pointService.readByUserEmailAndPointStatus(user.getEmail(), PointStatus.CREATED);
            Iterator<PointEntity> iterator = points.iterator();
            while (iterator.hasNext()) {
                PointEntity point = iterator.next();
                if (point.getTerminationDate() == null || now.after(point.getTerminationDate())) {
                    log.info("Delete terminated point {} from user {}. Termination date was {}", point.getPointId(), user.getEmail(), point.getTerminationDate());
                    pointService.delete(point);
                    iterator.remove();
                }
            }
            if (onlineSeconds > 6 && points.size() < pointsOnline) {
                if (user.getLatitude() == null  || user.getLongitude() == null) {
                    log.warn("User {} latitude {} or longitude {} is null! Cannot create points to this user!", user.getEmail(), user.getLatitude(), user.getLongitude());
                    break;
                }
                for (int i = points.size(); i <= pointsOnline; i++) {
                    log.info("Create 1 new point to {} {}", user.getLatitude(), user.getLongitude());
                    int terminationTimeInSeconds = MIN_AGE_SECONDS + random.nextInt(MAX_AGE_SECONDS-MIN_AGE_SECONDS);

                    PointEntity point = new PointEntity();
                    point.setTitle("" + terminationTimeInSeconds + " seconds point");
                    point.setDescription("Description of point");
                    point.setPointStatus(PointStatus.CREATED);
                    point.setTerminationDate(new Date(System.currentTimeMillis() + terminationTimeInSeconds*1000));
                    point.setUser(user);

                    // Create custom variables
                    List<PointTemplateEntity> templates = pointTemplateService.readAll();
                    PointTemplateEntity random = templates.get(this.random.nextInt(templates.size()));
                    point.setTitle(random.getTitle());
                    point.setDescription(random.getDescription());
                    point.setWeight(random.getWeight());
                    point.setColorCode(random.getColorCode());

                    // Set random coordinates
                    Float latitude = Float.valueOf(user.getLatitude());
                    Float longitude = Float.valueOf(user.getLongitude());
                    point.setLatitude("" + (latitude.floatValue() + getRandomChange()));
                    point.setLongitude("" + (longitude.floatValue() + getRandomChange()));

                    pointService.create(point);
                }
            }
        }
    }

    public int getNextInt() {
        return this.random.nextInt(24);
    }
    public float getRandomChange() {
        return  (((float)(getNextInt()-12)) / 10000);
    }

    public int getRandomWeight() {
        return this.random.nextInt(5)*10;
    }
}
