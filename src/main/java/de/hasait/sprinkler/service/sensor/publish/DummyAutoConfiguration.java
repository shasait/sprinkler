package de.hasait.sprinkler.service.sensor.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DummyAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    SensorValuePublisher sensorValuePublisher() {
        LOG.info("Using {} as {}", DummySensorValuePublisher.class, SensorValuePublisher.class);
        return new DummySensorValuePublisher();
    }

}
