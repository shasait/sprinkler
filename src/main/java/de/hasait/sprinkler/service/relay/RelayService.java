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

package de.hasait.sprinkler.service.relay;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import de.hasait.common.service.driver.AbstractDriverService;
import de.hasait.common.service.driver.DriverInstance;
import de.hasait.common.util.Util;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.domain.relay.RelayRepository;
import de.hasait.sprinkler.service.relay.driver.RelayDriver;

@Service
public class RelayService extends AbstractDriverService<RelayDriver<?>, RelayPO, DriverInstance> {

    private static final Logger LOG = LoggerFactory.getLogger(RelayService.class);

    private final RelayRepository repository;

    private final TaskScheduler taskScheduler;

    public RelayService(RelayDriver<?>[] drivers, RelayRepository repository, TaskScheduler taskScheduler) {
        super(drivers, RelayPO.class, RelayPO::getDriverInstance);
        this.repository = repository;
        this.taskScheduler = taskScheduler;
    }

    public boolean isActive(DriverInstance driverInstance) {
        RelayDriver<?> driver = getDriverByIdNotNull(driverInstance.getDriverId());
        return isActiveInternal(driver, driverInstance.getDriverConfig());
    }

    public void changeActive(DriverInstance driverInstance, int amount) {
        RelayDriver<?> driver = getDriverByIdNotNull(driverInstance.getDriverId());
        changeActiveInternal(driver, driverInstance.getDriverConfig(), amount);
    }

    private <C> boolean isActiveInternal(RelayDriver<C> driver, String driverConfigText) {
        C config = driver.parseDriverConfigText(driverConfigText);
        return driver.isActive(config);
    }

    private <C> void changeActiveInternal(RelayDriver<C> driver, String driverConfigText, int amount) {
        C config = driver.parseDriverConfigText(driverConfigText);
        driver.changeActive(config, amount);
    }

    public void changeActive(long relayId, int amount) {
        RelayPO relayPO = repository.findById(relayId).orElseThrow();
        driverCallable(relayPO, ((owner, driver, driverInstance) -> changeActiveInternal(driver, driverInstance.getDriverConfig(), amount)));
    }

    public ScheduledFuture<?> scheduleNow(long relayId, long durationMillis, String explanation) {
        RelayPO relayPO = repository.findById(relayId).orElseThrow();
        return taskScheduler.schedule(new RelayTask(relayPO.getId(), relayPO.getName(), durationMillis, explanation), Instant.now());
    }

    public void deactivate(long relayId) {
        RelayPO relayPO = repository.findById(relayId).orElseThrow();
        changeActive(relayPO.getDriverInstance(), -10000);
    }

    private class RelayTask implements Runnable {

        private final long relayId;
        private final String relayName;
        private final long durationMillis;
        private final String durationMillisHuman;
        private final String explanation;

        public RelayTask(long relayId, String relayName, long durationMillis, String explanation) {
            this.relayId = relayId;
            this.relayName = relayName;
            this.durationMillis = durationMillis;
            this.durationMillisHuman = Util.millisToHuman(durationMillis, 3);
            this.explanation = explanation;
        }

        @Override
        public void run() {
            if (LOG.isInfoEnabled()) {
                LOG.info("{} activating for {}ms ({}) - {}...", relayName, durationMillis, durationMillisHuman, explanation);
            }
            changeActive(relayId, 1);
            try {
                Thread.sleep(durationMillis);
            } catch (InterruptedException e) {
                LOG.info("Sleeping was interrupted");
            }
            changeActive(relayId, -1);
            if (LOG.isInfoEnabled()) {
                LOG.info("{} deactivated", relayName);
            }
        }

    }

}
