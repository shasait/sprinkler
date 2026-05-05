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

package de.hasait.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class I18nSupport {

    private final MessageSource messageSource;

    public I18nSupport(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String labelText(String baseKey) {
        return msgkka(baseKey + ".label");
    }

    public String headerText(String baseKey) {
        return msgkka(baseKey + ".header");
    }

    public String tooltipText(String baseKey) {
        return msgkka(baseKey + ".tooltip");
    }

    public String tooltipMarkdown(String baseKey) {
        return msgdka(null, baseKey + ".tooltipMd");
    }

    public String applicationTitle() {
        return msgkka("application.title");
    }

    public String pageTitle(String pageKey, String pageType) {
        return msgkka(pageKey + "." + pageType + ".title");
    }

    public String applicationAndPageTitle(String pageKey, String pageType) {
        return applicationTitle() + " | " + pageTitle(pageKey, pageType);
    }

    public String applicationAndPageTitle(Class<?> clazz, String pageType) {
        return applicationAndPageTitle(clazz.getSimpleName(), pageType);
    }

    public String format(String baseKey, LocalDateTime localDateTime) {
        return format(null, baseKey, localDateTime);
    }

    public String format(Locale localeMayBeNull, String baseKey, LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(msgldka(localeMayBeNull, "yyyy-MM-dd HH:mm", baseKey + ".formatter")).format(localDateTime);
    }

    public String msgkka(String key, Object... args) {
        return msgdka(key, key, args);
    }

    public String msgdka(String defaultText, String key, Object... args) {
        return msgldka(null, defaultText, key, args);
    }

    public String msgldka(Locale localeMayBeNull, String defaultText, String key, Object... args) {
        Locale locale = localeMayBeNull != null ? localeMayBeNull : Locale.getDefault();
        return messageSource.getMessage(key, args, defaultText, locale);
    }

    public List<Locale> getSupportedLocales() {
        return List.of(Locale.ENGLISH, Locale.GERMAN);
    }

}
