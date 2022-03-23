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

package org.inappdevtools.library;

import android.content.Context;
import android.view.GestureDetector;

import org.inappdevtools.library.logic.config.ConfigManager;
import org.inappdevtools.library.logic.events.IadtEventBuilder;
import org.inappdevtools.library.logic.events.IadtMessageBuilder;
import org.inappdevtools.library.logic.reports.ReportType;
import org.inappdevtools.library.view.components.items.ButtonFlexData;

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

    public static void addTeamAction(ButtonFlexData runnable){}
    public static GestureDetector getGestureDetector() {
        return null;
    } //TODO
    public static void codepoint(Object caller){}

    //endregion

    //region [ MESSAGES & EVENTS ]

    public static IadtMessageBuilder buildMessage(int textResource) {
        return new IadtMessageBuilder(textResource);
    }

    public static IadtMessageBuilder buildMessage(String text) {
        return new IadtMessageBuilder(text);
    }

    public static IadtEventBuilder buildEvent(String text) {
        return new IadtEventBuilder(text);
    }

    public static void trackUserAction(String text) {
        return;
    }

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

    public static void crashUiThread() {}
    public static void crashBackgroundThread() {}

    //endregion

    //region [ CLOSE & RESTART APP ]

    public static void disable(){}
    public static void cleanAll(){}
    public static void restartApp(){}
    public static void forceCloseApp(){}
    public static void addOnForceCloseRunnable(Runnable onForceClose){}

    //endregion
}
