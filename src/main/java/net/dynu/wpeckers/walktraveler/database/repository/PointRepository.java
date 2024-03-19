package net.dynu.wpeckers.walktraveler.database.repository;

import net.dynu.wpeckers.walktraveler.database.model.PointEntity;
import net.dynu.wpeckers.walktraveler.database.model.PointStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface PointRepository extends CrudRepository<PointEntity, Long> {

    List<PointEntity> findByUserEmail(String email);
    List<PointEntity> findByUserEmailAndPointStatus(String email, PointStatus pointStatus);
    List<PointEntity> findByUserEmailAndCreatedDateAfter(String email, Date after);
}
