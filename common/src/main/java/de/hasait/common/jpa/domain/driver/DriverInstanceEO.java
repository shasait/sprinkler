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

package de.hasait.common.jpa.domain.driver;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import de.hasait.common.service.driver.DriverInstance;

@Embeddable
public class DriverInstanceEO implements DriverInstance {

    @Size(min = 1, max = 32)
    @NotNull
    @Column(name = "DRIVER_ID", nullable = false)
    private String driverId;

    @Size(max = 512)
    @Column(name = "DRIVER_CONFIG")
    private String driverConfig;

    @Override
    public String getDriverId() {
        return driverId;
    }

    @Override
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    @Override
    public String getDriverConfig() {
        return driverConfig;
    }

    @Override
    public void setDriverConfig(String driverConfig) {
        this.driverConfig = driverConfig;
    }

}
