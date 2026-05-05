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

package de.hasait.sprinkler.service.relay.driver;


import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class AbstractPinBasedRelayDriver<C> implements RelayDriver<C> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPinBasedRelayDriver.class);

    private final String id;
    private final String disabledReason;

    protected final ConcurrentHashMap<C, Integer> pins = new ConcurrentHashMap<>();

    protected AbstractPinBasedRelayDriver(String id, String disabledReason) {
        this.id = id;
        this.disabledReason = disabledReason;

        if (disabledReason != null) {
            LOG.warn("{} - {}", id, disabledReason);
        }
    }

    @Nonnull
    @Override
    public final String getId() {
        return id;
    }

    @Nullable
    @Override
    public final String getDisabledReason() {
        return disabledReason;
    }

    @Override
    public final boolean isActive(@Nonnull C config) {
        if (disabledReason != null) {
            LOG.warn("Cannot query pin {} - {}", config, disabledReason);
            return false;
        }
        return pins.computeIfAbsent(config, ignored -> initPin01(config)) > 0;
    }

    @Override
    public final void changeActive(@Nonnull C config, int amount) {
        if (disabledReason != null) {
            LOG.warn("Cannot change pin {} - {}", config, disabledReason);
            return;
        }
        pins.compute(config, (key, current) -> {
            int state = current == null ? initPin01(config) : current;
            int newState = Math.max(0, state + amount);
            if (state > 0 && newState == 0) {
                LOG.info("PIN {} deactivated - state: {} -> {}", config, state, newState);
                changePin(config, false);
            } else if (state == 0 && newState > 0) {
                LOG.info("PIN {} activated - state: {} -> {}", config, state, newState);
                changePin(config, true);
            } else {
                LOG.info("PIN {} not changed - state: {} -> {}", config, state, newState);
            }
            return newState;
        });
    }

    protected abstract void changePin(C config, boolean active);

    private int initPin01(C config) {
        return initPin(config) ? 1 : 0;
    }

    /**
     * Initialize pin and return current state (true=active).
     */
    protected abstract boolean initPin(C config);

    @PreDestroy
    protected void shutdown() {
        pins.clear();
    }

}
