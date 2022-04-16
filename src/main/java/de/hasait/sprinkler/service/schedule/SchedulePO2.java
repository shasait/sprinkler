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

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import de.hasait.sprinkler.service.base.IdGenerator;

/**
 *
 */
public class SchedulePO2 implements Serializable {

    static final TimeUnit DURATION_TIME_UNIT = TimeUnit.SECONDS;

    static final long serialVersionUID = 1L;

    private final long id;
    private long version;

    private boolean enabled;
    private String providerId;
    private String relayId;
    private long duration;
    private int rainFactor100;
    private String cronExpression;

    public SchedulePO2(@Nullable Long id) {
        this.id = id != null ? id : IdGenerator.next();
    }

    public SchedulePO2(@Nonnull SchedulePO2 other) {
        id = other.id;
        version = other.version;
        enabled = other.enabled;
        providerId = other.providerId;
        relayId = other.relayId;
        duration = other.duration;
        rainFactor100 = other.rainFactor100;
        cronExpression = other.cronExpression;
    }

    public SchedulePO2(@Nonnull SchedulePO other) {
        id = other.getId();
        version = other.getVersion();
        enabled = true;
        providerId = other.getProviderId();
        relayId = other.getRelayId();
        duration = DURATION_TIME_UNIT.convert(other.getDurationMinutes(), TimeUnit.MINUTES);
        rainFactor100 = other.getRainFactor100();
        String cronExpression = other.getCronExpression();
        this.cronExpression = StringUtils.isNotBlank(cronExpression) ? "0 " + cronExpression : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchedulePO2 other = (SchedulePO2) o;
        return id == other.id && version == other.version //
                && Objects.equals(providerId, other.providerId) //
                && Objects.equals(relayId, other.relayId) //
                && enabled == other.enabled //
                && duration == other.duration //
                && rainFactor100 == other.rainFactor100 //
                && Objects.equals(cronExpression, other.cronExpression) //
                ;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getId() {
        return id;
    }

    public String getProviderId() {
        return providerId;
    }

    public int getRainFactor100() {
        return rainFactor100;
    }

    public String getRelayId() {
        return relayId;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version);
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setRainFactor100(int rainFactor100) {
        this.rainFactor100 = rainFactor100;
    }

    public void setRelayId(String relayId) {
        this.relayId = relayId;
    }

    public void setVersion(long version) {
        this.version = version;
    }

}
