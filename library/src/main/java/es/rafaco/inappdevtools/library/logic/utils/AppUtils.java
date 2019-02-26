package es.rafaco.inappdevtools.library.logic.utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;

public class AppUtils {

    private AppUtils() { throw new IllegalStateException("Utility class"); }

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

    public static void killProcessesAround(Context context) throws PackageManager.NameNotFoundException {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        String myProcessPrefix = context.getApplicationInfo().processName;
        //TODO: Not working, fix following
        String myProcessName = "";//activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0).processName;
        for (ActivityManager.RunningAppProcessInfo proc : am.getRunningAppProcesses()) {
            if (proc.processName.startsWith(myProcessPrefix) && !proc.processName.equals(myProcessName)) {
                android.os.Process.killProcess(proc.pid);
            }
        }
    }

    public static void programRestart(Context context, boolean isCrash) {
        Log.e("DevTools", "Programming restart after 100ms");
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isCrash) intent.putExtra("crash", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
    }

    public static void exit() {
        Log.d("DevTools", "Killing application");
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    public static Intent getAppLauncherIntent(Context context){
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        Class<?> mainActivityClass = null;
        try {
            mainActivityClass = Class.forName(launchIntent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(context, mainActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return intent;
    }

    public static Intent getLastTaskIntent(Context context) {
        Intent intent = new Intent();
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RecentTaskInfo> recentTaskInfos = am.getRecentTasks(1024,0);
        String myPkgNm = context.getPackageName();

        if (!recentTaskInfos.isEmpty()) {
            ActivityManager.RecentTaskInfo recentTaskInfo;
            final int size = recentTaskInfos.size();
            for (int i=0;i<size;i++) {
                recentTaskInfo = recentTaskInfos.get(i);
                if (recentTaskInfo.baseIntent.getComponent().getPackageName().equals(myPkgNm)) {
                    intent = recentTaskInfo.baseIntent;
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        }
        return intent;
    }




    public static void openDeveloperOptions(Context context){
        context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
    }

    public static void openAppSettings(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    //TODO: DELETE? it seems not working
    public static void openInstalledApps(final Context context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }
}
