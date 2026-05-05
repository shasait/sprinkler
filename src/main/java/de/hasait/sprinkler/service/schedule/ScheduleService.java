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

package de.hasait.sprinkler.service.schedule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hasait.common.service.schedule.AbstractScheduleService;
import de.hasait.common.service.schedule.ScheduleInstance;
import de.hasait.common.util.Util;
import de.hasait.common.util.ValueWithExplanation;
import de.hasait.sprinkler.domain.schedule.ScheduleLogPO;
import de.hasait.sprinkler.domain.schedule.ScheduleLogRepository;
import de.hasait.sprinkler.domain.schedule.SchedulePO;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.service.relay.RelayService;
import de.hasait.sprinkler.service.sensor.SensorService;

@Service
public class ScheduleService extends AbstractScheduleService<SchedulePO, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleService.class);

    private final ScheduleLogRepository scheduleLogRepository;
    private final RelayService relayService;
    private final SensorService sensorService;

    public ScheduleService(ScheduleLogRepository scheduleLogRepository, RelayService relayService, SensorService sensorService) {
        super(SchedulePO.class, SchedulePO::getScheduleInstance);

        this.scheduleLogRepository = scheduleLogRepository;
        this.relayService = relayService;
        this.sensorService = sensorService;

        SchedulePOListener.scheduleTaskService = this;
    }

    public ValueWithExplanation<Long> determineDurationMillisSensor(SchedulePO schedulePO) {
        return determineDurationMillisSensor(schedulePO.determineDurationMillis(), schedulePO.getSensorInfluence(), schedulePO.getSensorChangeLimit(), schedulePO.getSensor());
    }

    public ValueWithExplanation<Long> determineDurationMillisSensor(long durationMillis, int sensorInfluence, int sensorChangeLimit, SensorPO sensorPO) {
        if (sensorPO == null) {
            return new ValueWithExplanation<>(durationMillis, "Unmodified duration as no sensor is configured");
        } else if (sensorInfluence == 0) {
            return new ValueWithExplanation<>(durationMillis, "Unmodified duration as sensor is ignored (sensorInfluence is 0)");
        }
        int sensorChange = sensorService.determineChange(sensorPO);
        if (sensorChange > sensorChangeLimit) {
            return new ValueWithExplanation<>(0L, "Zero as sensorChange is greater than sensorChangeLimit: " + sensorChange + " > " + sensorChangeLimit);
        }
        List<SensorValuePO> lastSensorValues = sensorService.getLastValues(sensorPO);
        int value = lastSensorValues.isEmpty() ? 0 : lastSensorValues.get(0).getIntValue();
        long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
        int influenceDivisor = 100;
        long durationMillisWithInfluence = TimeUnit.SECONDS.toMillis(Math.max(0, durationSeconds - (long) value * sensorInfluence / influenceDivisor));
        return new ValueWithExplanation<>(durationMillisWithInfluence, "duration - lastSensorValue x sensorInfluence / " + influenceDivisor + " = " + durationSeconds + " - " + value + " x " + sensorInfluence + " / " + influenceDivisor);
    }

    @Override
    protected void postRescheduled(SchedulePO po, ScheduleInstance scheduleInstance) {
        super.postRescheduled(po, scheduleInstance);

        long durationMillis = po.determineDurationMillis();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime previousSeed = now.minus(durationMillis, ChronoUnit.MILLIS);
        LocalDateTime previousStart = Util.determineNext(scheduleInstance.getScheduleCron(), previousSeed);
        if (previousStart.isBefore(now)) {
            long remainingMillis = durationMillis - Duration.between(previousStart, now).toMillis();
            if (remainingMillis > 10000) {
                LOG.info("Resuming {}...", po.getRelay().getName());
                registerScheduledFuture(po.getId(), relayService.scheduleNow(po.getRelay().getId(), remainingMillis, "Resuming after restart"));
            }
        }
    }

    @Override
    protected void executeTaskForObject(SchedulePO po) {
        ValueWithExplanation<Long> durationMillisSensor = determineDurationMillisSensor(po);
        if (durationMillisSensor.value() <= 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info("{} skipped: {}", po.getRelay().getName(), durationMillisSensor.explanation());
            }
            return;
        }

        ScheduleLogPO scheduleLog = new ScheduleLogPO();
        LocalDateTime now = LocalDateTime.now();
        scheduleLog.setStart(now);
        scheduleLog.setSchedule(po);
        scheduleLog.setRelayName(po.getRelay().getName());
        scheduleLog.setDurationMillis(durationMillisSensor.value());
        scheduleLogRepository.save(scheduleLog);
        scheduleLogRepository.deleteAllBefore(now.minusMonths(2));

        registerScheduledFuture(po.getId(), relayService.scheduleNow(po.getRelay().getId(), durationMillisSensor.value(), durationMillisSensor.explanation()));
    }

}
