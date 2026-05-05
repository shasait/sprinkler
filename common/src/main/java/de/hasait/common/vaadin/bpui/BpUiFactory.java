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

import jakarta.annotation.Nonnull;

import de.hasait.common.util.BeanProperty;

/**
 * BeanProperty UI Factory.
 *
 * @param <LB> Lower bound for beanClass. beanClass must extend LB. For pure property based widgets this is Object.
 * @param <LP>
 */
public interface BpUiFactory<LB, LP> {

    Class<LP> getPropertyClass();

    int getPriority();

    /**
     * @param <CB> Candidate beanClass
     * @param <CP> Candidate propertyClass
     */
    <CB, CP> boolean canHandle(@Nonnull Class<CB> beanClass, @Nonnull BeanProperty<CB, CP> beanProperty);

    @Nonnull
    <B extends LB, P extends LP> BpUiWidget<B> createWidget(@Nonnull BpUiWidgets<B> bpUiWidgets, @Nonnull BeanProperty<B, P> beanProperty);

}
