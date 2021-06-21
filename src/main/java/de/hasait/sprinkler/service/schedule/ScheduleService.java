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

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.mapdb.BTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import de.hasait.sprinkler.service.base.AbstractListenableService;
import de.hasait.sprinkler.service.base.MapDbService;
import de.hasait.sprinkler.service.relay.RelayDTO;
import de.hasait.sprinkler.service.relay.RelayService;
import de.hasait.sprinkler.service.weather.RainService;
import de.hasait.sprinkler.service.weather.RainValue;

/**
 *
 */
@Service
public class ScheduleService extends AbstractListenableService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleService.class);

    private final MapDbService mapDbService;
    private final TaskScheduler taskScheduler;
    private final RelayService relayService;
    private final RainService rainService;
    private final BTreeMap<Long, SchedulePO> schedules;
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    public ScheduleService(MapDbService mapDbService, TaskScheduler taskScheduler, RelayService relayService, RainService rainService) {
        this.mapDbService = mapDbService;
        this.taskScheduler = taskScheduler;
        this.relayService = relayService;
        this.rainService = rainService;

        schedules = this.mapDbService.getDb().treeMap("schedules-" + SchedulePO.serialVersionUID, Serializer.LONG, Serializer.JAVA)
                                     .createOrOpen();
        schedules.values().forEach(this::updateSchedule);
    }

    public void addOrUpdateSchedule(@Nonnull ScheduleDTO scheduleDTO) {
        SchedulePO schedule = mapToSchedulePO(scheduleDTO);
        schedules.merge(schedule.getId(), schedule, this::optimisticLockUpdate);
        mapDbService.commit();
        updateSchedule(schedule);

        notifyListeners();
    }

    public void deleteSchedule(@Nonnull ScheduleDTO scheduleDTO) {
        SchedulePO schedule = mapToSchedulePO(scheduleDTO);
        if (schedules.remove(schedule.getId(), schedule)) {
            mapDbService.commit();
            notifyListeners();
        } else {
            throw createOptimisticLockFailed();
        }
    }

    public long determineDurationMillis(long durationMillis, int rainFactor100) {
        if (rainFactor100 == 0) {
            return durationMillis;
        }
        boolean raining = rainService.isRaining();
        if (raining) {
            return 0;
        }
        RainValue rainValue = rainService.getLastRainValue();
        int rain = rainValue != null ? rainValue.getRain() : 0;
        long durationSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);
        return TimeUnit.SECONDS.toMillis(Math.max(0, durationSeconds - (long) rain * rainFactor100 / 100));
    }

    @Nonnull
    public List<ScheduleDTO> getSchedules() {
        return schedules.values().stream().map(this::mapToScheduleDTO).collect(Collectors.toList());
    }

    public void pulse(@Nonnull RelayDTO relay, long durationMillis) {
        taskScheduler.schedule(new PulseTask(relay.getProviderId(), relay.getRelayId(), durationMillis, 0), new Date());
    }

    public void stop(RelayDTO relay) {
        relayService.setActive(relay.getProviderId(), relay.getRelayId(), false);
    }

    private RuntimeException createOptimisticLockFailed() {
        return new RuntimeException("Modified meanwhile");
    }

    private String getCronExpression(SchedulePO schedule) {
        String cronExpression = schedule.getCronExpression();
        return !StringUtils.isBlank(cronExpression) ? "0 " + cronExpression : null;
    }

    private long getDurationMillis(SchedulePO schedule) {
        return TimeUnit.MINUTES.toMillis(schedule.getDurationMinutes());
    }

    private RelayDTO getRelay(SchedulePO schedule) {
        String relayId = schedule.getRelayId();
        return !StringUtils.isBlank(relayId) ? relayService.getRelay(schedule.getProviderId(), schedule.getRelayId()) : null;
    }

    private ScheduleDTO mapToScheduleDTO(SchedulePO po) {
        ScheduleDTO dto = new ScheduleDTO(po.getId(), po.getVersion());
        dto.setRelay(getRelay(po));
        dto.setDurationMinutes(po.getDurationMinutes());
        dto.setRainFactor100(po.getRainFactor100());
        dto.setCronExpression(po.getCronExpression());
        String cronExpression = getCronExpression(po);
        if (!StringUtils.isBlank(cronExpression)) {
            CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
            Date next = cronSequenceGenerator.next(new Date());
            dto.setNext(next);
        }
        return dto;
    }

    private SchedulePO mapToSchedulePO(ScheduleDTO dto) {
        SchedulePO po = new SchedulePO(dto.getId());
        po.setVersion(dto.getVersion());
        RelayDTO relay = dto.getRelay();
        po.setProviderId(relay != null ? relay.getProviderId() : null);
        po.setRelayId(relay != null ? relay.getRelayId() : null);
        po.setDurationMinutes(dto.getDurationMinutes());
        po.setRainFactor100(dto.getRainFactor100());
        po.setCronExpression(dto.getCronExpression());
        return po;
    }

    private SchedulePO optimisticLockUpdate(SchedulePO current, SchedulePO updated) {
        if (current.getVersion() != updated.getVersion()) {
            throw createOptimisticLockFailed();
        }
        SchedulePO result = new SchedulePO(updated);
        result.setVersion(result.getVersion() + 1);
        return result;
    }

    private void updateSchedule(SchedulePO schedule) {
        long scheduleId = schedule.getId();

        ScheduledFuture<?> oldSchedule = scheduledFutures.remove(scheduleId);
        if (oldSchedule != null) {
            oldSchedule.cancel(true);
        }

        RelayDTO relay = getRelay(schedule);
        String cronExpression = getCronExpression(schedule);
        if (relay != null && cronExpression != null) {
            long durationMillis = getDurationMillis(schedule);
            CronTrigger cronTrigger = new CronTrigger(cronExpression);
            PulseTask task = new PulseTask(schedule.getProviderId(), schedule.getRelayId(), durationMillis, schedule.getRainFactor100());
            ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(task, cronTrigger);
            scheduledFutures.put(scheduleId, scheduledFuture);
        }
    }

    private class PulseTask implements Runnable {

        private final String providerId;
        private final String relayId;
        private final long durationMillis;
        private final int rainFactor100;

        public PulseTask(@Nonnull String providerId, @Nonnull String relayId, long durationMillis, int rainFactor100) {
            this.providerId = providerId;
            this.relayId = relayId;
            this.durationMillis = durationMillis;
            this.rainFactor100 = rainFactor100;
        }

        @Override
        public void run() {
            long durationMillisAfterRain;
            if (rainFactor100 > 0) {
                durationMillisAfterRain = determineDurationMillis(durationMillis, rainFactor100);
            } else {
                durationMillisAfterRain = durationMillis;
            }
            if (durationMillisAfterRain <= 0) {
                LOG.info("Skip activation due to rain");
                return;
            }

            relayService.setActive(providerId, relayId, true);
            if (LOG.isInfoEnabled()) {
                LOG.info("Activated relay: {}", relayService.getRelay(providerId, relayId));
            }
            try {
                Thread.sleep(durationMillisAfterRain);
            } catch (InterruptedException e) {
                // continue
            }
            relayService.setActive(providerId, relayId, false);
        }
    }

}
