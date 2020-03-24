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

package es.rafaco.inappdevtools.library;

import android.content.Context;
import android.view.GestureDetector;

import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.reports.ReportType;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;

public class Iadt {

    public static final String TAG = "InAppDevTools-Noop";

    public static boolean isEnabled(){
        return false;
    }
    public static boolean isDebug(){
        return false;
    }
    public static boolean isNoop(){
        return true;
    }

    //region [ CORE CONTROLLER ]

    public static Context getAppContext() {
        return null;
    }
    public static ConfigManager getConfig() {
        return new ConfigManager(null);
    }

    //endregion

    //region [ OVERLAY NAVIGATION ]

    public static void show() {}
    public static void show(String screenName) {}
    public static void hide() {}

    //endregion

    //region [ INTEGRATIONS ]

    public static void addRunButton(RunButton runnable){}
    public static GestureDetector getGestureDetector() {
        return null;
    } //TODO
    public static void codepoint(Object caller){}

    //endregion

    //region [ TOAST & LOG ]

    public static void showMessage(int stringId) {}
    public static void showMessage(String text) {}
    public static void showWarning(String text) {}
    public static void showError(String text) {}
    public static void trackUserAction(String text) {}

    //endregion

    //region [ REPORTING ]

    public static void takeScreenshot() {}
    public static void newSession() {}
    public void startReportWizard() {}
    public void startReportWizard(ReportType type, final Object param) {}

    //endregion

    //region [ USEFUL STUFF ]

    public static void viewReadme() {}
    public static void shareDemo() {}
    public static void shareLibrary() {}

    public void crashUiThread() {}
    public void crashBackgroundThread() {}

    //endregion

    //region [ CLOSE & RESTART APP ]

    public static void restartApp(){}
    public static void forceCloseApp(){}
    public static void addOnForceCloseRunnable(Runnable onForceClose){}

    //endregion
}
