package de.hasait.sprinkler.domain.schedule;

import de.hasait.sprinkler.domain.SearchableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends SearchableRepository<SchedulePO, Long> {

    @Override
    @Query("SELECT s FROM SchedulePO s WHERE s.cronExpression LIKE %:search%")
    Page<SchedulePO> search(String search, Pageable pageable);

    @Override
    @Query("SELECT COUNT(s) FROM SchedulePO s WHERE s.cronExpression LIKE %:search%")
    long searchCount(String search);

}
