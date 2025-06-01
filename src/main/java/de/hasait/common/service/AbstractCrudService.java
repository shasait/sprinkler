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

package de.hasait.common.service;

import de.hasait.common.domain.IdAndVersion;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AbstractCrudService<PO extends IdAndVersion, DTO extends IdAndVersion, RP extends JpaRepository<PO, Long>> extends AbstractListenableService {

    private final Validator validator;
    protected final RP repository;

    public AbstractCrudService(Validator validator, RP repository) {
        this.validator = validator;
        this.repository = repository;
    }

    public final long count() {
        return repository.count();
    }

    @Nullable
    public final DTO find(long id) {
        PO po = repository.findById(id).orElse(null);
        return toDTO(po);
    }

    @Nullable
    public final PO findPO(long id) {
        return repository.findById(id).orElse(null);
    }

    public final List<DTO> list() {
        List<PO> poList = repository.findAll();
        return toDTOList(poList);
    }

    public final List<DTO> list(int page, int pageSize, List<String> sortProps, List<Boolean> sortAscs) {
        Sort last = null;
        Iterator<String> sortPropI = sortProps.iterator();
        Iterator<Boolean> sortAscI = sortAscs.iterator();
        while (sortPropI.hasNext()) {
            String sortProp = sortPropI.next();
            boolean sortAsc = sortAscI.next();
            Sort curr = Sort.by(sortProp);
            if (sortAsc) {
                curr = curr.ascending();
            } else {
                curr = curr.descending();
            }
            if (last != null) {
                last = last.and(curr);
            } else {
                last = curr;
            }
        }
        Sort sort = last == null ? Sort.unsorted() : last;
        List<PO> poList = repository.findAll(PageRequest.of(page, pageSize, sort)).getContent();
        return toDTOList(poList);
    }

    public final void createOrUpdate(@Nonnull DTO dto) {
        if (dto.getId() == null) {
            create(dto);
        } else {
            update(dto);
        }
    }

    public final void create(@Nonnull DTO dto) {
        PO po = newPO();
        toPO(dto, po);

        validate(po);

        repository.saveAndFlush(po);
        afterCreateOrUpdate(po);
        notifyListeners();
    }

    public final void update(@Nonnull DTO dto) {
        PO po = repository.findById(dto.getId()).orElseThrow();
        toPO(dto, po);

        validate(po);

        repository.saveAndFlush(po);
        afterCreateOrUpdate(po);
        notifyListeners();
    }

    public final void delete(@Nonnull DTO dto) {
        PO po = repository.findById(dto.getId()).orElseThrow();
        toPO(dto, po);
        repository.delete(po);
        afterDelete(po);
        notifyListeners();
    }

    @Nonnull
    protected abstract PO newPO();

    protected abstract DTO toDTO(PO po);

    protected final List<DTO> toDTOList(List<PO> poList) {
        List<DTO> dtoList = new ArrayList<>();
        for (PO po : poList) {
            dtoList.add(toDTO(po));
        }
        return dtoList;
    }

    protected abstract void toPOwoIdAndVersion(DTO dto, PO po);

    protected void afterCreateOrUpdate(PO po) {
        // empty
    }

    protected void afterDelete(PO po) {
        // empty
    }

    private void toPO(DTO dto, PO po) {
        if (po.getVersion() != dto.getVersion()) {
            throw new RuntimeException("Modified meanwhile (version_db=" + po.getVersion() + " vs. version_ui=" + dto.getVersion() + ")");
        }
        po.setVersion(po.getVersion() + 1);
        toPOwoIdAndVersion(dto, po);
    }

    protected void validate(PO po) {
        Set<ConstraintViolation<PO>> constraintViolations = validator.validate(po);
        if (!constraintViolations.isEmpty()) {
            throw new ValidationException(constraintViolations.toString());
        }
    }

}
