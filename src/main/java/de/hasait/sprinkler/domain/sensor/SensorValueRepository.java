package de.hasait.sprinkler.domain.sensor;

import de.hasait.sprinkler.domain.SearchableRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SensorValueRepository extends SearchableRepository<SensorValuePO, Long> {

    @Override
    @Query("SELECT v FROM SensorValuePO v WHERE CAST(v.intValue AS string) LIKE %:search%")
    Page<SensorValuePO> search(@Param("search") String search, Pageable pageable);

    @Override
    @Query("SELECT COUNT(v) FROM SensorValuePO v WHERE CAST(v.intValue AS string) LIKE %:search%")
    long searchCount(String search);

    @Transactional
    @Modifying
    @Query("DELETE FROM SensorValuePO v where v.dateTime < :dateTime")
    void deleteAllBefore(LocalDateTime dateTime);

    List<SensorValuePO> findTop2BySensorOrderByIdDesc(SensorPO sensorPO);

}
