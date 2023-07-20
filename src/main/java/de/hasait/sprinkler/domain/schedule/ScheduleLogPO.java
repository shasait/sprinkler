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

package de.hasait.sprinkler.domain.schedule;

import de.hasait.sprinkler.service.IdAndVersion;
import de.hasait.sprinkler.util.Util;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 *
 */
@Entity
@Table(name = "SCHEDULE_LOG")
public class ScheduleLogPO implements IdAndVersion {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private long version;

    @ManyToOne
    @JoinColumn(name = "SCHEDULE_ID", nullable = false)
    private SchedulePO schedule;

    @Column(name = "START", nullable = false)
    private LocalDateTime start;

    @Size(min = 1, max = 32)
    @NotNull
    @Column(name = "RELAY_NAME", nullable = false)
    private String relayName;

    @Min(0)
    @Column(name = "DURATION_MILLIS", nullable = false)
    private long durationMillis;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

    public SchedulePO getSchedule() {
        return schedule;
    }

    public void setSchedule(SchedulePO schedule) {
        this.schedule = schedule;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public String getRelayName() {
        return relayName;
    }

    public void setRelayName(String relayName) {
        this.relayName = relayName;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public String determineDurationHuman() {
        return Util.millisToHuman(getDurationMillis(), Integer.MAX_VALUE);
    }

}
