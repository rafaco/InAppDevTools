/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.navigation;

import org.inappdevtools.library.view.overlay.screens.Screen;

public class NavigationStep{
    private final Class<? extends Screen> className;
    private String params;

    public NavigationStep(Class<? extends Screen> className, String params) {
        this.className = className;
        this.params = params;
    }

    public Class<? extends Screen> getClassName() {
        return className;
    }

    public String getStringClassName() {
        return getClassName().getSimpleName();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString(){
       return "NavigationStep [" + getStringClassName()+ "] : " + getParams();
    }
}
