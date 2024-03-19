package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import net.dynu.wpeckers.walktraveler.database.repository.PointRepository;
import net.dynu.wpeckers.walktraveler.rest.messaging.point.PointModel;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {

    private final long EARTH_RADIUS_IN_METERS = 6371000;
    private final PointRepository pointRepository;
    private Random random = new Random();

    public Long create(PointEntity point) {
        point.setCreatedDate(new Date());
        point.setModifiedDate(new Date());
        PointEntity saved = pointRepository.save(point);
        log.info("Created point {} {} {} for user {}", saved.getPointId(), saved.getLatitude(), saved.getLongitude(), saved.getUser().getEmail());
        point.setPointId(saved.getPointId());
        return saved.getPointId();
    }

    public PointEntity read(Long id) {
        return pointRepository.findById(id).get();
    }

    public List<PointEntity> readByUserEmail(String email) {
        return pointRepository.findByUserEmail(email);
    }

    public List<PointEntity> readByUserEmailAndPointStatus(String email, PointStatus pointStatus) {
        return pointRepository.findByUserEmailAndPointStatus(email, pointStatus);
    }

    public List<PointEntity> filterOnlyLatestDeleted(List<PointEntity> points) {
        List<PointEntity> result = new LinkedList<>();
        List<PointEntity> deletedPoints = new LinkedList<>();
        for (PointEntity point : points) {
            if (point.getPointStatus().equals(PointStatus.DELETED)) {
                if (point.getDeletedDate() != null) {
                    deletedPoints.add(point);
                } else {
                    log.warn("Skip point {} because deleted date is {}", point.getPointId(), point.getDeletedDate());
                }
            } else {
                result.add(point);
            }
        }
        Collections.sort(deletedPoints, new Comparator<PointEntity>() {
            @Override
            public int compare(PointEntity o1, PointEntity o2) {
                return o1.getDeletedDate().compareTo(o2.getDeletedDate());
            }
        });

        int i = 0;
        for (PointEntity point : deletedPoints) {
            result.add(point);
            if (i > 6) {
                break;
            }
            i++;
        }
        return result;

    }

    public List<PointEntity> readLatestPointsForUser(String email) {
        List<PointEntity> latestPoints = pointRepository.findByUserEmailAndCreatedDateAfter(email, new Date(System.currentTimeMillis()-60*60*1000));
        return this.filterOnlyLatestDeleted(latestPoints);
    }

    public Iterable<PointEntity> readAll() {
        return pointRepository.findAll();
    }

    public void update(PointEntity point) {
        pointRepository.save(point);
    }

    public void delete(PointEntity point) {
        point.setDeletedDate(new Date());
        point.setPointStatus(PointStatus.DELETED);
        pointRepository.save(point);
    }

    public List<PointEntity> collectPoints(String userEmail, String longitude, String latitude) {
        log.debug("____collectPoints____");
        List<PointEntity> collectedPoints = new LinkedList<>();
        List<PointEntity> userActivePoints = this.readByUserEmailAndPointStatus(userEmail, PointStatus.CREATED);
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
                PointEntity saved = pointRepository.save(point);
                collectedPoints.add(saved);
                log.info("User {} collected point {} with title {}", userEmail, point.getPointId(), point.getTitle());
            }
        }
        return collectedPoints;
    }
}
