/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.components.deco;

import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;

public class DecoratedToolInfo {
    String title;
    String message;
    int color;
    int icon;
    Long order;
    private final NavigationStep navigationStep;
    private final Runnable runnable;


    public DecoratedToolInfo(String title, String message, int color, long order, NavigationStep navigationStep) {
        this.title = title;
        this.message = message;
        this.color = color;
        this.order = order;
        this.navigationStep = navigationStep;
        this.runnable = null;
        this.icon = -1;
    }

    public DecoratedToolInfo(String title, String message, int color, long order, Runnable runnable) {
        this.title = title;
        this.message = message;
        this.color = color;
        this.order = order;
        this.navigationStep = null;
        this.runnable = runnable;
        this.icon = -1;
    }

    public DecoratedToolInfo(String title, String message, int color, int icon, long order, NavigationStep navigationStep) {
        this(title, message, color, order, navigationStep);
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public NavigationStep getNavigationStep() {
        return navigationStep;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
