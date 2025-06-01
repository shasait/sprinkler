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

package de.hasait.sprinkler.service.relay.provider.taspow;

import de.hasait.sprinkler.service.relay.provider.AbstractPinBasedRelayProvider;
import de.hasait.common.util.MessageFormatUtil;
import de.hasait.common.util.Util;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 */
@Service
public class TasmotaPowerRelayProvider extends AbstractPinBasedRelayProvider {

    public static final String PROVIDER_ID = "taspow";

    private static final Logger LOG = LoggerFactory.getLogger(TasmotaPowerRelayProvider.class);

    private static final String EXPECTED_CONFIG_MESSAGE = "Expected: <host or ip>;<int index>";
    private static final String SET_STATE_URL = "http://{0}/cm?cmnd=Power{1}%20{2}";

    public TasmotaPowerRelayProvider() {
        super(PROVIDER_ID, null);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Relays via Tasmota Power command via HTTP";
    }

    @Nullable
    @Override
    protected String validateConfigNonEmpty(@Nonnull String config) {
        try {
            parseConfig(config);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return null;
    }

    @Override
    protected void changePin(String address, boolean active) {
        String value = active ? "1" : "0";
        Configuration configuration = parseConfig(address);
        String url = MessageFormatUtil.format(SET_STATE_URL, configuration.host, configuration.index, value);
        try {
            IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean initPin(String address) {
        changePin(address, false);
        return false;
    }

    private Configuration parseConfig(@Nonnull String config) {
        String trimmedConfig = config.trim();
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

    private static class Configuration {
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
