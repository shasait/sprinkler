package de.hasait.sprinkler.domain.sensor;

import de.hasait.sprinkler.service.IdAndVersion;
import de.hasait.sprinkler.service.sensor.SensorPOListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "SENSOR")
@EntityListeners(SensorPOListener.class)
public class SensorPO implements IdAndVersion {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private long version;

    @Size(min = 1, max = 32)
    @NotNull
    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

    @Size(min = 1, max = 32)
    @NotNull
    @Column(name = "PROVIDER_ID", nullable = false)
    private String providerId;

    @Size(max = 128)
    @NotNull
    @Column(name = "PROVIDER_CONFIG", nullable = false)
    private String providerConfig;

    @Size(min = 1, max = 64)
    @NotNull
    @Column(name = "CRON_EXPRESSION", nullable = false)
    private String cronExpression;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderConfig() {
        return providerConfig;
    }

    public void setProviderConfig(String providerConfig) {
        this.providerConfig = providerConfig;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

}
