package de.hasait.sprinkler.ui.relay;

import org.springframework.stereotype.Service;

import de.hasait.common.jpa.domain.driver.DriverInstanceEO;
import de.hasait.common.util.BeanProperties;
import de.hasait.common.util.BeanProperty;
import de.hasait.common.vaadin.bpui.BpCustomizer;
import de.hasait.sprinkler.domain.relay.RelayPO;
import de.hasait.sprinkler.service.relay.RelayService;

@Service
public class RelayPOCustomizer implements BpCustomizer<RelayPO> {

    private final RelayService relayService;

    public RelayPOCustomizer(RelayService relayService) {
        this.relayService = relayService;
    }

    @Override
    public Class<RelayPO> getBeanClass() {
        return RelayPO.class;
    }

    @Override
    public void customizeBeanProperties(BeanProperties<RelayPO> beanProperties) {
        BeanProperty<RelayPO, Boolean> activeBeanProperty = new BeanProperty<>(RelayPO.class, Boolean.class, "relayActive", relay -> {
            DriverInstanceEO driverInstance = relay.getDriverInstance();
            try {
                return relayService.isActive(driverInstance);
            } catch (RuntimeException e) {
                return false;
            }
        }, null);
        beanProperties.add(activeBeanProperty);
    }

}
