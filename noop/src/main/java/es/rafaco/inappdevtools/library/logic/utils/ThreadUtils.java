/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.Set;

//TODO: this class seems better as operational.
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

    public static void runOnBack(Runnable runnable, long delay){
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        handler.postDelayed(runnable, delay);
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

    public static String formatGroup(ThreadGroup group){
        return String.format( "Group %s has %s groups and %s active threads",
                group.getName(), group.activeGroupCount(), group.activeCount());
    }


    public static Set<Thread> getAllStacktraces(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        return threadSet;
    }


    public static Thread[] getCurrentGroupThreads(){
        ThreadGroup targetGroup = getCurrentGroup();
        return getThreadsFromGroup(targetGroup);
    }

    public static Thread[] getAllThreads(){
        ThreadGroup targetGroup = getRootGroup();
        return getThreadsFromGroup(targetGroup);
    }

    private static ThreadGroup getCurrentGroup() {
        return Thread.currentThread().getThreadGroup();
    }

    public static ThreadGroup getRootGroup(){
        ThreadGroup rootGroup = getCurrentGroup();
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }
        return rootGroup;
    }

    public static Thread[] getThreadsFromGroup(ThreadGroup group){
        int size = group.activeCount();
        if (group.getParent() != null){
            size++;
        }

        Thread[] threads = new Thread[size];
        while (group.enumerate(threads, true ) == threads.length) {
            threads = new Thread[threads.length * 2];
        }
        return threads;
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

}
