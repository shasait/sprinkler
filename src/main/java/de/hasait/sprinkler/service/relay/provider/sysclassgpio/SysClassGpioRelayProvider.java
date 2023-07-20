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
import de.hasait.sprinkler.util.MessageFormatUtil;
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

    private final String disabledReason;

    public SysClassGpioRelayProvider() {
        super(PROVIDER_ID);

        if (!new File(SYS_CLASS_GPIO_EXPORT).exists()) {
            disabledReason = SYS_CLASS_GPIO_EXPORT + " does not exist";
        } else if (!new File(SYS_CLASS_GPIO_EXPORT).canWrite()) {
            disabledReason = SYS_CLASS_GPIO_EXPORT + " is not writable";
        } else {
            disabledReason = null;
        }

        if (disabledReason != null) {
            LOG.warn("{} - {}", PROVIDER_ID, disabledReason);
        }
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Relays via " + SYS_CLASS_GPIO_VALUE_FORMAT;
    }

    @Nullable
    @Override
    public String getDisabledReason() {
        return disabledReason;
    }

    @Override
    protected void changePin(int address, boolean active) {
        if (disabledReason != null) {
            LOG.warn("Cannot change pin {} to {} - {}", address, active, disabledReason);
            return;
        }

        String value = active ? "1" : "0";
        echoInto(MessageFormatUtil.format(SYS_CLASS_GPIO_VALUE_FORMAT, address), value, 100);
    }

    @Override
    protected boolean initPin(int address) {
        if (disabledReason != null) {
            LOG.warn("Cannot init pin {} - {}", address, disabledReason);
            return false;
        }

        echoInto(SYS_CLASS_GPIO_EXPORT, Integer.toString(address), 2000);
        echoInto(MessageFormatUtil.format(SYS_CLASS_GPIO_DIRECTION_FORMAT, address), GPIO_DIRECTION_OUT, 1000);
        return false;
    }

    @Override
    protected void shutdown() {
        if (disabledReason != null) {
            return;
        }

        pins.forEach((address, active) -> echoInto(SYS_CLASS_GPIO_UNEXPORT, Integer.toString(address), 0));

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
