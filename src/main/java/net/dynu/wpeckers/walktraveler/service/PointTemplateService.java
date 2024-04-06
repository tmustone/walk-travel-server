package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.PointTemplateEntity;
import net.dynu.wpeckers.walktraveler.database.repository.PointTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointTemplateService {

    private final PointTemplateRepository pointTemplateRepository;

    public PointTemplateEntity create(PointTemplateEntity pointTemplate) {
        pointTemplate.setCreatedDate(new Date());
        pointTemplate.setModifiedDate(new Date());
        PointTemplateEntity saved = pointTemplateRepository.save(pointTemplate);
        log.info("Created point template {} {} {} {} {}", saved.getPointTemplateId(), saved.getTitle(), saved.getDescription(), saved.getWeight(), saved.getColorCode());
        return saved;
    }

    public PointTemplateEntity read(Long id) {
        return pointTemplateRepository.findById(id).get();
    }

    public List<PointTemplateEntity> readAll() {
        List<PointTemplateEntity> result = new LinkedList<>();
        Iterator<PointTemplateEntity> iterator = pointTemplateRepository.findAll().iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    public PointTemplateEntity update(PointTemplateEntity pointTemplate) {
        return pointTemplateRepository.save(pointTemplate);
    }
}
