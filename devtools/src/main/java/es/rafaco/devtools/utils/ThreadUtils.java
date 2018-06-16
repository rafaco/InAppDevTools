package es.rafaco.devtools.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {

    public static boolean amIOnUiThread(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Looper.getMainLooper().isCurrentThread()
                : isTheUiThread(Thread.currentThread());
    }

    public static boolean isTheUiThread(Thread thread){
        return thread == Looper.getMainLooper().getThread();
    }

    public static void runOnUiThread(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
