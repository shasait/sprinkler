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

import java.util.List;
import java.util.Locale;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Service;

import de.hasait.common.util.I18nSupport;

@Service
public class MessageSourceI18NProvider implements I18NProvider {

    private final I18nSupport i18nSupport;

    public MessageSourceI18NProvider(I18nSupport i18nSupport) {
        this.i18nSupport = i18nSupport;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return i18nSupport.getSupportedLocales();
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        return i18nSupport.msgldka(locale, key, key, params);
    }

}

