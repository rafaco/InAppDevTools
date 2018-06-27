package es.rafaco.devtools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.logic.activityLog.ActivityLogManager;
import es.rafaco.devtools.logic.anr.AnrLogger;
import es.rafaco.devtools.logic.crash.CrashHandler;
import es.rafaco.devtools.view.overlay.tools.screenshot.ScreenshotHelper;
import es.rafaco.devtools.utils.AppUtils;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.NotificationUIService;
import es.rafaco.devtools.view.OverlayUIService;

public class DevTools {

    public static final String TAG = "DevTools";

    private static Context appContext;
    private static DevToolsConfig config;
    private static ActivityLogManager activityLogManager;
    private static AnrLogger anrLogger;
    public static int readerCounter = 0;


    //region [ PUBLIC INITIALIZATION ]

    public static void install(Context context) {
        install(context, DevToolsConfig.newBuilder().build());
    }

    public static void install(Context context, DevToolsConfig config) {
        if (config == null || !config.enabled){
            Log.w(DevTools.TAG, "DevTools initialization skipped.");
            return;
        }

        Log.d(DevTools.TAG, "Initializing DevTools...");
        appContext = context.getApplicationContext();
        DevTools.config = config;

        if (config.crashHandlerEnabled) startCrashHandler(context);
        if (config.anrLoggerEnabled) startAnrLogger();
        if (config.strictModeEnabled) startStrictMode();
        if (config.activityLoggerEnabled) startActivityLogger(context);
        if (config.notificationUiEnabled) startForegroundService(context);
        if (config.overlayUiEnabled) startUiService(context);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                getDatabase().printOverview();
            }
        });
        Log.i(DevTools.TAG, "DevTools initialized");
    }

    //endregion

    //region [ PRIVATE INITIALIZATION ]

    private static void startCrashHandler(Context context) {
        Thread.UncaughtExceptionHandler currentHanler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHanler != null || !currentHanler.getClass().isInstance(CrashHandler.class)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context.getApplicationContext(), context, currentHanler));
            Log.d(DevTools.TAG, "Exception handler added");
        }else{
            Log.d(DevTools.TAG, "Exception handler already attach on thread");
        }
    }

    private static void startStrictMode() {
        if (config.strictModeEnabled && BuildConfig.DEBUG) {
            AppUtils.startStrictMode();
        }
    }

    private static void startActivityLogger(Context context) {
        activityLogManager = new ActivityLogManager(context);
    }

    private static void startAnrLogger(){
        anrLogger = new AnrLogger();
    }

    private static void startForegroundService(Context context) {
        Intent intent = new Intent(context, NotificationUIService.class);
        intent.setAction(NotificationUIService.ACTION_START_FOREGROUND_SERVICE);
        context.startService(intent);
    }



    private static void startUiService(Context context) {
        context.startService(new Intent(context, OverlayUIService.class));
    }

    //endregion

    //region [ PUBLIC ACCESSORS ]

    public static Context getAppContext() {
        return appContext;
    }
    public static DevToolsDatabase getDatabase() {
        return DevToolsDatabase.getInstance();
    }
    public static ActivityLogManager getActivityLogManager() {
        return activityLogManager;
    }
    public static DevToolsConfig getConfig() {
        return config;
    }

    //endregion

    //region [ PUBLIC ACTIONS ]

    public static void showMessage(int stringId) {
        showMessage(getAppContext().getResources().getString(stringId));
    }

    public static void showMessage(final String text) {
        Log.d(DevTools.TAG, "Showing message: " + text);

        //TODO: use a custom overlay toast
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void takeScreenshot() {

        Boolean hasPermission = PermissionActivity.startIfNeeded(appContext, PermissionActivity.IntentAction.STORAGE);
        Log.d(DevTools.TAG, "hasPermission is: " + String.valueOf(hasPermission));

        //TODO:
        //if (hasPermission)
        ScreenshotHelper helper = new ScreenshotHelper(appContext);
        File screen = helper.takeScreenshot();

        if(config.overlayUiEnabled){
            helper.openFile(screen);
        }
    }

    public static void sendReport() {

    }

    //endregion
}
