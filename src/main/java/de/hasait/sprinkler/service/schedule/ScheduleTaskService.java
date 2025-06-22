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

import de.hasait.common.service.AbstractTaskService;
import de.hasait.common.util.Util;
import de.hasait.common.util.ValueWithExplanation;
import de.hasait.sprinkler.domain.schedule.ScheduleLogPO;
import de.hasait.sprinkler.domain.schedule.ScheduleLogRepository;
import de.hasait.sprinkler.domain.schedule.SchedulePO;
import de.hasait.sprinkler.domain.schedule.ScheduleRepository;
import de.hasait.sprinkler.service.relay.RelayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ScheduleTaskService extends AbstractTaskService<SchedulePO, Long, ScheduleRepository> {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTaskService.class);

    private final ScheduleLogRepository scheduleLogRepository;
    private final ScheduleService scheduleService;
    private final RelayService relayService;

    public ScheduleTaskService(ScheduleRepository repository, TaskScheduler taskScheduler, ScheduleLogRepository scheduleLogRepository, ScheduleService scheduleService, RelayService relayService) {
        super(SchedulePO.class, repository, taskScheduler);

        this.scheduleLogRepository = scheduleLogRepository;
        this.scheduleService = scheduleService;
        this.relayService = relayService;

        SchedulePOListener.scheduleTaskService = this;
    }

    @Override
    protected void postRescheduled(SchedulePO po) {
        super.postRescheduled(po);

        long durationMillis = po.determineDurationMillis();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime previousSeed = now.minus(durationMillis, ChronoUnit.MILLIS);
        LocalDateTime previousStart = Util.determineNext(po.getCronExpression(), previousSeed);
        if (previousStart.isBefore(now)) {
            long remainingMillis = durationMillis - Duration.between(previousStart, now).toMillis();
            if (remainingMillis > 10000) {
                LOG.info("Resuming {}...", po.getRelay().getName());
                registerScheduledFuture(po.getId(), relayService.scheduleNow(po.getRelay().getId(), remainingMillis, "Resuming after restart"));
            }
        }
    }

    @Override
    protected void executeTaskWithPO(SchedulePO po) {
        ValueWithExplanation<Long> durationMillisSensor = scheduleService.determineDurationMillisSensor(po);
        if (durationMillisSensor.getValue() <= 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info("{} skipped: {}", po.getRelay().getName(), durationMillisSensor.getExplanation());
            }
            return;
        }

        ScheduleLogPO scheduleLog = new ScheduleLogPO();
        LocalDateTime now = LocalDateTime.now();
        scheduleLog.setStart(now);
        scheduleLog.setSchedule(po);
        scheduleLog.setRelayName(po.getRelay().getName());
        scheduleLog.setDurationMillis(durationMillisSensor.getValue());
        scheduleLogRepository.save(scheduleLog);
        scheduleLogRepository.deleteAllBefore(now.minusMonths(2));

        registerScheduledFuture(po.getId(), relayService.scheduleNow(po.getRelay().getId(), durationMillisSensor.getValue(), durationMillisSensor.getExplanation()));
    }

}
