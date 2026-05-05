/*
 * Copyright (C) 2025 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.service.sensor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hasait.common.service.driver.AbstractDriverService;
import de.hasait.common.service.driver.DriverInstance;
import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.domain.sensor.SensorValueRepository;
import de.hasait.sprinkler.service.sensor.driver.SensorDriver;
import de.hasait.sprinkler.service.sensor.driver.SensorValue;

@Service
public class SensorService extends AbstractDriverService<SensorDriver<?>, SensorPO, DriverInstance> {

    private static final Logger LOG = LoggerFactory.getLogger(SensorService.class);

    private final SensorValueRepository valueRepository;

    public SensorService(SensorDriver<?>[] drivers, SensorValueRepository valueRepository) {
        super(drivers, SensorPO.class, SensorPO::getDriverInstance);

        this.valueRepository = valueRepository;
    }

    public SensorValue obtainValue(SensorPO sensorPO) {
        return driverFunction(sensorPO, (ignored, driver, di) -> obtainValueInternal(driver, di.getDriverConfig()));
    }

    private <C> SensorValue obtainValueInternal(SensorDriver<C> driver, String driverConfigText) {
        C config = driver.parseDriverConfigText(driverConfigText);
        return driver.obtainValue(config);
    }

    public int determineChange(SensorPO sensorPO) {
        List<SensorValuePO> list = valueRepository.findTop2BySensorOrderByDateTimeDesc(sensorPO);
        if (list.size() < 2) {
            return 0;
        }
        SensorValuePO rv0 = list.get(0);
        SensorValuePO rv1 = list.get(1);
        int dv = rv0.getIntValue() - rv1.getIntValue();
        // int minutes = (int) Duration.between(rv1.getDateTime(), rv0.getDateTime()).toMinutes();
        // return dv / minutes;
        return dv;
    }

    public List<SensorValuePO> getLastValues(SensorPO sensorPO) {
        return valueRepository.findTop2BySensorOrderByDateTimeDesc(sensorPO);
    }

}
