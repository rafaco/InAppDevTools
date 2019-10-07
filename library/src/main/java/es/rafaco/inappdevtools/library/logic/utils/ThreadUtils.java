package es.rafaco.inappdevtools.library.logic.utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import es.rafaco.inappdevtools.library.Iadt;

public class ThreadUtils {

    private ThreadUtils() { throw new IllegalStateException("Utility class"); }

    public static boolean isMain(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Looper.getMainLooper().isCurrentThread()
                : isMain(Thread.currentThread());
    }

    public static boolean isMain(Thread thread){
        return thread == Looper.getMainLooper().getThread();
    }


    public static void runOnMain(Runnable runnable){
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static void runOnMain(Runnable runnable, long delay){
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
    }

    public static void runOnBack(Runnable runnable){
        AsyncTask.execute(runnable);
    }

    public static void runOnBack(final String threadName, final Runnable runnable){
        //TODO: this keep threads open for a bit as TIMED_WAITING
        /*ExecutorService executorservice = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable arg0) {
                return new Thread(arg0, threadName);
            }
        });
        executorservice.submit(runnable);*/
        AsyncTask.execute(runnable);
    }

    public static void runOnBack(Runnable runnable, long delay){
        Handler handler = new Handler();
        handler.postDelayed(runnable, delay);
    }


    public static void setName(String name) {
        Thread thread = Thread.currentThread();
        if (isMain(thread)){
            Log.w(Iadt.TAG, "Skipped setName(" + name + "). You are on MAIN thread.");
            return;
        }
        if (!thread.getName().contains(name)){
            thread.setName(name + "-" + thread.getName());
        }
    }


    public static String formatThread() {
        return formatThread(Thread.currentThread());
    }

    public static String formatThread(Thread thread){
        return formatThreadId(thread) + " "
                + formatThreadDescription(thread) + " "
                + thread.getState();
    }

    private static String formatThreadId(Thread info){
        String id = String.valueOf(info.getId());
        while(id.length()<4){
            id = "  " + id;
        }
        return id;
    }

    private static String formatThreadDescription(Thread info){
        String standard = info.toString();
        return standard.replaceFirst("Thread", "");
    }

    public static void printOverview(String from){
        Log.d(Iadt.TAG, formatOverview(from));
    }

    public static String formatOverview(String from){
        String threadName = (isMain()) ? "MAIN" : "a background";
        String result = from + " is running on " + threadName + " thread";
        if (!isMain()){
            Thread thread = Thread.currentThread();
            result += ": " + thread.getName() + " " + thread.getId();
        }
        return result;
    }

    public static String formatCurrentName(){
        String thread = (isMain()) ? "main" : "background";
        return thread;
    }


    public static Set<Thread> getAllStacktraces(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        return threadSet;
    }


    public static void addDummy(String name, final long delay, final Runnable onFinish){
        Thread splashThread = new Thread(name) {
            public void run() {
                try {
                    synchronized (this) {
                        wait(delay);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    onFinish.run();
                    interrupt();
                }
            }
        };
        splashThread.start();
    }

    public static void addDummyAsync(final String name, final long delay, final Runnable onFinish){
        AsyncTask<String, Void, Void> backTask = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                Thread.currentThread().setName(name);
                try {
                    Thread.sleep( delay );
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                onFinish.run();
                cancel(true);
            }
        };

        backTask.execute();
    }

    public static int myPid(){
        return  android.os.Process.myPid();
    }

    public static void printCurrentStacktrace(){
        new Throwable().printStackTrace();
        //Log.e("TEST", new Throwable());
    }
}
