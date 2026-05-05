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

package de.hasait.common.service.schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import de.hasait.common.service.HasId;
import de.hasait.common.service.Store;
import de.hasait.common.service.StoreRegistry;
import de.hasait.common.util.Util;

@Service
public abstract class AbstractScheduleService<O extends HasId<ID>, ID> implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractScheduleService.class);

    private final Class<O> ownerClass;
    private final Function<O, ScheduleInstance> scheduleInstanceGetter;

    private final ConcurrentHashMap<ID, List<ScheduledFuture<?>>> scheduledFutures = new ConcurrentHashMap<>();

    private StoreRegistry storeRegistry;
    private Store<O, ID> store;
    private TaskScheduler taskScheduler;

    public AbstractScheduleService(@Nonnull Class<O> ownerClass, @Nonnull Function<O, ScheduleInstance> scheduleInstanceGetter) {
        super();

        this.ownerClass = Objects.requireNonNull(ownerClass);
        this.scheduleInstanceGetter = Objects.requireNonNull(scheduleInstanceGetter);
    }

    public StoreRegistry getStoreRegistry() {
        return storeRegistry;
    }

    @Autowired
    public void setStoreRegistry(StoreRegistry storeRegistry) {
        this.storeRegistry = storeRegistry;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    @Autowired
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        store = storeRegistry.getNonnull(ownerClass);

        LOG.info("Scheduling tasks for already existing {}s...", ownerClass.getSimpleName());
        store.listAllBeans().forEach(this::createOrUpdateScheduledTask);
    }

    public void postPersist(O owner) {
        LOG.debug("postPersist: {}", owner);

        createOrUpdateScheduledTask(owner);
    }

    public void postUpdate(O owner) {
        LOG.debug("postUpdate: {}", owner);

        createOrUpdateScheduledTask(owner);
    }

    public void preRemove(O owner) {
        LOG.debug("preRemove: {}", owner);

        cancelScheduledTask(owner.getId());
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

    private void createOrUpdateScheduledTask(O owner) {
        ID scheduleId = owner.getId();

        cancelScheduledTask(scheduleId);

        ScheduleInstance scheduleInstance = scheduleInstanceGetter.apply(owner);

        boolean enabled = scheduleInstance.isScheduleEnabled();
        String cronExpression = scheduleInstance.getScheduleCron();
        if (enabled && cronExpression != null) {
            CronTrigger cronTrigger = new CronTrigger(cronExpression);
            TaskWithId task = new TaskWithId(scheduleId);
            registerScheduledFuture(scheduleId, taskScheduler.schedule(task, cronTrigger));

            postRescheduled(owner, scheduleInstance);
        }
    }

    protected void postRescheduled(O owner, ScheduleInstance scheduleInstance) {
        // empty default implementation
    }

    public final void executeTask(ID scheduleId) {
        O owner = store.findBeanById(scheduleId).orElseThrow();
        ScheduleInstance scheduleInstance = scheduleInstanceGetter.apply(owner);
        executeTaskForObject(owner);
        scheduleInstance.setScheduleLastExecution(LocalDateTime.now());
    }

    protected abstract void executeTaskForObject(O owner);

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
