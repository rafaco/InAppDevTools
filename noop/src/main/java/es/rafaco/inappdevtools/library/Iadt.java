package es.rafaco.inappdevtools.library;

import android.content.Context;
import android.view.GestureDetector;

import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.reports.ReportHelper;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableItem;
import okhttp3.OkHttpClient;

public class Iadt {

    public static final String TAG = "InAppDevTools";

    //region [ ACCESSORS TO IADT CONTROLLER ]

    public static Context getAppContext() {
        return null;
    }

    public static ConfigManager getConfig() {
        return new ConfigManager(null);
    }

    public static EventManager getEventManager() {
        return null;
    }

    //endregion

    //region [ FEATURE: NETWORK INTERCEPTOR ]

    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }

    //endregion

    //region [ OVERLAY CONTROLLER ]

    public static void show() {}
    public static void hide() {}
    public static void takeScreenshot() {}
    public static void sendReport(final ReportHelper.ReportType type, final Object param){}

    //endregion

    //region [ APP CONTROLLER (RESTART AND FORCE CLOSE) ]

    public static void restartApp(){}
    public static void forceCloseApp(){}

    //endregion

    //region [ EVENT DETECTOR ]

    /*//TODO:
    public static EventDetector getEventDetector(Class<? extends EventDetector> className) {
        return null;
    }*/

    public static GestureDetector getGestureDetector() {
        return null;
    }

    //endregion

    //region [ FEATURE: CUSTOM RUNNABLE ]

    public static void addCustomRunnable(RunnableItem runnable){}
    public static void addOnForceCloseRunnable(Runnable onForceClose){}
    public static Runnable getOnForceCloseRunnable(){
        return null;
    }

    //endregion

    //region [ FEATURE: FRIENDLY LOG ]

    public static void showMessage(int stringId) {}
    public static void showMessage(final String text) {}
    public static void showWarning(final String text) {}
    public static void showError(final String text) {}

    //endregion

    public static void codepoint(Object caller){}

    public static boolean isEnabled(){
        return false;
    }
    public static boolean isDebug(){
        return false;
    }
    public static boolean isNoop(){
        return true;
    }

    public static void viewReadme() {}
    public static void shareDemo() {}
    public static void shareLibrary() {}

    public void crashUiThread() {}
    public void crashBackgroundThread() {}
}
