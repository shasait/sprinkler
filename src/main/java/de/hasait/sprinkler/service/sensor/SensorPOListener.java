/*
 * Copyright (C) 2024 by Sebastian Hasait (sebastian at hasait dot de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
