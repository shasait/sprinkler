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

package de.hasait.sprinkler.service.relay.driver.taspow;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hasait.common.util.MessageFormatUtil;
import de.hasait.common.util.Util;
import de.hasait.sprinkler.service.relay.driver.AbstractPinBasedRelayDriver;

/**
 *
 */
@Service
public class TasmotaPowerRelayDriver extends AbstractPinBasedRelayDriver<TasmotaPowerRelayDriver.Configuration> {

    public static final String PROVIDER_ID = "taspow";

    private static final Logger LOG = LoggerFactory.getLogger(TasmotaPowerRelayDriver.class);

    private static final String EXPECTED_CONFIG_MESSAGE = "Expected: <host or ip>;<int index>";
    private static final String SET_STATE_URL = "http://{0}/cm?cmnd=Power{1}%20{2}";

    public TasmotaPowerRelayDriver() {
        super(PROVIDER_ID, null);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Relays via Tasmota Power command via HTTP";
    }

    @Override
    protected void changePin(Configuration config, boolean active) {
        String value = active ? "1" : "0";
        String url = MessageFormatUtil.format(SET_STATE_URL, config.host, config.index, value);
        try {
            IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean initPin(Configuration config) {
        changePin(config, false);
        return false;
    }

    @Override
    public @Nonnull Configuration parseDriverConfigText(@Nullable String driverConfigText) {
        String trimmedConfig = driverConfigText.trim();
        if (trimmedConfig.contains("\n")) {
            throw new IllegalArgumentException(EXPECTED_CONFIG_MESSAGE);
        }
        String[] split = trimmedConfig.split(";");
        if (split.length != 2) {
            throw new IllegalArgumentException(EXPECTED_CONFIG_MESSAGE);
        }
        Configuration configuration = new Configuration();
        int i = 0;
        configuration.setHost(split[i++]);
        Util.parse(split[i++], "int", Integer::parseInt, "index", configuration::setIndex);
        return configuration;
    }

    public static class Configuration {
        private String host;
        private int index;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

}
