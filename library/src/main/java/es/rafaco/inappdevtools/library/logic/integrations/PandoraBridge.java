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

package es.rafaco.inappdevtools.library.logic.integrations;

import android.content.Context;

import es.rafaco.inappdevtools.library.IadtController;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.inspector.GridLineView;
import tech.linjiang.pandora.network.OkHttpInterceptor;
import tech.linjiang.pandora.ui.Dispatcher;
import tech.linjiang.pandora.ui.connector.Type;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;

public class PandoraBridge {

    private static GridLineView gridLineView;

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static void init() {
        Utils.init(getContext());
        Config.setSANDBOX_DPM(true);    //enable DeviceProtectMode
        Config.setSHAKE_SWITCH(false);  //disable open overlay on shake
        setInterceptorListener();
    }

    public static OkHttpInterceptor getInterceptor() {
        return Pandora.get().getInterceptor();
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
