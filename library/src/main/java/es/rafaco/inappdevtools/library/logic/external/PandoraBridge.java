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

package es.rafaco.inappdevtools.library.logic.external;

import android.content.Context;
import android.util.Log;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.inspector.GridLineView;
import tech.linjiang.pandora.ui.Dispatcher;
import tech.linjiang.pandora.ui.connector.Type;
import tech.linjiang.pandora.util.Config;

public class PandoraBridge {

    private static boolean isInitialised;
    private static GridLineView gridLineView;

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static void init() {
        if (!isAnyPandoraFeaturesEnabled()) {
            if (IadtController.get().isDebug())
                Log.d(Iadt.TAG, "Pandora init skipped, disabled by config");
            return;
        }
        else if (isInitialised) {
            Log.w(Iadt.TAG, "Pandora init skipped, already initialised");
            return;
        }
        try {
            initLibrary();
            isInitialised = true;
        }
        catch (Throwable e) {
            Log.w(Iadt.TAG, "Pandora init error", e);
            Log.w(Iadt.TAG, "Pandora features DISABLED: network, view and storage inspector");
            setPandoraFeaturesEnabled(false);
        }
    }

    private static void initLibrary() {
        if (IadtController.get().isDebug()) Log.d(Iadt.TAG, "Pandora init");
        Pandora.get();
        Config.setSHAKE_SWITCH(false);  //disable open overlay on shake
        Config.setSANDBOX_DPM(true);    //enable DeviceProtectMode

        if (getConfig().getBoolean(BuildConfigField.NETWORK_INTERCEPTOR)){
            setInterceptorListener();       //Set a listener to the network interceptor
        }
        else{
            Config.setNetLogEnable(false);
        }
    }

    public static void setPandoraFeaturesEnabled(boolean isEnabled){
        getConfig().setBoolean(BuildConfigField.NETWORK_INTERCEPTOR, isEnabled);
        getConfig().setBoolean(BuildConfigField.VIEW_INSPECTION, isEnabled);
        getConfig().setBoolean(BuildConfigField.STORAGE_INSPECTION, isEnabled);
    }

    public static boolean isAllPandoraFeaturesEnabled(){
        return getConfig().getBoolean(BuildConfigField.NETWORK_INTERCEPTOR)
                && getConfig().getBoolean(BuildConfigField.VIEW_INSPECTION)
                && getConfig().getBoolean(BuildConfigField.STORAGE_INSPECTION);
    }

    public static boolean isAnyPandoraFeaturesEnabled(){
        return getConfig().getBoolean(BuildConfigField.NETWORK_INTERCEPTOR)
                || getConfig().getBoolean(BuildConfigField.VIEW_INSPECTION)
                || getConfig().getBoolean(BuildConfigField.STORAGE_INSPECTION);
    }

    private static ConfigManager getConfig() {
        return IadtController.get().getConfig();
    }

    public static void setInterceptorListener() {
        Pandora.get().getInterceptor().setListener(new PandoraListener());
    }

    public static void open() {
        Pandora.get().open();
    }

    public static void select() {
        Dispatcher.start(getContext().getApplicationContext(), Type.SELECT);
    }

    public static void hierarchy() {
        Dispatcher.start(getContext().getApplicationContext(), Type.HIERARCHY);
    }

    public static void storage() {
        Dispatcher.start(getContext().getApplicationContext(), Type.FILE);
    }

    public static void network() {
        Dispatcher.start(getContext().getApplicationContext(), Type.NET);
    }

    public static void grid() {
        if (gridLineView == null){
            gridLineView = new GridLineView(getContext());
        }
        gridLineView.toggle();
    }

    public static void measure() {
        Dispatcher.start(getContext().getApplicationContext(), Type.BASELINE);
    }
}
