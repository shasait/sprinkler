package de.hasait.sprinkler.service.sensor;

import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.domain.sensor.SensorValueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorService {

    private static final Logger LOG = LoggerFactory.getLogger(SensorService.class);

    private final SensorValueRepository valueRepository;

    public SensorService(SensorValueRepository valueRepository) {
        this.valueRepository = valueRepository;
    }

    public int determineChange(SensorPO sensorPO) {
        List<SensorValuePO> list = valueRepository.findTop2BySensorOrderByIdDesc(sensorPO);
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
        return valueRepository.findTop2BySensorOrderByIdDesc(sensorPO);
    }

}
