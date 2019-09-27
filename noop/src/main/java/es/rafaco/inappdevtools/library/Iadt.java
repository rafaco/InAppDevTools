package es.rafaco.inappdevtools.library;

import android.content.Context;
import android.view.GestureDetector;

import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.reports.ReportHelper;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import okhttp3.OkHttpClient;

public class Iadt {

    public static final String TAG = "InAppDevTools";

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

    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }
    public static void addRunButton(RunButton runnable){}
    public static GestureDetector getGestureDetector() {
        return null;
    } //TODO
    public static void codepoint(Object caller){}

    //endregion

    //region [ TOAST & LOG ]

    public static void showMessage(int stringId) {}
    public static void showMessage(final String text) {}
    public static void showWarning(final String text) {}
    public static void showError(final String text) {}

    //endregion

    //region [ REPORTING ]

    public static void takeScreenshot() {}
    public static void sendReport(final ReportHelper.ReportType type, final Object param){}

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
    public static Runnable getOnForceCloseRunnable(){
        return null;
    }

    //endregion
}
