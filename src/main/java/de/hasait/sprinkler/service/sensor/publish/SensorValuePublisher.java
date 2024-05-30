package de.hasait.sprinkler.service.sensor.publish;

import de.hasait.sprinkler.domain.sensor.SensorValuePO;

public interface SensorValuePublisher {

    void publish(SensorValuePO value) throws Exception;

}
