package es.rafaco.devtools.utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import es.rafaco.devtools.DevTools;

public class ThreadUtils {

    public static void logCurrentThread(String fromText){
        String thread = (amIOnUiThread()) ? "MAIN" : "a background";
        DevTools.showMessage(fromText + " is running on " + thread + " thread");
    }

    public static String getFormattedThread(){
        String thread = (amIOnUiThread()) ? "main" : "background";
        return thread;
    }

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

    public static void runOnBackThread(Runnable runnable){
        AsyncTask.execute(runnable);
    }
}
