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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import de.hasait.sprinkler.service.base.Util;
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
    private final BTreeMap<Long, SchedulePO2> schedules;
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    public ScheduleService(MapDbService mapDbService, TaskScheduler taskScheduler, RelayService relayService, RainService rainService) {
        this.mapDbService = mapDbService;
        this.taskScheduler = taskScheduler;
        this.relayService = relayService;
        this.rainService = rainService;

        schedules = this.mapDbService.getDb().treeMap("schedules2", Serializer.LONG, Serializer.JAVA).createOrOpen();
        migrate1();
        schedules.values().forEach(this::updateSchedule);
    }

    private void migrate1() {
        BTreeMap<Long, SchedulePO> schedules1 = //
                mapDbService.getDb().treeMap("schedules-" + SchedulePO.serialVersionUID, Serializer.LONG, Serializer.JAVA).createOrOpen();
        Iterator<Map.Entry<Long, SchedulePO>> iterator = schedules1.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, SchedulePO> next = iterator.next();
            SchedulePO2 po = new SchedulePO2(next.getValue());
            schedules.put(po.getId(), po);
            iterator.remove();
            LOG.info("Migrated SchedulePO -> SchedulePO2: {}", next.getKey());
        }
        mapDbService.commit();
    }

    public void addOrUpdateSchedule(@Nonnull ScheduleDTO scheduleDTO) {
        SchedulePO2 schedule = mapToSchedulePO(scheduleDTO);
        schedules.merge(schedule.getId(), schedule, this::optimisticLockUpdate);
        mapDbService.commit();
        updateSchedule(schedule);

        notifyListeners();
    }

    public void deleteSchedule(@Nonnull ScheduleDTO scheduleDTO) {
        SchedulePO2 schedule = mapToSchedulePO(scheduleDTO);
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

    public Date determineNext(String cronExpression, Date seed) {
        if (StringUtils.isNotBlank(cronExpression)) {
            CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
            return cronSequenceGenerator.next(seed);
        }
        return null;
    }

    public String determineNextRelative(Date seed, Date next, int limit) {
        return "in " + Util.millisToHuman(seed, next, limit);
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

    private long getDurationMillis(SchedulePO2 schedule) {
        return TimeUnit.SECONDS.toMillis(schedule.getDuration());
    }

    private RelayDTO getRelay(SchedulePO2 schedule) {
        String relayId = schedule.getRelayId();
        return !StringUtils.isBlank(relayId) ? relayService.getRelay(schedule.getProviderId(), schedule.getRelayId()) : null;
    }

    private ScheduleDTO mapToScheduleDTO(SchedulePO2 po) {
        ScheduleDTO dto = new ScheduleDTO(po.getId(), po.getVersion());
        dto.setEnabled(po.isEnabled());
        dto.setRelay(getRelay(po));
        dto.setDuration(ScheduleDTO.DURATION_TIME_UNIT.convert(po.getDuration(), SchedulePO2.DURATION_TIME_UNIT));
        dto.setRainFactor100(po.getRainFactor100());
        String cronExpression = po.getCronExpression();
        dto.setCronExpression(cronExpression);
        Date now = new Date();
        Date next = determineNext(cronExpression, now);
        dto.setNext(next);
        dto.setNextRelative(determineNextRelative(now, next, 3));
        return dto;
    }

    private SchedulePO2 mapToSchedulePO(ScheduleDTO dto) {
        SchedulePO2 po = new SchedulePO2(dto.getId());
        po.setVersion(dto.getVersion());
        po.setEnabled(dto.isEnabled());
        RelayDTO relay = dto.getRelay();
        po.setProviderId(relay != null ? relay.getProviderId() : null);
        po.setRelayId(relay != null ? relay.getRelayId() : null);
        po.setDuration(SchedulePO2.DURATION_TIME_UNIT.convert(dto.getDuration(), ScheduleDTO.DURATION_TIME_UNIT));
        po.setRainFactor100(dto.getRainFactor100());
        po.setCronExpression(dto.getCronExpression());
        return po;
    }

    private SchedulePO2 optimisticLockUpdate(SchedulePO2 current, SchedulePO2 updated) {
        if (current.getVersion() != updated.getVersion()) {
            throw createOptimisticLockFailed();
        }
        SchedulePO2 result = new SchedulePO2(updated);
        result.setVersion(result.getVersion() + 1);
        return result;
    }

    private void updateSchedule(SchedulePO2 schedule) {
        long scheduleId = schedule.getId();

        ScheduledFuture<?> oldSchedule = scheduledFutures.remove(scheduleId);
        if (oldSchedule != null) {
            oldSchedule.cancel(true);
        }

        boolean enabled = schedule.isEnabled();
        RelayDTO relay = getRelay(schedule);
        String cronExpression = schedule.getCronExpression();
        if (enabled && relay != null && cronExpression != null) {
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
                if (LOG.isInfoEnabled()) {
                    LOG.info("{} skipped due to rain", relayService.getRelay(providerId, relayId));
                }
                return;
            }

            relayService.setActive(providerId, relayId, true);
            if (LOG.isInfoEnabled()) {
                LOG.info("{} activated for {}ms", relayService.getRelay(providerId, relayId), durationMillisAfterRain);
            }
            try {
                Thread.sleep(durationMillisAfterRain);
            } catch (InterruptedException e) {
                LOG.info("Sleeping was interrupted");
            }
            relayService.setActive(providerId, relayId, false);
            if (LOG.isInfoEnabled()) {
                LOG.info("{} deactivated", relayService.getRelay(providerId, relayId));
            }
        }
    }

}
