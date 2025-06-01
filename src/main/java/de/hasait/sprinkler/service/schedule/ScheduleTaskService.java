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

import de.hasait.sprinkler.domain.schedule.ScheduleLogPO;
import de.hasait.sprinkler.domain.schedule.ScheduleLogRepository;
import de.hasait.sprinkler.domain.schedule.SchedulePO;
import de.hasait.sprinkler.domain.schedule.ScheduleRepository;
import de.hasait.sprinkler.service.relay.RelayService;
import de.hasait.common.util.Util;
import de.hasait.common.util.ValueWithExplanation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class ScheduleTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTaskService.class);

    private final ScheduleRepository repository;
    private final ScheduleLogRepository scheduleLogRepository;
    private final ScheduleService scheduleService;
    private final TaskScheduler taskScheduler;
    private final RelayService relayService;
    private final ConcurrentHashMap<Long, List<ScheduledFuture<?>>> scheduledFutures = new ConcurrentHashMap<>();

    public ScheduleTaskService(ScheduleRepository repository, ScheduleLogRepository scheduleLogRepository, ScheduleService scheduleService, TaskScheduler taskScheduler, RelayService relayService) {
        super();

        this.repository = repository;
        this.scheduleLogRepository = scheduleLogRepository;
        this.scheduleService = scheduleService;
        this.taskScheduler = taskScheduler;
        this.relayService = relayService;

        SchedulePOListener.scheduleTaskService = this;

        repository.findAll().forEach(this::createOrUpdateScheduledTask);
    }

    public void postPersistSchedulePO(SchedulePO po) {
        LOG.debug("postPersistSchedulePO: {}", po);

        createOrUpdateScheduledTask(po);
    }

    public void postUpdateSchedulePO(SchedulePO po) {
        LOG.debug("postUpdateSchedulePO: {}", po);

        createOrUpdateScheduledTask(po);
    }

    public void preRemoveSchedulePO(SchedulePO po) {
        LOG.debug("preRemoveSchedulePO: {}", po);

        cancelScheduledTask(po.getId());
    }

    private void cancelScheduledTask(long scheduleId) {
        LOG.debug("cancelScheduledTask {}...", scheduleId);

        List<ScheduledFuture<?>> oldSchedules = scheduledFutures.remove(scheduleId);
        if (oldSchedules != null) {
            oldSchedules.forEach(it -> it.cancel(true));
        }
    }

    private void createOrUpdateScheduledTask(SchedulePO po) {
        long scheduleId = po.getId();

        cancelScheduledTask(scheduleId);

        boolean enabled = po.isEnabled();
        String cronExpression = po.getCronExpression();
        if (enabled && cronExpression != null) {
            long durationMillis = po.determineDurationMillis();
            CronTrigger cronTrigger = new CronTrigger(cronExpression);
            SprinklerWithSensorTask task = new SprinklerWithSensorTask(scheduleId);
            registerScheduledFuture(scheduleId, taskScheduler.schedule(task, cronTrigger));

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime previousSeed = now.minus(durationMillis, ChronoUnit.MILLIS);
            LocalDateTime previousStart = Util.determineNext(cronExpression, previousSeed);
            if (previousStart.isBefore(now)) {
                long remainingMillis = durationMillis - Duration.between(previousStart, now).toMillis();
                if (remainingMillis > 10000) {
                    LOG.info("Resuming {}...", po.getRelay().getName());
                    registerScheduledFuture(scheduleId, relayService.scheduleNow(po.getRelay().getId(), remainingMillis, "Resuming after restart"));
                }
            }
        }
    }

    private void registerScheduledFuture(long scheduleId, ScheduledFuture<?> scheduledFuture) {
        LOG.debug("registerScheduledFuture {}...", scheduleId);
        Util.registerScheduledFuture(scheduleId, scheduledFuture, scheduledFutures);
    }

    public void executeSchedule(long scheduleId) {
        SchedulePO schedulePO = repository.findById(scheduleId).orElseThrow();
        ValueWithExplanation<Long> durationMillisSensor = scheduleService.determineDurationMillisSensor(schedulePO);
        if (durationMillisSensor.getValue() <= 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info("{} skipped: {}", schedulePO.getRelay().getName(), durationMillisSensor.getExplanation());
            }
            return;
        }

        ScheduleLogPO scheduleLog = new ScheduleLogPO();
        LocalDateTime now = LocalDateTime.now();
        scheduleLog.setStart(now);
        scheduleLog.setSchedule(schedulePO);
        scheduleLog.setRelayName(schedulePO.getRelay().getName());
        scheduleLog.setDurationMillis(durationMillisSensor.getValue());
        scheduleLogRepository.save(scheduleLog);
        scheduleLogRepository.deleteAllBefore(now.minusMonths(2));

        registerScheduledFuture(scheduleId, relayService.scheduleNow(schedulePO.getRelay().getId(), durationMillisSensor.getValue(), durationMillisSensor.getExplanation()));
    }

    private class SprinklerWithSensorTask implements Runnable {

        private final long scheduleId;

        public SprinklerWithSensorTask(long scheduleId) {
            this.scheduleId = scheduleId;
        }

        @Override
        public void run() {
            executeSchedule(scheduleId);
        }
    }

}
