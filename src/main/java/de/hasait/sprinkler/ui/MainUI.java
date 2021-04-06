/*
 * Copyright (C) 2021 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hasait.sprinkler.Application;

/**
 *
 */
@SpringUI
@Theme(ValoTheme.THEME_NAME)
@Push(PushMode.AUTOMATIC)
@Title(Application.TITLE)
@SpringViewDisplay
public class MainUI extends UI implements ViewDisplay {

    private static final Logger LOG = LoggerFactory.getLogger(MainUI.class);

    private final SpringViewProvider viewProvider;

    private final Map<String, VerticalLayout> tabLayoutByViewName = new HashMap<>();
    private final Map<VerticalLayout, String> viewNameByTabLayout = new HashMap<>();

    private VerticalLayout currentTabLayout;
    private TabSheet tabSheet;

    public MainUI(SpringViewProvider viewProvider) {
        super();

        this.viewProvider = viewProvider;
    }

    @Override
    public void detach() {
        LOG.debug("detach");

        View currentView = getNavigator().getCurrentView();
        if (currentView instanceof ViewDetach) {
            ((ViewDetach) currentView).detach();
        }

        super.detach();
    }

    @Override
    public void showView(View view) {
        String curState = getNavigator().getState();
        LOG.debug("showView: {}, {}", curState, view.getClass().getSimpleName());

        if (currentTabLayout != null) {
            currentTabLayout.removeAllComponents();
        }

        currentTabLayout = tabLayoutByViewName.get(curState);
        Component actualTabLayout = tabSheet.getSelectedTab();
        if (currentTabLayout != actualTabLayout) {
            LOG.debug("showView - setSelectedTab");
            tabSheet.setSelectedTab(currentTabLayout);
        } else {
            LOG.debug("showView - tab correct");
        }

        currentTabLayout.removeAllComponents();
        Component viewComponent = view.getViewComponent();
        currentTabLayout.addComponent(viewComponent);
        currentTabLayout.setExpandRatio(viewComponent, 1);
    }

    @Override
    protected void init(final VaadinRequest pRequest) {
        LOG.debug("init");

        AbsoluteLayout rootLayout = new AbsoluteLayout();
        rootLayout.setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(new MarginInfo(false, true, true, true));
        layout.setSpacing(true);
        layout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        layout.setSizeFull();
        rootLayout.addComponent(layout);

        Label title = new Label(Application.TITLE);
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        layout.addComponent(title);
        layout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);

        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        layout.addComponent(tabSheet);
        layout.setExpandRatio(tabSheet, 1);

        for (String viewName : viewProvider.getViewNamesForCurrentUI()) {
            VerticalLayout tabLayout = new VerticalLayout();
            tabLayoutByViewName.put(viewName, tabLayout);
            viewNameByTabLayout.put(tabLayout, viewName);
            tabLayout.setSizeFull();
            tabLayout.setMargin(new MarginInfo(true, false, false, false));
            tabLayout.setCaption(viewName);
            tabSheet.addTab(tabLayout, viewName);
        }

        tabSheet.addSelectedTabChangeListener(this::onTabChange);

        Label versionLabel = new Label("Version: " + getClass().getPackage().getImplementationVersion());
        rootLayout.addComponent(versionLabel, "right:5px; bottom:2px;");

        setContent(rootLayout);

        if (!tabLayoutByViewName.containsKey(getNavigator().getState())) {
            LOG.debug("Navigator has invalid state - activating first view...");
            onTabChange(tabSheet);
        }
    }

    private void onTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
        onTabChange(selectedTabChangeEvent.getTabSheet());
    }

    private void onTabChange(TabSheet tabSheet) {
        LOG.debug("onTabChange");

        Component selectedTab = tabSheet.getSelectedTab();
        String newState = viewNameByTabLayout.get(selectedTab);
        String curState = getNavigator().getState();
        if (newState.equals(curState)) {
            LOG.debug("onTabChange - tab has not changed");
            return;
        }

        LOG.debug("onTabChange - navigateTo: {} -> {}", curState, newState);
        getNavigator().navigateTo(newState);
        // TODO handle prevented navigation
    }

}
