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

package de.hasait.common.service;

import java.util.List;
import java.util.Optional;

public interface Store<B extends HasId<ID>, ID> {

    default Class<B> getBeanClass() {
        throw new RuntimeException("Override in extending interface");
    }

    List<B> listAllBeans();

    long countAllBeans();

    Optional<B> findBeanById(ID id);

    <EB extends B> EB addOrUpdateBean(EB bean);

    void deleteBean(B bean);

}
