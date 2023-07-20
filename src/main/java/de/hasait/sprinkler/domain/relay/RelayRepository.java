package de.hasait.sprinkler.domain.relay;

import de.hasait.sprinkler.domain.SearchableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RelayRepository extends SearchableRepository<RelayPO, Long> {

    @Override
    @Query("SELECT r FROM RelayPO r WHERE r.name LIKE %:search%")
    Page<RelayPO> search(String search, Pageable pageable);

    @Override
    @Query("SELECT COUNT(r) FROM RelayPO r WHERE r.name LIKE %:search%")
    long searchCount(String search);

}
