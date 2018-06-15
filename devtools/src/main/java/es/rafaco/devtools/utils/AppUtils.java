package es.rafaco.devtools.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;

import es.rafaco.devtools.DevTools;

public class AppUtils {


    public static void startStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    public static void throwExceptionWithDelay(final String title, int delayMillis) {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                throw new RuntimeException(title);
            }
        };
        handler.postDelayed(r, delayMillis);
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static void clearAppData() {
        Context appContext = DevTools.getAppContext();
        try {

            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = appContext.getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void killProcessesAround(Context activity) throws PackageManager.NameNotFoundException {
        ActivityManager am = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
        String myProcessPrefix = activity.getApplicationInfo().processName;
        String myProcessName = "";//activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0).processName;
        for (ActivityManager.RunningAppProcessInfo proc : am.getRunningAppProcesses()) {
            if (proc.processName.startsWith(myProcessPrefix) && !proc.processName.equals(myProcessName)) {
                android.os.Process.killProcess(proc.pid);
            }
        }
    }
}
