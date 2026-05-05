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
import java.util.concurrent.CopyOnWriteArrayList;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.hasait.common.util.I18nSupport;
import de.hasait.common.vaadin.wf.DefaultVaadinWidgetFactory;
import de.hasait.common.vaadin.wf.VaadinWidgetFactory;

public abstract class AbstractForm extends FormLayout {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractForm.class);

    private final HorizontalLayout buttonBar = new HorizontalLayout();

    private final List<Runnable> changeListeners = new CopyOnWriteArrayList<>();

    protected I18nSupport i18nSupport;
    protected VaadinWidgetFactory vaadinWidgetFactory;

    public AbstractForm() {
        super();
    }

    public final I18nSupport getI18nSupport() {
        return i18nSupport;
    }

    @Autowired
    public final void setI18nSupport(I18nSupport i18nSupport) {
        this.i18nSupport = i18nSupport;
        this.vaadinWidgetFactory = i18nSupport == null ? null : new DefaultVaadinWidgetFactory(i18nSupport);
    }

    @PostConstruct
    public final void init() {
        populateLayoutBeforeButtonBar();

        add(buttonBar);

        populateLayoutAfterButtonBar();

        initAfterLayout();
    }

    protected void populateLayoutBeforeButtonBar() {
    }

    protected void populateLayoutAfterButtonBar() {
    }

    protected void initAfterLayout() {
    }

    protected final void addToButtonBar(Component component) {
        buttonBar.add(component);
    }

    public final void addListener(@Nonnull Runnable listener) {
        changeListeners.add(listener);
    }

    public final void removeListener(Runnable listener) {
        changeListeners.remove(listener);
    }

    protected final void notifyListeners() {
        for (Runnable listener : changeListeners) {
            try {
                listener.run();
            } catch (RuntimeException e) {
                LOG.warn("ignored listener failure", e);
            }
        }
    }

}
