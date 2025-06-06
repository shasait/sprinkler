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

package de.hasait.sprinkler.service.relay.provider.sysclassgpio;

import de.hasait.sprinkler.service.relay.provider.AbstractPinBasedRelayProvider;
import de.hasait.common.util.MessageFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
@Service
public class SysClassGpioRelayProvider extends AbstractPinBasedRelayProvider {

    public static final String PROVIDER_ID = "sysclass-gpio";

    private static final Logger LOG = LoggerFactory.getLogger(SysClassGpioRelayProvider.class);

    private static final String SYS_CLASS_GPIO_EXPORT = "/sys/class/gpio/export";
    private static final String SYS_CLASS_GPIO_UNEXPORT = "/sys/class/gpio/unexport";
    private static final String SYS_CLASS_GPIO_DIRECTION_FORMAT = "/sys/class/gpio/gpio{0}/direction";
    private static final String SYS_CLASS_GPIO_VALUE_FORMAT = "/sys/class/gpio/gpio{0}/value";
    private static final String GPIO_DIRECTION_OUT = "out";

    private static String determineDisabledReason() {
        if (!new File(SYS_CLASS_GPIO_EXPORT).exists()) {
            return SYS_CLASS_GPIO_EXPORT + " does not exist";
        }
        if (!new File(SYS_CLASS_GPIO_EXPORT).canWrite()) {
            return SYS_CLASS_GPIO_EXPORT + " is not writable";
        }
        return null;
    }

    public SysClassGpioRelayProvider() {
        super(PROVIDER_ID, determineDisabledReason());
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Relays via " + SYS_CLASS_GPIO_VALUE_FORMAT;
    }

    @Nullable
    @Override
    protected String validateConfigNonEmpty(@Nonnull String config) {
        try {
            Integer.parseInt(config);
        } catch (NumberFormatException e) {
            return "Not a number";
        }
        return null;
    }

    @Override
    protected void changePin(String address, boolean active) {
        String value = active ? "1" : "0";
        echoInto(MessageFormatUtil.format(SYS_CLASS_GPIO_VALUE_FORMAT, address), value, 100);
    }

    @Override
    protected boolean initPin(String address) {
        echoInto(SYS_CLASS_GPIO_EXPORT, address, 2000);
        echoInto(MessageFormatUtil.format(SYS_CLASS_GPIO_DIRECTION_FORMAT, address), GPIO_DIRECTION_OUT, 1000);
        return false;
    }

    @Override
    protected void shutdown() {
        pins.forEach((address, active) -> echoInto(SYS_CLASS_GPIO_UNEXPORT, address, 0));

        super.shutdown();
    }

    private void echoInto(String path, String line, int sleepMillis) {
        LOG.debug("echo \"{}\" into \"{}\"", line, path);
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(line);
            fw.write("\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (sleepMillis > 0) {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                // continue
            }
        }
    }

}
