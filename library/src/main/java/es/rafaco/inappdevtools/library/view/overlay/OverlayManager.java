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

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;

public class OverlayManager {

    private final Context context;
    private final LayerManager layerManager;
    private final ScreenManager screenManager;

    public OverlayManager(Context context) {
        this.context = context;
        this.layerManager = new LayerManager(context);
        this.screenManager = new ScreenManager(context, layerManager.getMainLayer());


        subscribeNewSyncWithAppImportance();
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
            screenManager.goHome();
        }

        layerManager.toggleMainLayerVisibility(true);
    }

    protected void showIcon() {
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
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().resume();
        layerManager.toggleAllLayerVisibility(true);
    }

    private void pause() {
        if (screenManager.getCurrentScreen() != null)
            screenManager.getCurrentScreen().pause();
        layerManager.toggleAllLayerVisibility(false);
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

    public void subscribeNewSyncWithAppImportance() {

        getController().getEventManager().subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (!isForeground && isSuspended) {
                    isForeground = true;
                    isSuspended = false;
                    resume();
                }
            }
        });

        getController().getEventManager().subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (isForeground) {
                    isForeground = false;
                    isSuspended = true;
                    pause();
                }
            }
        });
    }

    //endregion

    private Context getContext() {
        return context;
    }

    private IadtController getController() {
        return IadtController.get();
    }

    public void destroy() {
        if (screenManager != null) screenManager.destroy();
        if (layerManager != null) layerManager.destroy();
    }
}
