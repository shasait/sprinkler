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

package de.hasait.sprinkler.domain.sensor;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

import de.hasait.common.jpa.domain.AbstractNameDescPO;
import de.hasait.common.jpa.domain.ScheduleInstanceEO;
import de.hasait.common.jpa.domain.driver.DriverInstanceEO;
import de.hasait.common.vaadin.bpui.impl.DriverInstanceBpUi;
import de.hasait.sprinkler.service.sensor.SensorPOListener;
import de.hasait.sprinkler.service.sensor.driver.SensorDriver;

@Entity
@Table(name = "SENSOR")
@EntityListeners(SensorPOListener.class)
public class SensorPO extends AbstractNameDescPO {

    @Embedded
    @DriverInstanceBpUi(driverClass = SensorDriver.class)
    private DriverInstanceEO driverInstance = new DriverInstanceEO();

    @Embedded
    private ScheduleInstanceEO scheduleInstance = new ScheduleInstanceEO();

    public DriverInstanceEO getDriverInstance() {
        return driverInstance;
    }

    public void setDriverInstance(DriverInstanceEO driverInstance) {
        this.driverInstance = driverInstance;
    }

    public ScheduleInstanceEO getScheduleInstance() {
        return scheduleInstance;
    }

    public void setScheduleInstance(ScheduleInstanceEO scheduleInstance) {
        this.scheduleInstance = scheduleInstance;
    }

}
