package de.hasait.sprinkler.service.sensor.publish;

import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class MqttSensorValuePublisher implements SensorValuePublisher {

    private static final Logger LOG = LoggerFactory.getLogger(MqttSensorValuePublisher.class);

    private final MqttConfiguration configuration;

    private final IMqttClient client;

    public MqttSensorValuePublisher(MqttConfiguration configuration) throws MqttException {
        this.configuration = configuration;

        this.client = new MqttClient(configuration.getUri(), configuration.getClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setUserName(configuration.getUsername());
        options.setPassword(configuration.getPassword());
        client.connect(options);

        LOG.info("Connected to MQTT: {}", configuration.getUri());
    }

    @Override
    public void publish(SensorValuePO value) throws Exception {
        String messageContent = "{\"value\":" + value.getIntValue() + "}";
        String topic = configuration.getTopic() + "/" + value.getSensor().getName();

        LOG.debug("Publishing MQTT to topic {}: {}", topic, messageContent);

        MqttMessage msg = new MqttMessage(messageContent.getBytes(StandardCharsets.UTF_8));
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(topic, msg);
        LOG.info("Published MQTT to topic {}: {}", topic, messageContent);
    }

}
