/*
 * Copyright (C) 2024 by Sebastian Hasait (sebastian at hasait dot de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hasait.sprinkler.domain.schedule;

import de.hasait.common.domain.SearchableRepository;
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
