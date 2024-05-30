package de.hasait.sprinkler.service.sensor.publish;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MqttConfiguration.class)
public class MqttAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MqttAutoConfiguration.class);

    @Bean
    @ConditionalOnProperty(value = "sprinkler.mqtt.uri")
    SensorValuePublisher sensorValuePublisher(MqttConfiguration configuration) throws MqttException {
        LOG.info("Using {} as {}", MqttSensorValuePublisher.class, SensorValuePublisher.class);
        return new MqttSensorValuePublisher(configuration);
    }

}
