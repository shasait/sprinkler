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

package de.hasait.common.vaadin.bpui;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.hasait.common.util.BeanProperties;
import de.hasait.common.util.BeanProperty;

/**
 * BeanProperty UI Service.
 */
@Service
public final class BpUiService {

    private static final Logger LOG = LoggerFactory.getLogger(BpUiService.class);

    private final Set<String> ignoredPropertyNames = new HashSet<>();

    private final List<BpUiFactory<?, ?>> propertyUiFactories;
    private final List<BpCustomizer<?>> bpCustomizers;

    private final Map<Class<?>, List<PuiFactoryInvocation>> propertyWidgetsCache = new ConcurrentHashMap<>();

    public BpUiService(List<BpUiFactory<?, ?>> propertyUiFactories, List<BpCustomizer<?>> bpCustomizers) {
        this.propertyUiFactories = new ArrayList<>(propertyUiFactories);
        this.propertyUiFactories.sort(Comparator.comparingInt(BpUiFactory::getPriority));

        this.bpCustomizers = bpCustomizers;

        ignoredPropertyNames.add("class");
        ignoredPropertyNames.add("id");
        ignoredPropertyNames.add("version");
    }

    public <B> BpUiWidgets<B> createWidgets(Class<B> beanClass) {
        BpUiWidgets<B> result = new BpUiWidgets<>(beanClass);
        cachedDeterminePuiFactoryInvocations(beanClass).forEach(puifi -> {
            puifi.createWidget(result);
        });
        return result;
    }

    private <B> List<PuiFactoryInvocation> cachedDeterminePuiFactoryInvocations(Class<B> beanClass) {
        return (List) propertyWidgetsCache.computeIfAbsent(beanClass, ignored -> (List) determinePuiFactoryInvocations(beanClass));
    }

    private <B> List<PuiFactoryInvocation> determinePuiFactoryInvocations(Class<B> beanClass) {
        List<PuiFactoryInvocation> result = new ArrayList<>();

        BeanProperties<B> beanProperties = BeanProperty.getBeanProperties(beanClass);
        for (BpCustomizer<?> customizer : bpCustomizers) {
            if (customizer.getBeanClass().equals(beanClass)) {
                customizer.customizeBeanProperties((BeanProperties) beanProperties);
            }
        }

        for (BeanProperty<B, ?> beanProperty : beanProperties.all()) {
            if (beanProperty.getGetter() == null) {
                continue;
            }

            String propertyName = beanProperty.getName();
            if (ignoredPropertyNames.contains(propertyName)) {
                continue;
            }

            Optional<BpUiFactory<?, ?>> puiFactory = propertyUiFactories.stream().filter(f -> f.canHandle(beanClass, beanProperty)).findFirst();
            if (puiFactory.isPresent()) {
                result.add(new PuiFactoryInvocation(puiFactory.get(), beanProperty));
            } else {
                LOG.warn("Unhandled property: {}", propertyName);
            }
        }

        return result;
    }

    private record PuiFactoryInvocation(BpUiFactory<?, ?> factory, BeanProperty<?, ?> beanProperty) {
        private <B> BpUiWidget<B> createWidget(BpUiWidgets<B> bpUiWidgets) {
            return ((BpUiFactory) factory).createWidget(bpUiWidgets, beanProperty);
        }
    }
}
