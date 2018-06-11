package es.rafaco.devtools;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import es.rafaco.devtools.logic.activityLog.ActivityLogManager;
import es.rafaco.devtools.utils.AppUtils;
import es.rafaco.devtools.logic.exception.CustomExceptionHandler;

public class DevTools {

    public static final String TAG = "DevTools";
    private static final boolean STRICT_MODE_ENABLED = false;
    public static boolean SERVICE_STICKY = false;
    public static boolean CALL_DEFAULT_EXCEPTION_HANDLER = false;

    private static Context appContext;
    private static ActivityLogManager activityLogManager;
    public static int readerCounter = 0;

    public static void install(@Nullable final Context context) {
        Log.d(DevTools.TAG, "Initializing DevTools...");

        appContext = context.getApplicationContext();
        startUncaughtExceptionHandler(context);
        startStrictMode();
        startService(context);
        startActivityLog(context);
        //throwExceptionWithDelay(10000);
    }

    private static void startActivityLog(Context context) {
        activityLogManager = new ActivityLogManager(context);
    }

    public static Context getAppContext() {
        return appContext;
    }

    private static void startUncaughtExceptionHandler(Context context) {
        Thread.UncaughtExceptionHandler currentHanler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHanler != null || !currentHanler.getClass().isInstance(CustomExceptionHandler.class)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context.getApplicationContext(), context, currentHanler));
            Log.d(DevTools.TAG, "Exception handler added");
        }else{
            Log.d(DevTools.TAG, "Exception handler already attach on thread");
        }
    }

    private static void startStrictMode() {
        if (STRICT_MODE_ENABLED && BuildConfig.DEBUG) {
            AppUtils.startStrictMode();
        }
    }

    private static void startService(Context context) {
        context.startService(new Intent(context, DevToolsService.class));
    }

    public static ActivityLogManager getActivityLogManager() {
        return activityLogManager;
    }

    public static void showMessage(int stringId) {
        showMessage(getAppContext().getResources().getString(stringId));
    }

    public static void showMessage(String text) {
        //TODO: use a custom overlay toast
        Toast.makeText(getAppContext(), text, Toast.LENGTH_LONG).show();
    }
}
