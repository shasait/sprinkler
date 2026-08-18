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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterState;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import de.hasait.common.security.SecurityService;
import de.hasait.common.util.I18nSupport;

/**
 *
 */
@PermitAll
@SpringComponent
@UIScope
public final class MainLayout extends AppLayout {

    private final MainLayoutCustomizer customizer;

    private final SecurityService securityService;

    private final I18nSupport i18nSupport;

    public MainLayout(MainLayoutCustomizer customizer, SecurityService securityService, I18nSupport i18nSupport) {
        this.customizer = customizer;
        this.securityService = securityService;
        this.i18nSupport = i18nSupport;

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        Signal<RouterState> routerState = UI.getCurrent().routerStateSignal();
        H1 logoH1 = new H1();
        logoH1.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);
        logoH1.bindText(routerState.map(state -> {
            Class<? extends Component> viewClass = state != null ? state.navigationTarget() : null;
            return viewClass != null ? i18nSupport.applicationAndPageTitle(viewClass) : i18nSupport.applicationTitle();
        }));

        String loggedInUsername = securityService.getAuthenticatedUser().getUsername();
        Button logoutButton = new Button(i18nSupport.msgkka("logoutButton.labelWithUser", loggedInUsername), e -> securityService.logout());

        var header = new HorizontalLayout(new DrawerToggle(), logoH1, logoutButton);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logoH1);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout verticalLayout = new VerticalLayout();
        customizer.populateDrawer(verticalLayout);
        addToDrawer(verticalLayout);
    }

}
