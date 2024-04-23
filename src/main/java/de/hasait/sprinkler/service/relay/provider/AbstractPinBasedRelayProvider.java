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

package de.hasait.sprinkler.service.relay.provider;


import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public abstract class AbstractPinBasedRelayProvider implements RelayProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPinBasedRelayProvider.class);

    protected final ConcurrentHashMap<String, Integer> pins = new ConcurrentHashMap<>();
    private final String id;

    protected AbstractPinBasedRelayProvider(String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public final String getId() {
        return id;
    }

    @Nullable
    @Override
    public final String validateConfig(@Nullable String config) {
        if (config == null || config.trim().isEmpty()) {
            return "Cannot be empty";
        }
        return validateConfigNonEmpty(config);
    }

    protected abstract String validateConfigNonEmpty(@Nonnull String config);

    @Override
    public final boolean isActive(@Nonnull String config) {
        String address = config.trim();
        return pins.computeIfAbsent(address, ignored -> initPin01(address)) > 0;
    }

    @Override
    public final void changeActive(@Nonnull String config, int amount) {
        String address = config.trim();
        pins.compute(address, (key, current) -> {
            int state = current == null ? initPin01(address) : current;
            int newState = Math.max(0, state + amount);
            if (state > 0 && newState == 0) {
                LOG.info("PIN {} deactivated - state: {} -> {}", address, state, newState);
                changePin(address, false);
            } else if (state == 0 && newState > 0) {
                LOG.info("PIN {} activated - state: {} -> {}", address, state, newState);
                changePin(address, true);
            } else {
                LOG.info("PIN {} not changed - state: {} -> {}", address, state, newState);
            }
            return newState;
        });
    }

    protected abstract void changePin(String address, boolean active);

    private int initPin01(String address) {
        return initPin(address) ? 1 : 0;
    }

    /**
     * Initialize pin and return current state (true=active).
     */
    protected abstract boolean initPin(String address);

    @PreDestroy
    protected void shutdown() {
        pins.clear();
    }

}
