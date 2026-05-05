/*
 * Copyright (C) 2026 by Sebastian Hasait (sebastian at hasait dot de)
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

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hasait.common.service.schedule.AbstractScheduleService;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.domain.sensor.SensorValueRepository;
import de.hasait.sprinkler.service.sensor.driver.SensorValue;
import de.hasait.sprinkler.service.sensor.publish.SensorValuePublisher;

@Service
public class SensorScheduleService extends AbstractScheduleService<SensorPO, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(SensorScheduleService.class);

    private final SensorService sensorService;
    private final SensorValueRepository valueRepository;
    private final SensorValuePublisher sensorValuePublisher;

    public SensorScheduleService(SensorService sensorService, SensorValueRepository valueRepository, SensorValuePublisher sensorValuePublisher) {
        super(SensorPO.class, SensorPO::getScheduleInstance);

        this.sensorService = sensorService;
        this.valueRepository = valueRepository;
        this.sensorValuePublisher = sensorValuePublisher;

        SensorPOListener.sensorTaskService = this;
    }

    @Override
    protected void executeTaskForObject(SensorPO po) {
        LOG.debug("Reading sensor {}...", po.getName());
        SensorValue sensorValue = sensorService.obtainValue(po);
        if (sensorValue == null) {
            LOG.debug("No value obtained from {}", po.getName());
            return;
        }

        SensorValuePO sensorValuePO = new SensorValuePO();
        sensorValuePO.setSensor(po);
        sensorValuePO.setInsertDateTime(LocalDateTime.now());
        LocalDateTime dateTime = sensorValue.dateTime();
        sensorValuePO.setDateTime(dateTime);
        int value = sensorValue.value();
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
