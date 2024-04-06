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

    private final PointRepository pointRepository;

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
        List<PointEntity> collectedPoints = new LinkedList<>();
        for (PointEntity point : points) {
            if (point.getPointStatus().equals(PointStatus.DELETED)) {
                if (point.getDeletedDate() != null) {
                    deletedPoints.add(point);
                } else {
                    log.warn("Skip point {} because deleted date is {}", point.getPointId(), point.getDeletedDate());
                }
            } else if (point.getPointStatus().equals(PointStatus.COLLECTED)) {
                if (point.getCollectedDate() != null) {
                    collectedPoints.add(point);
                } else {
                    log.warn("Skip point {} because collected date is {}", point.getPointId(), point.getDeletedDate());
                }
            } else {
                result.add(point);
            }
        }
        Collections.sort(deletedPoints, Comparator.comparing(PointEntity::getDeletedDate));
        Collections.sort(collectedPoints, Comparator.comparing(PointEntity::getCollectedDate));
        result.addAll(collectedPoints.subList(0,Math.min(collectedPoints.size(), 6)));
        result.addAll(deletedPoints.subList(0,Math.min(deletedPoints.size(), 5)));
        /*
        int i = 0;
        for (PointEntity point : deletedPoints) {
            result.add(point);
            if (i > 6) {
                break;
            }
            i++;
        }
        */
        return result;

    }

    public List<PointEntity> readLatestPointsForUser(String email) {
        List<PointEntity> latestPoints = pointRepository.findByUserEmailAndCreatedDateAfter(email, new Date(System.currentTimeMillis()-60*60*1000));
        return this.filterOnlyLatestDeleted(latestPoints);
    }

    public Iterable<PointEntity> readAll() {
        return pointRepository.findAll();
    }

    public PointEntity update(PointEntity point) {
        return pointRepository.save(point);
    }

    public void delete(PointEntity point) {
        point.setDeletedDate(new Date());
        point.setPointStatus(PointStatus.DELETED);
        pointRepository.save(point);
    }
}
