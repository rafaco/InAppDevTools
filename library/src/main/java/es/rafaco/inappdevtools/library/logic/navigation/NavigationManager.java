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

package es.rafaco.inappdevtools.library.logic.navigation;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

public class NavigationManager {

    private List<NavigationStep> history;

    public NavigationManager() {
        this.history = new ArrayList<>();
    }

    public void addStep(Class<? extends Screen> screenClass, final String params) {
        NavigationStep newStep = new NavigationStep(screenClass, params);
        addStep(newStep);
    }

    public void addStep(NavigationStep newStep) {
        //Ensure home screen is always at the the first for backward navigation
        if (history.isEmpty() &&
                !newStep.getClassName().equals(ScreenManager.getHomeClass())){
            NavigationStep homeStep = new NavigationStep(ScreenManager.getHomeClass(), null);
            history.add(homeStep);
        }

        history.add(newStep);
        IadtController.get().getEventManager().fire(Event.OVERLAY_NAVIGATION, newStep);
    }

    public void clearSteps(){
        history.clear();
    }

    public NavigationStep removeStep(){
        if (history.isEmpty()){
           return null;
        }
        return history.remove(history.size() - 1);
    }

    public NavigationStep getCurrent(){
        if (history.isEmpty()){
            return null;
        }
        return history.get(history.size() - 1);
    }

    public boolean isCurrentScreen(Class<? extends Screen> screenClass){
        if (getCurrent() != null
                && getCurrent().getClassName().equals(screenClass)){
            return true;
        }
        return false;
    }

    public boolean isPreviousScreen(Class<? extends Screen> screenClass){
        if (history.isEmpty() || history.size() < 2){
            return false;
        }
        NavigationStep previousStep = history.get(history.size() - 2);
        return previousStep.getClassName().equals(screenClass);
    }

    public String getCurrentParams(){
        return getCurrent().getParams();
    }

    public void updateCurrentParams(String newParams){
        getCurrent().setParams(newParams);
    }

    public boolean isHome() {
        return history.isEmpty() || history.size() == 1;
    }
}
