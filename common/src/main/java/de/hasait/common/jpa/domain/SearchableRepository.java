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

package de.hasait.common.jpa.domain;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import de.hasait.common.service.Store;

@NoRepositoryBean
public interface SearchableRepository<PO extends PersistantObject> extends JpaRepository<PO, Long>, Store<PO, Long> {

    Page<PO> search(String search, Pageable pageable);

    long searchCount(String search);

    @Override
    default List<PO> listAllBeans() {
        return findAll();
    }

    @Override
    default long countAllBeans() {
        return count();
    }

    @Override
    default Optional<PO> findBeanById(@Nonnull Long id) {
        return findById(id);
    }

    @Nonnull
    @Override
    default <EB extends PO> EB addOrUpdateBean(@Nonnull EB bean) {
        return saveAndFlush(bean);
    }

    @Override
    default void deleteBean(PO bean) {
        delete(bean);
    }

}
