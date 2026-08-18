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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.Nonnull;

public class BasicThreadFactory implements ThreadFactory {

    private final String format;

    private final boolean daemon;

    private final AtomicLong idHolder = new AtomicLong();

    private final Integer priority;

    public BasicThreadFactory(String format, boolean daemon) {
        this(format, daemon, null);
    }

    public BasicThreadFactory(String format, boolean daemon, Integer priority) {
        super();

        this.format = format;
        this.daemon = daemon;
        this.priority = priority;
    }

    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        long id = idHolder.incrementAndGet();
        Thread thread = new Thread(runnable);
        if (format != null) {
            thread.setName(String.format(format, id));
        }
        thread.setDaemon(daemon);
        if (priority != null) {
            thread.setPriority(priority);
        }
        return thread;
    }

}
