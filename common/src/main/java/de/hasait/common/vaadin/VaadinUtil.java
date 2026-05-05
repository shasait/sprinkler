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

package de.hasait.common.vaadin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.DataProvider;

import de.hasait.common.service.HasId;
import de.hasait.common.service.Store;
import de.hasait.common.util.Util;

public class VaadinUtil {

    private final static Class<?> SEARCHABLE_REPO_CLASS;
    private final static Constructor<?> JPA_REPO_DP_CLASS_CONSTRUCTOR;

    static {
        Class<?> srClass;
        Constructor<?> dpContructor;
        try {
            srClass = Class.forName("de.hasait.common.jpa.domain.SearchableRepository");
            Class<?> dpClass = Class.forName("de.hasait.common.vaadinjpa.JpaRepositoryDataProvider");
            dpContructor = dpClass.getConstructor(srClass);
        } catch (ClassNotFoundException e) {
            // no jpa support
            srClass = null;
            dpContructor = null;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        SEARCHABLE_REPO_CLASS = srClass;
        JPA_REPO_DP_CLASS_CONSTRUCTOR = dpContructor;
    }

    public static <T> ComboBox<T> createDropDown(String label, List<T> items, ItemLabelGenerator<T> labelGenerator) {
        ComboBox<T> comboBox = new ComboBox<>(label);
        comboBox.setItems(items);
        comboBox.setItemLabelGenerator(labelGenerator);
        return comboBox;
    }

    public static <B extends HasId<?>, S extends Store<B, ?>> DataProvider<B, String> createBestDataProvider(S store) {
        if (SEARCHABLE_REPO_CLASS != null && SEARCHABLE_REPO_CLASS.isInstance(store)) {
            try {
                return (DataProvider<B, String>) JPA_REPO_DP_CLASS_CONSTRUCTOR.newInstance(store);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        // TODO implement filters, sorting ...
        return DataProvider.fromFilteringCallbacks(q -> store.listAllBeans().stream(), q -> Util.longToInt(store.countAllBeans()));
    }

}
