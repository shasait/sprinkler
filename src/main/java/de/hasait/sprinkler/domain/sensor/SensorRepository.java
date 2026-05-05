/*
 * Copyright (C) 2026 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.domain.sensor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.hasait.common.jpa.domain.SearchableRepository;

@Repository
public interface SensorRepository extends SearchableRepository<SensorPO> {

    @Override
    default Class<SensorPO> getBeanClass() {
        return SensorPO.class;
    }

    @Override
    @Query("SELECT s FROM SensorPO s WHERE s.name LIKE %:search%")
    Page<SensorPO> search(String search, Pageable pageable);

    @Override
    @Query("SELECT COUNT(s) FROM SensorPO s WHERE s.name LIKE %:search%")
    long searchCount(String search);

}
