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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public final class Async {

    private static final ThreadFactory threadFactory = new BasicThreadFactory(Async.class.getSimpleName() + "-%02d", true);
    private static final ScheduledExecutorService asyncService = Executors.newScheduledThreadPool(1, threadFactory);

    public static void execute(Runnable runnable) {
        asyncService.execute(runnable);
    }

    public static void execute(int delayTime, TimeUnit delayTimeUnit, Runnable runnable) {
        asyncService.scheduleWithFixedDelay(runnable, delayTime, delayTime, delayTimeUnit);
    }

    private Async() {
        super();
    }

}
