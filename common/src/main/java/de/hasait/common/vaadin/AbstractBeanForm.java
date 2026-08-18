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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.StatusChangeEvent;
import jakarta.annotation.Nonnull;

import de.hasait.common.util.listener.ChangeListener;
import de.hasait.common.util.listener.ChangeListenerSupport;
import de.hasait.common.util.listener.ChangeListenerSupportDelegate;
import de.hasait.common.util.listener.ValueChange;

public abstract class AbstractBeanForm<B> extends AbstractForm implements ChangeListenerSupport<AbstractBeanForm<B>, ValueChange<B>> {

    private final ChangeListenerSupportDelegate<AbstractBeanForm<B>, ValueChange<B>> listenerSupportDelegate = new ChangeListenerSupportDelegate<>();

    private final Button addOrSaveButton = new Button();
    private final Button deleteButton = new Button();

    private final Span statusSpan = new Span();

    protected final Class<B> beanClass;

    protected final Binder<B> binder;

    private String addButtonLabelText;
    private String saveButtonLabelText;

    public AbstractBeanForm(Class<B> beanClass) {
        super();

        this.beanClass = beanClass;
        this.binder = new Binder<>(beanClass);
    }

    @Override
    public final void addChangeListener(@Nonnull ChangeListener<? super AbstractBeanForm<B>, ? super ValueChange<B>> listener) {
        listenerSupportDelegate.addChangeListener(listener);
    }

    @Override
    public final void removeChangeListener(@Nonnull ChangeListener<? super AbstractBeanForm<B>, ? super ValueChange<B>> listener) {
        listenerSupportDelegate.removeChangeListener(listener);
    }

    protected final void notifyListeners(B oldBean, B newBean) {
        listenerSupportDelegate.fireChanged(this, new ValueChange<>(oldBean, newBean));
    }

    protected void populateLayoutBeforeButtonBar() {
        super.populateLayoutBeforeButtonBar();

        addToButtonBar(addOrSaveButton);
        addButtonLabelText = i18nSupport.labelText("addButton");
        saveButtonLabelText = i18nSupport.labelText("saveButton");
        deleteButton.setText(i18nSupport.labelText("deleteButton"));
        deleteButton.setIcon(VaadinIcon.TRASH.create());
        addToButtonBar(deleteButton);
        addOrSaveButton.addClickListener(this::onAddOrSaveButtonClicked);
        deleteButton.addClickListener(this::onDeleteButtonClicked);
    }

    protected void populateLayoutAfterButtonBar() {
        super.populateLayoutAfterButtonBar();
        add(statusSpan);
    }

    @Override
    protected void initAfterLayout() {
        super.initAfterLayout();

        populateBinder();
        // binder.bindInstanceFields(this);

        binder.addStatusChangeListener(this::onBinderStatusChanged);
        binder.setStatusLabel(statusSpan);
    }

    protected abstract void populateBinder();

    public final void setBean(B bean) {
        binder.setBean(bean);
        binder.validate();

        boolean existing = !isNewBean(bean);
        addOrSaveButton.setText(existing ? saveButtonLabelText : addButtonLabelText);
        addOrSaveButton.setIcon(existing ? VaadinIcon.ARCHIVE.create() : VaadinIcon.PLUS.create());
        deleteButton.setEnabled(existing);

        afterBeanSet();
    }

    public final B getBean() {
        return binder.getBean();
    }

    protected abstract boolean isNewBean(B bean);

    protected void afterBeanSet() {
        // empty default implementation
    }

    private void onBinderStatusChanged(StatusChangeEvent event) {
        boolean valid = !event.hasValidationErrors();
        addOrSaveButton.setEnabled(valid);
    }

    private void onAddOrSaveButtonClicked(ClickEvent<?> event) {
        BinderValidationStatus<B> status = binder.validate();
        if (!status.isOk()) {
            String message = "Validation failed";
            if (status.hasErrors()) {
                message += ": " + status.getValidationErrors().get(0).getErrorMessage();
            }
            Notification.show(message);
            return;
        }

        B bean = binder.getBean();
        if (bean != null) {
            B savedBean = saveAndFlushBean(bean);
            notifyListeners(null, savedBean);
        }
    }

    protected abstract B saveAndFlushBean(B bean);

    private void onDeleteButtonClicked(ClickEvent<?> clickEvent) {
        B bean = binder.getBean();
        if (bean != null) {
            deleteBean(bean);
            notifyListeners(bean, null);
        }
    }

    protected abstract void deleteBean(B bean);

}
