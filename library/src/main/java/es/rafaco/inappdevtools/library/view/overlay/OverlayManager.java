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

package es.rafaco.inappdevtools.library.view.overlay;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;

public class OverlayManager {

    private final Context context;
    private final LayerManager layerManager;
    private final ScreenManager screenManager;
    private EventManager.Listener onForegroundListener;
    private EventManager.Listener onBackgroundListener;

    public OverlayManager(Context context) {
        this.context = context;
        this.layerManager = new LayerManager(context);
        this.screenManager = new ScreenManager(context, layerManager.getMainLayer());

        subscribeEventListeners();
    }

    private IadtController getController() {
        return IadtController.get();
    }

    private EventManager getEventManager() {
        return getController().getEventManager();
    }


    //region [ INTERNAL NAVIGATION ]

    protected void showToggle() {
        if (screenManager.getCurrentScreen() == null){
            screenManager.goHome();
        }

        layerManager.toggleMainLayerVisibility(null);
    }

    protected void showMain() {
        if (screenManager.getCurrentScreen() == null){
            NavigationStep currentStep = IadtController.get().getNavigationManager().getCurrent();
            if (currentStep !=null)
                screenManager.goTo(currentStep.getClassName(), currentStep.getParams());
            else
                screenManager.goHome();
        }else{
            resume();
        }

        layerManager.toggleMainLayerVisibility(true);
    }

    protected void showIcon() {
        pause();
        layerManager.toggleMainLayerVisibility(false);
    }

    protected void navigateHome() {
        screenManager.goHome();
    }

    protected void navigateTo(String name) {
        navigateTo(name, null);
    }

    protected void navigateTo(String name, String param) {
        layerManager.toggleMainLayerVisibility(true);
        screenManager.goTo(name, param);
    }

    protected void navigateBack() {
        screenManager.goBack();
    }

    private void resume() {
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "OverlayManager - resume");
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().resume();
        toggleVisibility(true);
    }

    private void pause() {
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "OverlayManager - pause");
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().pause();
        toggleVisibility(false);
    }

    public void toggleVisibility(boolean isVisible) {
        layerManager.toggleAllLayerVisibility(isVisible);
    }

    //endregion

    //region [ LIFECYCLE FROM SERVICE ]

    protected void onConfigurationChanged(Configuration newConfig) {
        if (layerManager != null){
            layerManager.onConfigurationChanged(newConfig);
        }

        if (screenManager != null){
            screenManager.onConfigurationChanged(newConfig);
        }
    }

    //endregion


    //region [ EVENTS SUBSCRIBERS ]

    private boolean isForeground = true;
    private boolean isSuspended = false;

    //TODO: refactor into an EventSubscriber
    public void subscribeEventListeners() {
        onForegroundListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (!isForeground && isSuspended) {
                    isForeground = true;
                    isSuspended = false;
                    resume();
                }
            }
        };
        onBackgroundListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (isForeground) {
                    isForeground = false;
                    isSuspended = true;
                    pause();
                }
            }
        };
        getEventManager().subscribe(Event.IMPORTANCE_FOREGROUND, onForegroundListener);
        getEventManager().subscribe(Event.IMPORTANCE_BACKGROUND, onBackgroundListener);
    }

    public void unsubscribeEventListeners() {
        if (onForegroundListener!=null){
            getEventManager().unsubscribe(Event.IMPORTANCE_FOREGROUND, onForegroundListener);
            onForegroundListener = null;
        }
        if (onBackgroundListener!=null){
            getEventManager().unsubscribe(Event.IMPORTANCE_BACKGROUND, onBackgroundListener);
            onBackgroundListener = null;
        }
    }

    //endregion

    public void destroy() {
        if (screenManager != null) screenManager.destroy();
        if (layerManager != null) layerManager.destroy();
        unsubscribeEventListeners();
    }
}
