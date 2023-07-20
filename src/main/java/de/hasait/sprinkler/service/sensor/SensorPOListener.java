package de.hasait.sprinkler.service.sensor;

import de.hasait.sprinkler.domain.sensor.SensorPO;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SensorPOListener {

    private static final Logger LOG = LoggerFactory.getLogger(SensorPOListener.class);

    static SensorTaskService sensorTaskService;

    @PostPersist
    public void postPersistSchedulePO(SensorPO po) {
        LOG.debug("postPersistSchedulePO: {}", po);

        sensorTaskService.postPersistSchedulePO(po);
    }

    @PostUpdate
    public void postUpdateSchedulePO(SensorPO po) {
        LOG.debug("postUpdateSchedulePO: {}", po);

        sensorTaskService.postUpdateSchedulePO(po);
    }

    @PreRemove
    public void preRemoveSchedulePO(SensorPO po) {
        LOG.debug("preRemoveSchedulePO: {}", po);

        sensorTaskService.preRemoveSchedulePO(po);
    }

}
