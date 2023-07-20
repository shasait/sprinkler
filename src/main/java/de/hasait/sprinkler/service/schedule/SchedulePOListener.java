package de.hasait.sprinkler.service.schedule;

import de.hasait.sprinkler.domain.schedule.SchedulePO;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SchedulePOListener {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulePOListener.class);

    static ScheduleTaskService scheduleTaskService;

    @PostPersist
    public void postPersistSchedulePO(SchedulePO po) {
        LOG.debug("postPersistSchedulePO: {}", po);

        scheduleTaskService.postPersistSchedulePO(po);
    }

    @PostUpdate
    public void postUpdateSchedulePO(SchedulePO po) {
        LOG.debug("postUpdateSchedulePO: {}", po);

        scheduleTaskService.postUpdateSchedulePO(po);
    }

    @PreRemove
    public void preRemoveSchedulePO(SchedulePO po) {
        LOG.debug("preRemoveSchedulePO: {}", po);

        scheduleTaskService.preRemoveSchedulePO(po);
    }

}
