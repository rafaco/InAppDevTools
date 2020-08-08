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

import android.view.GestureDetector;

import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.events.IadtEventBuilder;
import es.rafaco.inappdevtools.library.logic.events.IadtMessageBuilder;
import es.rafaco.inappdevtools.library.logic.events.detectors.user.GestureEventDetector;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.reports.ReportType;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.logic.external.CustomToast;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;

/**
 * This class is the main public interface of InAppDevTools.
 * Their method are always safe to call even with the library disabled or using our noop version.
 */
public class Iadt {

    public static final String TAG = "Iadt";

    public static boolean isEnabled() {
        if (getController()==null) {
            return false;
        }
        return getController().isEnabled();
    }

    public static boolean isDebug() {
        if (!isEnabled()) return false;
        return getController().isDebug();
    }

    public static boolean isNoop(){
        return false;
    }

    //region [ CORE CONTROLLER ]

    private static IadtController getController() {
        return IadtController.get();
    }

    public static ConfigManager getConfig() {
        if (!isEnabled()) return null;
        return getController().getConfig();
    }

    //endregion

    //region [ OVERLAY NAVIGATION ]

    public static void show() {
        if (!isEnabled()) return;
        getController().getOverlayHelper().showMain();
    }

    public static void hide() {
        if (!isEnabled()) return;
        getController().getOverlayHelper().showIcon();
    }

    //endregion

    //region [ INTEGRATIONS ]

    public static void addTeamAction(ButtonFlexData runnable){
        if(!isEnabled()) return;
        getController().getRunnableManager().add(runnable);
    }

    //TODO: Work in progress
    public static GestureDetector getGestureDetector() {
        if (!isEnabled()) return null;
        GestureEventDetector watcher = (GestureEventDetector) getController().getEventManager()
                .getEventDetectorsManager().get(GestureEventDetector.class);
        if (watcher==null) return null;
        return watcher.getDetector();
    }

    //TODO: CODEPOINT (WIP with low priority ]
    public static void codepoint(Object caller){
        if (!isEnabled()) return;
        //String objectToString = ToStringBuilder.reflectionToString(caller, ToStringStyle.MULTI_LINE_STYLE);
        //String result2 = new GsonBuilder().setPrettyPrinting().create().toJson(caller);
        String message = "Codepoint from " + caller.getClass().getSimpleName(); // + ": " + objectToString;
        CustomToast.show(getController().getContext(), message, CustomToast.TYPE_INFO);
        FriendlyLog.log("D", "Debug", "Codepoint", message);
    }

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
        if (!isEnabled()) return;
        new IadtEventBuilder(text)
                .setSeverity("I")
                .setCategory("User")
                .setSubcategory("Action")
                .fire();
    }

    //endregion

    //region [ REPORTING ]

    public static void takeScreenshot() {
        if (!isEnabled()) return;
        getController().takeScreenshot();
    }

    public static void newSession() {
        if (!isEnabled()) return;
        getController().newSession();
    }

    public static void startReportWizard() {
        if (!isEnabled()) return;
        getController().startReportWizard();
    }

    public static void startReportWizard(ReportType type, final Object param) {
        if (!isEnabled()) return;
        getController().startReportWizard(type, param);
    }

    //endregion

    //region [ USEFUL STUFF ]

    public static void viewReadme() {
        if (!isEnabled()) return;
        ExternalIntentUtils.viewReadme();
    }

    public static void shareDemo() {
        if (!isEnabled()) return;
        ExternalIntentUtils.shareDemo();
    }

    public static void shareLibrary() {
        if (!isEnabled()) return;
        ExternalIntentUtils.shareLibrary();
    }

    public static void crashUiThread() {
        if (!isEnabled()) return;
        getController().crashUiThread();
    }

    public static void crashBackgroundThread() {
        if (!isEnabled()) return;
        getController().crashBackgroundThread();
    }

    //endregion

    //region [ CLOSE & RESTART APP ]

    public static void disable(){
        if (!isEnabled()) return;
        getController().disable();
    }

    public static void cleanAll(){
        if (!isEnabled()) return;
        getController().cleanAll();
    }

    public static void restartApp(){
        if (!isEnabled()) return;
        getController().restartApp(false);
    }

    public static void forceCloseApp(){
        if (!isEnabled()) return;
        getController().forceCloseApp(false);
    }

    public static void addOnForceCloseRunnable(Runnable onForceClose){
        if (!isEnabled()) return;
        getController().getRunnableManager().setForceCloseRunnable(onForceClose);
    }

    //endregion

}
