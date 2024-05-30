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

package de.hasait.sprinkler.service.relay.provider.gpiod;

import de.hasait.sprinkler.service.relay.provider.AbstractPinBasedRelayProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@Service
public class GpioDRelayProvider extends AbstractPinBasedRelayProvider {

    public static final String PROVIDER_ID = "gpiod";

    private static final Logger LOG = LoggerFactory.getLogger(GpioDRelayProvider.class);

    private static final String GPIO_FIND_COMMAND = "/usr/bin/gpiofind";
    private static final String GPIO_GET_COMMAND = "/usr/bin/gpioget";
    private static final String GPIO_SET_COMMAND = "/usr/bin/gpioset";
    private static final List<String> COMMANDS = List.of(GPIO_FIND_COMMAND, GPIO_GET_COMMAND, GPIO_SET_COMMAND);

    public GpioDRelayProvider() {
        super(PROVIDER_ID, COMMANDS.stream() //
                .filter(command -> !new File(command).exists()) //
                .findFirst() //
                .map(command -> command + " does not exist") //
                .orElse(null) //
        );
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Relays via gpiod utils: " + String.join(", ", COMMANDS);
    }

    @Nullable
    @Override
    protected String validateConfigNonEmpty(@Nonnull String config) {
        if (config.contains("\n")) {
            return "Cannot contain newlines";
        }
        return null;
    }

    @Override
    protected void changePin(String address, boolean active) {
        String value = active ? "1" : "0";
        List<String> gpioFindResult = execute(GPIO_FIND_COMMAND, address);
        if (gpioFindResult.size() != 2) {
            throw new RuntimeException("Unexpected gpiofind result for " + address + ": " + gpioFindResult);
        }
        execute(GPIO_SET_COMMAND, gpioFindResult.get(0), gpioFindResult.get(1) + "=" + value);
    }

    @Override
    protected boolean initPin(String address) {
        changePin(address, false);
        return false;
    }

    private List<String> execute(String... command) {
        LOG.debug("Executing command... {}", List.of(command));

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            List<String> result = new ArrayList<>();
            try (var processOutputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
                String line;
                while ((line = processOutputReader.readLine()) != null) {
                    result.addAll(Arrays.asList(StringUtils.split(line)));
                }
                int exitCode;
                try {
                    exitCode = process.waitFor();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (exitCode != 0) {
                    throw new RuntimeException("exitCode: " + exitCode);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Command failed: " + List.of(command), e);
        }
    }

}
