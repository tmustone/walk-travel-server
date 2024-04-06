package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.database.model.PointTemplateEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UserModel;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@Slf4j
@EnableAsync
@RequiredArgsConstructor
public class GameService {

    private final int MIN_AGE_SECONDS = 3*60;
    private final int MAX_AGE_SECONDS = 10*60;

    private final long EARTH_RADIUS_IN_METERS = 6371000;

    private final SessionService sessionService;
    private final PointService pointService;
    private final PointTemplateService pointTemplateService;

    private Random random = new Random();
    private long lastShownSessions;

    private static final Map<String,Long> userIdToLastCollectTimeMap = new HashMap<>();
    private static final Map<String,Integer> userIdToCollectCountMap = new HashMap<>();

    private int getUserCollectCount(String email) {
        Integer collectCount = userIdToCollectCountMap.get(email);
        log.info("getUserCollectCount({})={}", email, collectCount);
        return collectCount == null ? 0 : collectCount.intValue();
    }

    private void addCollectCount(String email) {
        int newCount = getUserCollectCount(email) + 1;
        log.info("Update user {} collect count to {}", email, newCount);
        userIdToCollectCountMap.put(email, newCount);
        userIdToLastCollectTimeMap.put(email, System.currentTimeMillis());
    }

    private long getUserLastCollectTime(String email) {
        Long lastCollectTime = userIdToLastCollectTimeMap.get(email);
        return lastCollectTime == null ? 0 : lastCollectTime;
    }

    private void resetUserLastCollectData(String email) {
        this.userIdToLastCollectTimeMap.put(email, 0L);
        this.userIdToCollectCountMap.put(email, 0);
        log.info("RESET USER LAST COLLECT DATA : {}", email);
    }

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
            List<PointEntity> activePoints =  pointService.readByUserEmailAndPointStatus(user.getEmail(), PointStatus.CREATED);
            Iterator<PointEntity> iterator = activePoints.iterator();
            while (iterator.hasNext()) {
                PointEntity point = iterator.next();
                if (point.getTerminationDate() == null || now.after(point.getTerminationDate())) {
                    log.info("Delete terminated point {} from user {}. Termination date was {}", point.getPointId(), user.getEmail(), point.getTerminationDate());
                    pointService.delete(point);
                    iterator.remove();
                }
            }

            // Check when user last collected something
            if (getUserLastCollectTime(user.getEmail()) + 30*60*1000 < System.currentTimeMillis()) {
                this.resetUserLastCollectData(user.getEmail());
            }

            long lastCollectTime = getUserLastCollectTime(user.getEmail());
            int collectCount = getUserCollectCount(user.getEmail());
            long nextPointCreationTime = lastCollectTime + (collectCount * 1000*60);
            boolean timeToCreateNewPoint  =  nextPointCreationTime < System.currentTimeMillis() ? true : false;
            log.info("CREATE POINT STATUS : onlineSeconds={}, activePointCount={}, collectCount={}, lastCollectTime={}", onlineSeconds, activePoints.size(), collectCount, new Date(lastCollectTime));
            if (onlineSeconds > 6 && activePoints.size() < pointsOnline) {
                if (timeToCreateNewPoint == false) {
                    long secondsLeft = (nextPointCreationTime - System.currentTimeMillis())/1000;
                    log.info("It is not time to create new points yet (wait {} seconds)! Collected {} points and last was in {}", secondsLeft, collectCount, new Date(lastCollectTime));
                } else {
                    if (user.getLatitude() == null || user.getLongitude() == null) {
                        log.warn("User {} latitude {} or longitude {} is null! Cannot create points to this user!", user.getEmail(), user.getLatitude(), user.getLongitude());
                        break;
                    }
                    for (int i = activePoints.size(); i <= pointsOnline; i++) {
                        log.info("Create 1 new point to {} {}", user.getLatitude(), user.getLongitude());
                        int terminationTimeInSeconds = MIN_AGE_SECONDS + random.nextInt(MAX_AGE_SECONDS - MIN_AGE_SECONDS);

                        PointEntity point = new PointEntity();
                        point.setPointStatus(PointStatus.CREATED);
                        point.setTerminationDate(new Date(System.currentTimeMillis() + terminationTimeInSeconds * 1000));
                        point.setUser(user);

                        // Create custom variables
                        List<PointTemplateEntity> templates = pointTemplateService.readAll();
                        PointTemplateEntity random = templates.get(this.random.nextInt(templates.size()));
                        point.setTitle(random.getTitle());
                        point.setDescription(random.getDescription());
                        point.setWeight(getRandomWeight());
                        point.setColorCode(random.getColorCode());
                        point.setTotalAgeSeconds(terminationTimeInSeconds);

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

    public List<PointEntity> collectPoints(UserEntity user, String longitude, String latitude) {
        log.debug("____collectPoints____");
        List<PointEntity> collectedPoints = new LinkedList<>();
        List<PointEntity> userActivePoints = pointService.readByUserEmailAndPointStatus(user.getEmail(), PointStatus.CREATED);
        for (PointEntity point : userActivePoints) {

            double lat1Rad = Math.toRadians(Float.valueOf(latitude));
            double lat2Rad = Math.toRadians(Float.valueOf(point.getLatitude()));
            double lon1Rad = Math.toRadians(Float.valueOf(longitude));
            double lon2Rad = Math.toRadians(Float.valueOf(point.getLongitude()));

            double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
            double y = (lat2Rad - lat1Rad);
            double distance = Math.sqrt(x * x + y * y) * EARTH_RADIUS_IN_METERS;

            log.debug("\t point : {} {} {} {} {}", point.getPointId(), point.getTitle(), point.getLongitude(), point.getLatitude(), distance);
            if (distance < 25) {
                point.setPointStatus(PointStatus.COLLECTED);
                point.setCollectedDate(new Date());
                point = pointService.update(point);
                collectedPoints.add(point);
                log.info("User {} collected point {} with title {}", user.getEmail(), point.getPointId(), point.getTitle());
                addCollectCount(user.getEmail());
            }
        }
        return collectedPoints;
    }

    public List<UserModel> populateCollectCounts(List<UserModel> users) {
        List<UserModel> result = new LinkedList<>();
        for (UserModel userModel : users) {
            userModel.setCurrentCollectCount(this.getUserCollectCount(userModel.getEmail()));
            userModel.setTotalCollectCount(this.getUserCollectCount(userModel.getEmail()));
            result.add(userModel);
        }
        return result;
    }
}
