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

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import de.hasait.common.service.driver.RefreshableDriverInstance;

@Embeddable
public class RefreshableDriverInstanceEO extends DriverInstanceEO implements RefreshableDriverInstance {

    @Column(name = "DRIVER_LAST_REFRESH")
    private LocalDateTime driverLastRefresh;

    @Override
    public LocalDateTime getDriverLastRefresh() {
        return driverLastRefresh;
    }

    @Override
    public void setDriverLastRefresh(LocalDateTime driverLastRefresh) {
        this.driverLastRefresh = driverLastRefresh;
    }

}
