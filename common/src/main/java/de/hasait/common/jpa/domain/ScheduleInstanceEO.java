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

package de.hasait.common.jpa.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import de.hasait.common.service.schedule.ScheduleInstance;

@Embeddable
public class ScheduleInstanceEO implements ScheduleInstance {

    @Column(name = "SCHEDULE_ENABLED", nullable = false)
    private boolean scheduleEnabled;

    @Size(min = 1, max = 128)
    @NotNull
    @Column(name = "SCHEDULE_CRON", nullable = false)
    private String scheduleCron;

    @Column(name = "SCHEDULE_LAST_EXEC")
    private LocalDateTime scheduleLastExecution;

    @Override
    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    @Override
    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }

    @Override
    public String getScheduleCron() {
        return scheduleCron;
    }

    @Override
    public void setScheduleCron(String scheduleCron) {
        this.scheduleCron = scheduleCron;
    }

    @Override
    public LocalDateTime getScheduleLastExecution() {
        return scheduleLastExecution;
    }

    @Override
    public void setScheduleLastExecution(LocalDateTime scheduleLastExecution) {
        this.scheduleLastExecution = scheduleLastExecution;
    }

}
