package es.rafaco.devtools;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

import es.rafaco.devtools.db.Anr;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.logic.activityLog.ActivityLogManager;
import es.rafaco.devtools.logic.crash.CrashHandler;
import es.rafaco.devtools.utils.AppUtils;

public class DevTools {

    public static final String TAG = "DevTools";
    private static final boolean STRICT_MODE_ENABLED = false;
    public static boolean SERVICE_STICKY = false;
    public static boolean CALL_DEFAULT_EXCEPTION_HANDLER = false;

    private static Context appContext;
    private static ActivityLogManager activityLogManager;
    public static int readerCounter = 0;
    private static ANRWatchDog anrWarningWatcher;
    private static ANRWatchDog anrErrorWatcher;

    public static void install(@Nullable final Context context) {
        Log.d(DevTools.TAG, "Initializing DevTools...");

        appContext = context.getApplicationContext();
        startUncaughtExceptionHandler(context);
        startAnrWatchDog();
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
        if (currentHanler != null || !currentHanler.getClass().isInstance(CrashHandler.class)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context.getApplicationContext(), context, currentHanler));
            Log.d(DevTools.TAG, "Exception handler added");
        }else{
            Log.d(DevTools.TAG, "Exception handler already attach on thread");
        }
    }

    private static void startAnrWatchDog(){

        anrWarningWatcher = new ANRWatchDog(10000 /*timeout*/)
                .setANRListener(new ANRWatchDog.ANRListener() {
            @Override
            public void onAppNotResponding(ANRError error) {
                storeAnr(error, true);
            }
        });
        anrWarningWatcher.start();

        anrErrorWatcher = new ANRWatchDog()
                .setANRListener(new ANRWatchDog.ANRListener() {
                    @Override
                    public void onAppNotResponding(ANRError error) {
                        storeAnr(error, false);
                    }
                });
        anrErrorWatcher.start();
        Log.d(DevTools.TAG, "ANRWatchDog added");
    }

    private static void storeAnr(ANRError error, boolean isWarning) {

        if(isWarning){
            Log.w(DevTools.TAG, String.format("ANR 1sec warning: %s - %s", error.getMessage(), error.getCause()));
        }else{
            Log.e(DevTools.TAG, String.format("ANR WARNING: %s - %s", error.getMessage(), error.getCause()));
        }

        Anr anr = new Anr();
        anr.setMessage(error.getMessage().toString());
        anr.setCause(error.getCause().toString());
        storeAnr(anr);
    }

    private static void storeAnr(final Anr anr) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = getDatabase();
                db.anrDao().insertAll(anr);
                Log.d(DevTools.TAG, "Anr stored in db");
            }
        });
    }

    private static void startStrictMode() {
        if (STRICT_MODE_ENABLED && BuildConfig.DEBUG) {
            AppUtils.startStrictMode();
        }
    }

    private static void startService(Context context) {
        context.startService(new Intent(context, DevToolsUiService.class));
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

    public static DevToolsDatabase getDatabase() {
        return DevToolsDatabase.getInstance();
    }
}
