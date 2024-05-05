package de.hasait.sprinkler.service.relay;

import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.domain.relay.RelayRepository;
import de.hasait.sprinkler.service.relay.provider.RelayProviderService;
import de.hasait.sprinkler.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Service
public class RelayService {

    private static final Logger LOG = LoggerFactory.getLogger(RelayService.class);

    private final RelayRepository repository;

    private final RelayProviderService providerService;

    private final TaskScheduler taskScheduler;

    public RelayService(RelayRepository repository, RelayProviderService providerService, TaskScheduler taskScheduler) {
        this.repository = repository;
        this.providerService = providerService;
        this.taskScheduler = taskScheduler;
    }

    public void changeActive(long relayId, int amount) {
        RelayPO relayPO = repository.findById(relayId).orElseThrow();
        providerService.changeActive(relayPO.getProviderId(), relayPO.getProviderConfig(), amount);
    }

    public ScheduledFuture<?> scheduleNow(long relayId, long durationMillis, String explanation) {
        RelayPO relayPO = repository.findById(relayId).orElseThrow();
        return taskScheduler.schedule(new RelayTask(relayPO.getId(), relayPO.getName(), durationMillis, explanation), Instant.now());
    }

    public void deactivate(long relayId) {
        RelayPO relayPO = repository.findById(relayId).orElseThrow();
        providerService.changeActive(relayPO.getProviderId(), relayPO.getProviderConfig(), -10000);
    }

    private class RelayTask implements Runnable {

        private final long relayId;
        private final String relayName;
        private final long durationMillis;
        private final String durationMillisHuman;
        private final String explanation;

        public RelayTask(long relayId, String relayName, long durationMillis, String explanation) {
            this.relayId = relayId;
            this.relayName = relayName;
            this.durationMillis = durationMillis;
            this.durationMillisHuman = Util.millisToHuman(durationMillis, 3);
            this.explanation = explanation;
        }

        @Override
        public void run() {
            if (LOG.isInfoEnabled()) {
                LOG.info("{} activating for {}ms ({}) - {}...", relayName, durationMillis, durationMillisHuman, explanation);
            }
            changeActive(relayId, 1);
            try {
                Thread.sleep(durationMillis);
            } catch (InterruptedException e) {
                LOG.info("Sleeping was interrupted");
            }
            changeActive(relayId, -1);
            if (LOG.isInfoEnabled()) {
                LOG.info("{} deactivated", relayName);
            }
        }

    }

}
