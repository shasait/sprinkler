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

    protected final ConcurrentHashMap<Integer, Integer> pins = new ConcurrentHashMap<>();
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
    public String validateConfig(@Nonnull String config) {
        try {
            Integer.parseInt(config);
        } catch (NumberFormatException e) {
            return "Not a number";
        }
        return null;
    }

    @Override
    public final boolean isActive(@Nonnull String config) {
        int address = Integer.parseInt(config);
        return pins.computeIfAbsent(address, ignored -> initPin01(address)) > 0;
    }

    @Override
    public final void changeActive(@Nonnull String config, int amount) {
        int address = Integer.parseInt(config);
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

    protected abstract void changePin(int address, boolean active);

    private int initPin01(int address) {
        return initPin(address) ? 1 : 0;
    }

    /**
     * Initialize pin and return current state: <code>1</code> if active; <code>0</code> otherwise.
     */
    protected abstract boolean initPin(int address);

    @PreDestroy
    protected void shutdown() {
        pins.clear();
    }

}
