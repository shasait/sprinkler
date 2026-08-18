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

package de.hasait.common.util.thread;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.Nonnull;

public final class DelayedObject<T> implements Delayed {

    private static final AtomicLong SEQUENCE_NUMBER_GENERATOR = new AtomicLong();

    private final T object;
    private final long scheduledTime;
    private final long sequenceNumber;

    public DelayedObject(@Nonnull T object, int delayTime, @Nonnull TimeUnit delayTimeUnit) {
        super();

        this.object = object;
        scheduledTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayTime, delayTimeUnit);
        sequenceNumber = SEQUENCE_NUMBER_GENERATOR.incrementAndGet();
    }

    @Override
    public int compareTo(@Nonnull Delayed other) {
        if (other == this) {
            return 0;
        }
        if (other instanceof DelayedObject<?> otherDo) {
            final long scheduledTimeDifference = scheduledTime - otherDo.scheduledTime;
            if (scheduledTimeDifference < 0) {
                return -1;
            } else if (scheduledTimeDifference > 0) {
                return 1;
            } else if (sequenceNumber < otherDo.sequenceNumber) {
                return -1;
            } else {
                return 1;
            }
        }
        final long delayDifference = getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS);
        return delayDifference == 0 ? 0 : delayDifference < 0 ? -1 : 1;
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(scheduledTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    public T getObject() {
        return object;
    }

}
