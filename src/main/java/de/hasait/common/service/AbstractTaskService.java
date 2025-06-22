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

package de.hasait.common.service;

import de.hasait.common.domain.SchedulablePO;
import de.hasait.common.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public abstract class AbstractTaskService<PO extends SchedulablePO<ID>, ID, R extends JpaRepository<PO, ID>> implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTaskService.class);

    private final Class<PO> poClass;
    private final R repository;
    private final TaskScheduler taskScheduler;
    private final ConcurrentHashMap<ID, List<ScheduledFuture<?>>> scheduledFutures = new ConcurrentHashMap<>();

    public AbstractTaskService(Class<PO> poClass, R repository, TaskScheduler taskScheduler) {
        super();

        this.poClass = poClass;
        this.repository = repository;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("Scheduling tasks for already existing {}s...", poClass.getSimpleName());
        repository.findAll().forEach(this::createOrUpdateScheduledTask);
    }

    public void postPersist(PO po) {
        LOG.debug("postPersistPO: {}", po);

        createOrUpdateScheduledTask(po);
    }

    public void postUpdatePO(PO po) {
        LOG.debug("postUpdatePO: {}", po);

        createOrUpdateScheduledTask(po);
    }

    public void preRemovePO(PO po) {
        LOG.debug("preRemovePO: {}", po);

        cancelScheduledTask((ID) po.getId());
    }

    private void cancelScheduledTask(ID scheduleId) {
        LOG.debug("cancelScheduledTask {}...", scheduleId);

        List<ScheduledFuture<?>> oldSchedules = scheduledFutures.remove(scheduleId);
        if (oldSchedules != null) {
            oldSchedules.forEach(it -> it.cancel(true));
        }
    }

    protected final void registerScheduledFuture(ID scheduleId, ScheduledFuture<?> scheduledFuture) {
        LOG.debug("registerScheduledFuture {}...", scheduleId);
        Util.registerScheduledFuture(scheduleId, scheduledFuture, scheduledFutures);
    }

    private void createOrUpdateScheduledTask(PO po) {
        ID scheduleId = po.getId();

        cancelScheduledTask(scheduleId);

        boolean enabled = po.isEnabled();
        String cronExpression = po.getCronExpression();
        if (enabled && cronExpression != null) {
            CronTrigger cronTrigger = new CronTrigger(cronExpression);
            TaskWithId task = new TaskWithId(scheduleId);
            registerScheduledFuture(scheduleId, taskScheduler.schedule(task, cronTrigger));

            postRescheduled(po);
        }
    }

    protected void postRescheduled(PO po) {
        // empty default implementation
    }

    public final void executeTask(ID scheduleId) {
        PO po = repository.findById(scheduleId).orElseThrow();
        executeTaskWithPO(po);
    }

    protected abstract void executeTaskWithPO(PO po);

    private final class TaskWithId implements Runnable {

        private final ID scheduleId;

        public TaskWithId(ID scheduleId) {
            this.scheduleId = scheduleId;
        }

        @Override
        public void run() {
            executeTask(scheduleId);
        }
    }

}
