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

package de.hasait.sprinkler.service.relay.gpio;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConfigurationProperties(prefix = "relay.gpio")
public class GpioRelayConfiguration {

    private String relayToGpio;

    private List<RelayToGpio> relayToGpios;

    public String getRelayToGpio() {
        return relayToGpio;
    }

    public List<RelayToGpio> getRelayToGpios() {
        return relayToGpios;
    }

    public void setRelayToGpio(String relayToGpio) {
        this.relayToGpio = relayToGpio;

        List<RelayToGpio> relayToGpios = new ArrayList<>();

        if (!StringUtils.isEmpty(relayToGpio)) {

            String entryRegex = ",?([^=]+)=([^@]+)@([0-9]+)";
            String fullRegex = "(?:" + entryRegex + ")+";
            if (!relayToGpio.matches(fullRegex)) {
                throw new IllegalArgumentException("Invalid configuration for relayToGpio: " + relayToGpio);
            }
            Pattern pattern = Pattern.compile(entryRegex);
            Matcher matcher = pattern.matcher(relayToGpio);
            while (matcher.find()) {
                relayToGpios.add(new RelayToGpio(matcher.group(1), matcher.group(2), Integer.parseInt(matcher.group(3))));
            }

        }

        this.relayToGpios = relayToGpios;
    }

    static class RelayToGpio {

        private final String relayId;
        private final String gpioProvider;
        private final int gpioAddress;

        public RelayToGpio(String relayId, String gpioProvider, int gpioAddress) {
            this.relayId = relayId;
            this.gpioProvider = gpioProvider;
            this.gpioAddress = gpioAddress;
        }

        public int getGpioAddress() {
            return gpioAddress;
        }

        public String getGpioProvider() {
            return gpioProvider;
        }

        public String getRelayId() {
            return relayId;
        }

    }

}
