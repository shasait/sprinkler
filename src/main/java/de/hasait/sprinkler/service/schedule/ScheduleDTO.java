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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import de.hasait.sprinkler.service.relay.RelayDTO;

/**
 *
 */
public class ScheduleDTO {

    public static final TimeUnit DURATION_TIME_UNIT = TimeUnit.SECONDS;
    public static final String DURATION_TIME_UNIT_STR = "s";

    private final Long id;
    private final long version;

    private boolean enabled;

    private RelayDTO relay;

    private long duration = DURATION_TIME_UNIT.convert(10, TimeUnit.MINUTES);
    private int rainFactor100;

    private String cronExpression;
    private Date next;

    public ScheduleDTO() {
        id = null;
        version = 0;
    }

    public ScheduleDTO(@Nonnull ScheduleDTO other) {
        id = other.id;
        version = other.version;
        enabled = other.enabled;
        relay = other.relay;
        duration = other.duration;
        rainFactor100 = other.rainFactor100;
        cronExpression = other.cronExpression;
        next = other.next;
    }

    public ScheduleDTO(long id, long version) {
        this.id = id;
        this.version = version;
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

    public Long getId() {
        return id;
    }

    public Date getNext() {
        return next;
    }

    public int getRainFactor100() {
        return rainFactor100;
    }

    public RelayDTO getRelay() {
        return relay;
    }

    public long getVersion() {
        return version;
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

    public void setNext(Date next) {
        this.next = next;
    }

    public void setRainFactor100(int rainFactor100) {
        this.rainFactor100 = rainFactor100;
    }

    public void setRelay(RelayDTO relay) {
        this.relay = relay;
    }

}
