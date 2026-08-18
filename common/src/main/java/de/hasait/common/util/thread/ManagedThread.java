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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ManagedThread<L extends RunnableWithException> {

    private static final Logger LOG = LoggerFactory.getLogger(ManagedThread.class);

    private final AtomicBoolean keepRunningHolder = new AtomicBoolean(false);
    private final AtomicReference<Thread> threadHolder = new AtomicReference<>();

    private final String name;

    private final L logic;

    public ManagedThread(@Nonnull String name, @Nonnull L logic) {
        super();

        this.name = name;
        this.logic = logic;
    }

    public void shutdown() {
        if (!keepRunningHolder.compareAndSet(true, false)) {
            LOG.info("Already shutdown");
            return;
        }

        Thread thread = threadHolder.getAndSet(null);
        if (thread == null) {
            LOG.info("Thread already cleared");
            return;
        }

        boolean interrupted = false;
        try {
            thread.join(250);
        } catch (InterruptedException e) {
            interrupted = true;
        }
        thread.interrupt();
        if (!interrupted) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                LOG.info("Interrupted");
            }
        }
    }

    public void start() {
        if (!keepRunningHolder.compareAndSet(false, true)) {
            LOG.info("Already started");
            return;
        }

        Thread thread = new Thread(this::logicLoop, name);
        if (!threadHolder.compareAndSet(null, thread)) {
            LOG.info("Thread already set");
            return;
        }

        thread.start();
    }

    private void logicLoop() {
        try {
            long resetAfterMillis = TimeUnit.SECONDS.toMillis(60);
            long firstFailureTimeMillis = 0;
            int failures = 0;
            long backoffMillis = 1000;
            while (keepRunningHolder.get()) {
                try {
                    logic.run();
                } catch (Exception e) {
                    LOG.error("Thread failed", e);
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis > firstFailureTimeMillis + resetAfterMillis) {
                        firstFailureTimeMillis = currentTimeMillis;
                        failures = 0;
                    }
                    if (failures++ > 3) {
                        LOG.warn("Thread failing too fast - sleeping for {} ms", backoffMillis);
                        try {
                            //noinspection BusyWait
                            Thread.sleep(backoffMillis);
                        } catch (InterruptedException ie) {
                            LOG.warn("Thread interrupted");
                            return;
                        }
                        backoffMillis = backoffMillis * 2;
                    }
                }
            }
            LOG.info("Terminated gracefully");
        } catch (Throwable t) {
            LOG.info("Terminated abnormally", t);
        }
    }

    public boolean isRunning() {
        boolean keepRunning = keepRunningHolder.get();
        if (!keepRunning) {
            return false;
        }
        Thread thread = threadHolder.get();
        if (thread == null) {
            return false;
        }
        return thread.isAlive();
    }

    public L getLogic() {
        return logic;
    }

}
