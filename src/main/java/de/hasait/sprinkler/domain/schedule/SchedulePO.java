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

import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.common.domain.IdAndVersion;
import de.hasait.sprinkler.service.schedule.SchedulePOListener;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Entity
@Table(name = "SCHEDULE")
@EntityListeners(SchedulePOListener.class)
public class SchedulePO implements IdAndVersion {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private long version;

    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "RELAY_ID", nullable = false)
    private RelayPO relay;

    @Min(1)
    @Column(name = "DURATION_S")
    private int durationSeconds;

    @ManyToOne
    @JoinColumn(name = "SENSOR_ID")
    private SensorPO sensor;

    @Min(0)
    @Column(name = "SENSOR_INFLUENCE")
    private int sensorInfluence;

    @Min(0)
    @Column(name = "SENSOR_CHANGE_LIMIT")
    private int sensorChangeLimit;

    @Size(min = 1, max = 64)
    @NotNull
    @Column(name = "CRON_EXPRESSION", nullable = false)
    private String cronExpression;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    @OrderBy("start DESC")
    private List<ScheduleLogPO> log;

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RelayPO getRelay() {
        return relay;
    }

    public void setRelay(RelayPO relay) {
        this.relay = relay;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public SensorPO getSensor() {
        return sensor;
    }

    public void setSensor(SensorPO sensor) {
        this.sensor = sensor;
    }

    public int getSensorInfluence() {
        return sensorInfluence;
    }

    public void setSensorInfluence(int sensorInfluence) {
        this.sensorInfluence = sensorInfluence;
    }

    public int getSensorChangeLimit() {
        return sensorChangeLimit;
    }

    public void setSensorChangeLimit(int sensorChangeLimit) {
        this.sensorChangeLimit = sensorChangeLimit;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public long determineDurationMillis() {
        return TimeUnit.MILLISECONDS.convert(durationSeconds, TimeUnit.SECONDS);
    }

}
