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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.RouterState;
import com.vaadin.flow.signals.Signal;
import org.springframework.beans.factory.annotation.Autowired;

import de.hasait.common.util.I18nSupport;

public abstract class AbstractMainLayoutCustomizer implements MainLayoutCustomizer {

    protected I18nSupport i18nSupport;

    @Autowired
    public void setI18nSupport(I18nSupport i18nSupport) {
        this.i18nSupport = i18nSupport;
    }

    private String getPageTitle(Class<? extends Component> viewClass) {
        return i18nSupport.pageTitle(viewClass);
    }

    protected final void addDataViewRouterLink(HasComponents hasComponents, Class<? extends Component> targetViewClass) {
        RouterLink routerLink = new RouterLink(getPageTitle(targetViewClass), targetViewClass);
        Div routerLinkDiv = new Div(routerLink);
        Signal<RouterState> routerState = UI.getCurrent().routerStateSignal();
        // TODO adapt CSS
        routerLinkDiv.addClassName("routerLink");
        routerLinkDiv.bindClassName("active", routerState.map(state -> {
            if (state == null) {
                return false;
            }
            Class<? extends Component> viewClass = state.navigationTarget();
            if (viewClass == null) {
                return false;
            }
            return targetViewClass.equals(viewClass);
        }));
        hasComponents.add(routerLinkDiv);
    }

}
