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
