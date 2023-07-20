package de.hasait.sprinkler.domain.sensor;

import de.hasait.sprinkler.domain.SearchableRepository;
import de.hasait.sprinkler.domain.relay.RelayPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends SearchableRepository<SensorPO, Long> {

    @Override
    @Query("SELECT s FROM SensorPO s WHERE s.name LIKE %:search%")
    Page<SensorPO> search(String search, Pageable pageable);

    @Override
    @Query("SELECT COUNT(s) FROM SensorPO s WHERE s.name LIKE %:search%")
    long searchCount(String search);

}
