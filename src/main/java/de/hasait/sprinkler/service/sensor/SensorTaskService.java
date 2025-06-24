/*
 * Copyright (C) 2025 by Sebastian Hasait (sebastian at hasait dot de)
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

import de.hasait.common.service.AbstractTaskService;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorRepository;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.domain.sensor.SensorValueRepository;
import de.hasait.sprinkler.service.sensor.provider.SensorProviderService;
import de.hasait.sprinkler.service.sensor.provider.SensorValue;
import de.hasait.sprinkler.service.sensor.publish.SensorValuePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SensorTaskService extends AbstractTaskService<SensorPO, Long, SensorRepository> {

    private static final Logger LOG = LoggerFactory.getLogger(SensorTaskService.class);

    private final SensorValueRepository valueRepository;
    private final SensorProviderService providerService;
    private final SensorValuePublisher sensorValuePublisher;

    public SensorTaskService(SensorRepository repository, TaskScheduler taskScheduler, SensorValueRepository valueRepository, SensorProviderService providerService, SensorValuePublisher sensorValuePublisher) {
        super(SensorPO.class, repository, taskScheduler);

        this.valueRepository = valueRepository;
        this.providerService = providerService;
        this.sensorValuePublisher = sensorValuePublisher;

        SensorPOListener.sensorTaskService = this;
    }

    @Override
    protected void executeTaskWithPO(SensorPO po) {
        LOG.debug("Reading sensor {}...", po.getName());
        SensorValue sensorValue = providerService.obtainValue(po.getProviderId(), po.getProviderConfig());
        if (sensorValue == null) {
            LOG.debug("No value obtained from {}", po.getName());
            return;
        }

        SensorValuePO sensorValuePO = new SensorValuePO();
        sensorValuePO.setSensor(po);
        sensorValuePO.setInsertDateTime(LocalDateTime.now());
        LocalDateTime dateTime = sensorValue.getDateTime();
        sensorValuePO.setDateTime(dateTime);
        int value = sensorValue.getValue();
        sensorValuePO.setIntValue(value);
        valueRepository.saveAndFlush(sensorValuePO);
        valueRepository.deleteAllBefore(LocalDateTime.now().minusMonths(2));

        try {
            sensorValuePublisher.publish(sensorValuePO);
        } catch (Exception e) {
            LOG.warn("SensorValuePublisher failed to publish", e);
        }

        LOG.debug("Saved sensor value {} from {}", value, dateTime);
    }

}
