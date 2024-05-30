package de.hasait.sprinkler.service.sensor;

import de.hasait.sprinkler.domain.sensor.SensorPO;
import de.hasait.sprinkler.domain.sensor.SensorRepository;
import de.hasait.sprinkler.domain.sensor.SensorValuePO;
import de.hasait.sprinkler.domain.sensor.SensorValueRepository;
import de.hasait.sprinkler.service.sensor.provider.SensorProviderService;
import de.hasait.sprinkler.service.sensor.provider.SensorValue;
import de.hasait.sprinkler.service.sensor.publish.SensorValuePublisher;
import de.hasait.sprinkler.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class SensorTaskService {

    private static final Logger LOG = LoggerFactory.getLogger(SensorTaskService.class);

    private final SensorRepository repository;
    private final SensorValueRepository valueRepository;

    private final SensorProviderService providerService;

    private final TaskScheduler taskScheduler;

    private final ConcurrentHashMap<Long, List<ScheduledFuture<?>>> scheduledFutures = new ConcurrentHashMap<>();

    private final SensorValuePublisher sensorValuePublisher;

    public SensorTaskService(SensorRepository repository, SensorValueRepository valueRepository, SensorProviderService providerService, TaskScheduler taskScheduler, SensorValuePublisher sensorValuePublisher) {
        this.repository = repository;
        this.valueRepository = valueRepository;
        this.providerService = providerService;
        this.taskScheduler = taskScheduler;
        this.sensorValuePublisher = sensorValuePublisher;

        SensorPOListener.sensorTaskService = this;

        repository.findAll().forEach(this::createOrUpdateScheduledTask);
    }

    public void postPersistSchedulePO(SensorPO po) {
        LOG.debug("postPersistSchedulePO: {}", po);

        createOrUpdateScheduledTask(po);
    }

    public void postUpdateSchedulePO(SensorPO po) {
        LOG.debug("postUpdateSchedulePO: {}", po);

        createOrUpdateScheduledTask(po);
    }

    public void preRemoveSchedulePO(SensorPO po) {
        LOG.debug("preRemoveSchedulePO: {}", po);

        cancelScheduledTask(po.getId());
    }

    private void cancelScheduledTask(long sensorId) {
        LOG.debug("cancelScheduledTask {}...", sensorId);

        List<ScheduledFuture<?>> oldSchedules = scheduledFutures.remove(sensorId);
        if (oldSchedules != null) {
            oldSchedules.forEach(it -> it.cancel(true));
        }
    }

    private void registerScheduledFuture(long sensorId, ScheduledFuture<?> scheduledFuture) {
        LOG.debug("registerScheduledFuture {}...", sensorId);
        Util.registerScheduledFuture(sensorId, scheduledFuture, scheduledFutures);
    }

    private void createOrUpdateScheduledTask(SensorPO po) {
        long sensorId = po.getId();

        cancelScheduledTask(sensorId);

        boolean enabled = true;
        String cronExpression = po.getCronExpression();
        if (enabled && cronExpression != null) {
            CronTrigger cronTrigger = new CronTrigger(cronExpression);
            ReadAndSaveSensorValueTask task = new ReadAndSaveSensorValueTask(sensorId);
            registerScheduledFuture(sensorId, taskScheduler.schedule(task, cronTrigger));
        }
    }

    private void readAndSaveSensorValue(long sensorId) {
        SensorPO sensorPO = repository.findById(sensorId).orElseThrow();
        LOG.debug("Reading sensor {}...", sensorPO.getName());
        SensorValue sensorValue = providerService.obtainValue(sensorPO.getProviderId(), sensorPO.getProviderConfig());

        SensorValuePO sensorValuePO = new SensorValuePO();
        sensorValuePO.setSensor(sensorPO);
        LocalDateTime dateTime = sensorValue.getDateTime();
        sensorValuePO.setDateTime(dateTime);
        int value = sensorValue.getValue();
        sensorValuePO.setIntValue(value);
        valueRepository.saveAndFlush(sensorValuePO);
        valueRepository.deleteAllBefore(LocalDateTime.now().minusMonths(2));

        try {
            sensorValuePublisher.publish(sensorValuePO);
        } catch (Exception e) {
            LOG.warn("SensorValuePublisher failed to publish", e);
        }

        LOG.debug("Saved sensor value {} from {}", value, dateTime);
    }

    private class ReadAndSaveSensorValueTask implements Runnable {

        private final long sensorId;

        public ReadAndSaveSensorValueTask(long sensorId) {
            this.sensorId = sensorId;
        }

        @Override
        public void run() {
            readAndSaveSensorValue(sensorId);
        }
    }

}
