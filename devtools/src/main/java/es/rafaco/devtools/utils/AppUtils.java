package es.rafaco.devtools.utils;

import android.os.Handler;
import android.os.StrictMode;

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
}
