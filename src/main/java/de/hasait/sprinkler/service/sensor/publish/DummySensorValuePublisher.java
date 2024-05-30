package de.hasait.sprinkler.service.sensor.publish;

import de.hasait.sprinkler.domain.sensor.SensorValuePO;

public class DummySensorValuePublisher implements SensorValuePublisher {
    @Override
    public void publish(SensorValuePO value) {
        // nop
    }
}
