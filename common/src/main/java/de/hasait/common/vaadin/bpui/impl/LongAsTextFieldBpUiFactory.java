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

package de.hasait.common.vaadin.bpui.impl;

import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import de.hasait.common.util.BeanProperty;

@Service
public class LongAsTextFieldBpUiFactory extends AbstractNumberAsTextFieldBpUiFactory<Long> {

    public LongAsTextFieldBpUiFactory() {
        super(Long.class, Long.TYPE);
    }

    @Override
    @Nonnull
    protected <B, P extends Long> Converter<String, Long> createConverter(@Nonnull Class<B> beanClass, @Nonnull BeanProperty<B, P> beanProperty) {
        return new StringToLongConverter("Invalid");
    }

}
