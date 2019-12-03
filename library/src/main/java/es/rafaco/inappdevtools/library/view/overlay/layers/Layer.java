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

package es.rafaco.inappdevtools.library.view.overlay.layers;

import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.LayerManager;

public abstract class Layer {

    public enum Type { ICON, REMOVE, SCREEN}

    protected final LayerManager manager;
    protected View view;

    public Layer(LayerManager manager) {
        this.manager = manager;
    }

    public void addView() {
        manager.getInflater().getContext().setTheme(R.style.LibTheme);
        //manager.getInflater().cloneInContext(new ContextThemeWrapper(getBaseContext(), R.style.AppCompatAlertDialogStyle));

        view  = manager.getInflater().inflate(getLayoutId(), null);
        view.setTag(this.getClass().getSimpleName());
        WindowManager.LayoutParams paramRemove = getLayoutParams();

        beforeAttachView(view);
        manager.getWindowManager().addView(view, paramRemove);
        afterAttachView(view);
    }

    public View getView() {
        return view;
    }

    public void destroy() {
        manager.getWindowManager().removeView(getView());
    }

    public static int getLayoutType(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
        return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }

    //TODO: Research https://github.com/Manabu-GT/DebugOverlay-Android
    /*
    public static int getWindowTypeForOverlay(boolean allowSystemLayer) {
        if (allowSystemLayer) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return TYPE_APPLICATION_OVERLAY;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                return TYPE_SYSTEM_ALERT;
            } else {
                return TYPE_TOAST;
            }
        } else {
            // make layout of the window happens as that of a top-level window, not as a child of its container
            return TYPE_APPLICATION_ATTACHED_DIALOG;
        }
    }*/


    protected void afterAttachView(View view) {}
    public void onConfigurationChange(Configuration newConfig) {}

    public abstract Layer.Type getType();
    protected abstract int getLayoutId();
    protected abstract WindowManager.LayoutParams getLayoutParams();
    protected abstract void beforeAttachView(View view);
}
