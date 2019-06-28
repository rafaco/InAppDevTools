package es.rafaco.inappdevtools.library.logic.utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class ThreadUtils {

    private ThreadUtils() { throw new IllegalStateException("Utility class"); }

    public static String formatRunningOnString(String fromText){
        String threadName = (amIOnUiThread()) ? "MAIN" : "a background";
        String result = fromText + " is running on " + threadName + " thread";
        if (!amIOnUiThread()){
            Thread thread = Thread.currentThread();
            result += ": " + thread.getName() + " " + thread.getId();
        }
        return result;
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

    public static void runOnUiThread(Runnable runnable, long delay){
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
    }

    public static void runOnBackThread(Runnable runnable){
        AsyncTask.execute(runnable);
    }

    public static void runOnBackThread(Runnable runnable, long delay){
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        handler.postDelayed(runnable, delay);
    }
}
