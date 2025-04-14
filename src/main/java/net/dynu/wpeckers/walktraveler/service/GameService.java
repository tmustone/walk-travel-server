package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.configuration.GameConfiguration;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.database.model.PointTemplateEntity;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.rest.messaging.user.UserModel;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@EnableAsync
@RequiredArgsConstructor
public class GameService {

    private final long EARTH_RADIUS_IN_METERS = 6371000;

    private final SessionService sessionService;
    private final PointService pointService;
    private final PointTemplateService pointTemplateService;
    private final UserService userService;
    private final CacheService cacheService;
    private final GameConfiguration gameConfiguration;

    private final Random random = new Random();


    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void runGame() {

        // Read all online users
        Collection<UserEntity> onlineUsers = sessionService.getLoggedInUsers().values();
        if (onlineUsers.isEmpty()) {
            return;
        }

        long startTime = System.currentTimeMillis();

        // Print active sessions
        printActiveSessions();

        for (UserEntity user : onlineUsers) {

            // Check when user last collected something
            if (cacheService.getUserLastCollectTime(user.getEmail()) + 30*60*1000 < System.currentTimeMillis()) {
                cacheService.resetUserLastCollectData(user.getEmail());
            }

            // Read and remove old active points from user
            List<PointEntity> activePoints = this.readAndFilterOldActivePoints(user);

            // Create active points
            this.createNewActivePoints(user, activePoints);

        }

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("Game service run took {} ms", totalTime);
    }

    private void printActiveSessions() {
        if (cacheService.getNextShownSessionTime() < System.currentTimeMillis()) {
            Map<String, UserEntity> users = sessionService.getLoggedInUsers();
            log.info(" === ACTIVE SESSIONS === ");

            for (String sessionId : users.keySet()) {
                UserEntity user = users.get(sessionId);
                Long sessionTimeout = sessionService.getSessionExpirationTime(sessionId);
                log.info("\t{} ==> {} {} {} {} {} sessionTimeout={}", sessionId, user.getUserId(), user.getEmail(), user.getLatitude(), user.getLongitude(), user.getLastLoginDate(), sessionTimeout == null ? "expired" : ((System.currentTimeMillis()-sessionTimeout) / 1000) + " seconds");
            }
            cacheService.setNextShowSessionTime(System.currentTimeMillis() + 30*1000);
        }
    }

    private void createNewActivePoints(UserEntity user, List<PointEntity> activePoints) {
        int pointsOnline = 5;
        long onlineSeconds = user.getLastLoginDate() == null ? -1 : (System.currentTimeMillis() - user.getLastLoginDate().getTime())/1000;
        log.debug("Online user : email={}, onlineSeconds={}" , user.getEmail(), onlineSeconds);

        long lastCollectTime = cacheService.getUserLastCollectTime(user.getEmail());
        int collectCount = cacheService.getUserCollectCount(user.getEmail());
        log.debug("CREATE POINT STATUS : onlineSeconds={}, activePointCount={}, collectCount={}, lastCollectTime={}", onlineSeconds, activePoints.size(), collectCount, new Date(lastCollectTime));
        if (onlineSeconds > 6 && activePoints.size() < pointsOnline) {
            long nextPointCreationTime = lastCollectTime + (collectCount * 1000L*gameConfiguration.getPointRespawnDelaySeconds());
            boolean timeToCreateNewPoint  = nextPointCreationTime < System.currentTimeMillis();
            if (!timeToCreateNewPoint) {
                long secondsLeft = (nextPointCreationTime - System.currentTimeMillis())/1000;
                log.info("It is not time to create new points yet (wait {} seconds)! Collected {} points and last was in {}", secondsLeft, collectCount, new Date(lastCollectTime));
            } else if (user.getLatitude() == null || user.getLongitude() == null) {
                    log.warn("User {} latitude {} or longitude {} is null! Cannot create points to this user!", user.getEmail(), user.getLatitude(), user.getLongitude());
            } else {
                for (int i = activePoints.size(); i <= pointsOnline; i++) {
                    log.info("Create 1 new point to {} {}", user.getLatitude(), user.getLongitude());
                    int terminationTimeInSeconds = gameConfiguration.getPointMinAgeSeconds() + random.nextInt(gameConfiguration.getPointMaxAgeSeconds() - gameConfiguration.getPointMinAgeSeconds());

                    PointEntity point = new PointEntity();
                    point.setPointStatus(PointStatus.CREATED);
                    point.setTerminationDate(new Date(System.currentTimeMillis() + terminationTimeInSeconds * 1000L));
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
    private List<PointEntity> readAndFilterOldActivePoints(UserEntity user) {
        Date now = new Date();
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
        return activePoints;
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
                cacheService.addUserCollectCount(user.getEmail());
            }
        }
        return collectedPoints;
    }

    public List<UserModel> populateCollectCounts(List<UserModel> users) {
        List<UserModel> result = new LinkedList<>();
        for (UserModel userModel : users) {
            userModel.setCurrentCollectCount(cacheService.getUserCollectCount(userModel.getEmail()));
            userModel.setTotalCollectCount(cacheService.getUserCollectCount(userModel.getEmail()));
            result.add(userModel);
        }
        return result;
    }

    public void updateUserPosition(UserEntity user, String sessionId) {
        userService.update(user);
        sessionService.updateUser(sessionId, user);
    }
}
