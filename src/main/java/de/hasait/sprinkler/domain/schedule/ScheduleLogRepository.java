package de.hasait.sprinkler.domain.schedule;

import de.hasait.sprinkler.domain.SearchableRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ScheduleLogRepository extends SearchableRepository<ScheduleLogPO, Long> {

    @Override
    @Query("SELECT l FROM ScheduleLogPO l WHERE l.relayName LIKE %:search%")
    Page<ScheduleLogPO> search(@Param("search") String search, Pageable pageable);

    @Override
    @Query("SELECT COUNT(l) FROM ScheduleLogPO l WHERE l.relayName LIKE %:search%")
    long searchCount(String search);

    @Transactional
    @Modifying
    @Query("DELETE FROM ScheduleLogPO l where l.start < :start")
    void deleteAllBefore(LocalDateTime start);

}
