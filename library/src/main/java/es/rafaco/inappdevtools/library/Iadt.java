package es.rafaco.inappdevtools.library;

import android.content.Context;
import android.view.GestureDetector;

//#ifdef MODERN
//@import androidx.annotation.NonNull; 
//#else
import android.support.annotation.NonNull;
//#endif

import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.detectors.user.GestureEventDetector;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableItem;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportHelper;
import es.rafaco.inappdevtools.library.logic.integrations.CustomToast;
import okhttp3.OkHttpClient;

public class Iadt {

    public static final String TAG = "InAppDevTools";

    //region [ ACCESSORS TO IADT CONTROLLER ]

    private static IadtController getController() {
        return IadtController.get();
    }

    public static Context getAppContext() {
        return getController().getAppContext();
    }

    public static ConfigManager getConfig() {
        return getController().getConfig();
    }

    public static EventManager getEventManager() {
        return getController().getEventManager();
    }

    //endregion

    //region [ FEATURE: NETWORK INTERCEPTOR ]

    @NonNull
    public static OkHttpClient getOkHttpClient() {
        return getController().getOkHttpClient();
    }

    //endregion

    //region [ OVERLAY CONTROLLER ]

    public static void show() {
        if (!isEnabled()) return;
        getController().show();
    }

    public static void hide() {
        if (!isEnabled()) return;
        getController().hide();
    }

    public static void takeScreenshot() {
        if (!isEnabled()) return;
        getController().takeScreenshot();
    }

    public static void sendReport(final ReportHelper.ReportType type, final Object param) {
        if (!isEnabled()) return;
        getController().sendReport(type, param);
    }

    //endregion

    //region [ APP CONTROLLER (RESTART AND FORCE CLOSE) ]

    public static void restartApp(){
        getController().restartApp(false);
    }

    public static void forceCloseApp(){
        getController().forceCloseApp(false);
    }

    //endregion

    //region [ EVENT DETECTOR ]
    //TODO:
    public static EventDetector getEventDetector(Class<? extends EventDetector> className) {
        return getController().getEventManager().getEventDetectorsManager().get(className);
    }

    public static GestureDetector getGestureDetector() {
        GestureEventDetector watcher = (GestureEventDetector) getEventDetector(GestureEventDetector.class);

        if (watcher==null) return null;
        return watcher.getDetector();
    }

    //endregion

    //region [ FEATURE: CUSTOM RUNNABLE ]

    public static void addCustomRunnable(RunnableItem runnable){
        if(!isEnabled()) return;
        getController().getRunnableManager().add(runnable);
    }

    public static void addOnForceCloseRunnable(Runnable onForceClose){
        getController().getRunnableManager().addForceCloseRunnable(onForceClose);
    }

    public static Runnable getOnForceCloseRunnable(){
        return getController().getRunnableManager().getForceCloseRunnable();
    }

    //endregion

    //region [ FEATURE: FRIENDLY LOG ]

    public static void showMessage(int stringId) {
        showMessage(getAppContext().getResources().getString(stringId));
    }

    public static void showMessage(final String text) {
        if (!isEnabled()) return;

        CustomToast.show(getAppContext(), text, CustomToast.TYPE_INFO);
        FriendlyLog.log("I", "Message", "Info", text);
    }

    public static void showWarning(final String text) {
        CustomToast.show(getAppContext(), text, CustomToast.TYPE_WARNING);
        FriendlyLog.log("W", "Message", "Warning", text);
    }

    public static void showError(final String text) {
        CustomToast.show(getAppContext(), text, CustomToast.TYPE_ERROR);
        FriendlyLog.log("E", "Message", "Error", text);
    }

    //endregion

    //TODO: CODEPOINT (WIP with low priority ]
    public static void codepoint(Object caller){
        if (!isEnabled()) return;
        //String objectToString = ToStringBuilder.reflectionToString(caller, ToStringStyle.MULTI_LINE_STYLE);
        //String result2 = new GsonBuilder().setPrettyPrinting().create().toJson(caller);
        String message = "Codepoint from " + caller.getClass().getSimpleName(); // + ": " + objectToString;
        CustomToast.show(getAppContext(), message, CustomToast.TYPE_INFO);
        FriendlyLog.log("D", "Debug", "Codepoint", message);
    }

    //endregion



    public static boolean isEnabled() {
        return getController().isEnabled();
    }

    public static boolean isDebug() {
        return getController().isDebug();
    }
}
