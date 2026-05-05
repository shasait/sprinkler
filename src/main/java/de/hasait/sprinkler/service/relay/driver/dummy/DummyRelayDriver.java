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

package de.hasait.sprinkler.service.relay.driver.dummy;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hasait.sprinkler.service.relay.driver.AbstractPinBasedRelayDriver;

/**
 *
 */
@Service
public class DummyRelayDriver extends AbstractPinBasedRelayDriver<String> {

    public static final String PROVIDER_ID = "dummy";

    private static final Logger LOG = LoggerFactory.getLogger(DummyRelayDriver.class);

    public DummyRelayDriver() {
        super(PROVIDER_ID, null);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Dummy for testing";
    }

    @Override
    public @Nonnull String parseDriverConfigText(@Nullable String driverConfigText) {
        return driverConfigText;
    }

    @Override
    protected void changePin(String address, boolean active) {
        LOG.debug("changePin: {}, {}", address, active);
    }

    @Override
    protected boolean initPin(String address) {
        return false;
    }

}
