/*
 * Copyright (C) 2021 by Sebastian Hasait (sebastian at hasait dot de)
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
import de.hasait.sprinkler.domain.schedule.ScheduleRepository;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.service.sensor.SensorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service
public class ScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleRepository repository;
    private final SensorService sensorService;

    public ScheduleService(ScheduleRepository repository, SensorService sensorService) {
        this.repository = repository;
        this.sensorService = sensorService;
    }

    public long determineDurationMillisSensor(SchedulePO schedulePO) {
        return determineDurationMillisSensor(schedulePO.determineDurationMillis(), schedulePO.getSensorInfluence(), schedulePO.getSensorChangeLimit(), schedulePO.getSensor());
    }

    public long determineDurationMillisSensor(long durationMillis, int sensorInfluence, int sensorChangeLimit, SensorPO sensorPO) {
        if (sensorInfluence == 0 || sensorPO == null) {
            return durationMillis;
        }
        int change = sensorService.determineChange(sensorPO);
        if (change > sensorChangeLimit) {
            return 0;
        }
        List<SensorValuePO> lastSensorValues = sensorService.getLastValues(sensorPO);
        int value = lastSensorValues.isEmpty() ? 0 : lastSensorValues.get(0).getIntValue();
        long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
        return TimeUnit.SECONDS.toMillis(Math.max(0, durationSeconds - (long) value * sensorInfluence / 100));
    }

}
