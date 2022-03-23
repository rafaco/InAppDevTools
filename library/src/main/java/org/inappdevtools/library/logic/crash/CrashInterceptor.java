/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.crash;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.inappdevtools.library.logic.utils.ThreadUtils;
import org.inappdevtools.library.Iadt;

/**
 * This class intercept any crash in your app and process them using CrashHandler.
 * - UI thread: We surround your app main looper by a try-catch in order to catch all unhandled exceptions,
 *   to process them and to be able to recover the UI without restarting your app.
 * - Background thread: We set the DefaultUncaughtExceptionHandler to redirect them to the main looper,
 *   which is now safe and will process the exception.
 */
public class CrashInterceptor implements Runnable {

    Context context;
    private static Thread.UncaughtExceptionHandler systemHandler;

    public CrashInterceptor(Context context) {
        this.context = context;
    }

    public static void initialise(Context context) {
        //Save system handler before Pandora override it
        systemHandler = Thread.getDefaultUncaughtExceptionHandler();
        Log.v(Iadt.TAG, "CrashInterceptor: system handler is " + systemHandler.toString());

        new Handler(Looper.getMainLooper()).post(new CrashInterceptor(context));
    }

    @Override
    public void run() {
        //Keep current crash handler (system default or overwritten one)
        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        Log.v(Iadt.TAG, "CrashInterceptor: current handler is " + currentHandler.toString());

        //Init our crash handler
        CrashHandler crashHandler = new CrashHandler(context, systemHandler);

        //Catch exceptions thrown in BACKGROUND threads and redirect them to MAIN thread
        Thread.setDefaultUncaughtExceptionHandler(new BackgroundCrashHandler(new Handler()));

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Log.v(Iadt.TAG, "CrashInterceptor: enabled on main loop and background threads");
                Looper.loop();

                //Following code should never happen. For safety it throw an exception to system handler
                Thread.setDefaultUncaughtExceptionHandler(systemHandler);
                throw new RuntimeException("Main thread loop unexpectedly exited");
            }
            catch (BackgroundException e) {
                //Catch redirected exceptions from BACKGROUND threads
                Log.e(Iadt.TAG, "CrashInterceptor received a exception from a background thread "
                        + e.threadInfo.threadName + " [" + e.threadInfo.tid + "]");
                crashHandler.uncaughtException(Thread.currentThread(), e);
                //showCrashDisplayActivity(e.getCause());
            }
            catch (Throwable e) {
                //Catch exceptions thrown in the MAIN threads
                Log.e(Iadt.TAG, "CrashInterceptor caught a exception in the UI thread.");
                crashHandler.uncaughtException(Thread.currentThread(), e);
                //showCrashDisplayActivity(e);
            }
        }
    }

    public static Thread.UncaughtExceptionHandler getSystemHandler() {
        return systemHandler;
    }

    /**
     * This handler catches exceptions in the background threads and propagates them to the UI thread
     */
    static class BackgroundCrashHandler implements Thread.UncaughtExceptionHandler {

        private final Handler mHandler;

        BackgroundCrashHandler(Handler handler) {
            mHandler = handler;
        }

        public void uncaughtException(final Thread thread, final Throwable e) {
            Log.v(Iadt.TAG, "BackgroundCrashHandler caught a exception in the background thread "
                    + thread + ", propagating it to the UI thread.");
            final ThreadInfo threadInfo = new ThreadInfo(thread);
            mHandler.post(new Runnable() {
                public void run() {
                    throw new BackgroundException(threadInfo, e);
                }
            });
        }
    }

    /**
     * Object to keep information about a thread, recovered before the thread get close
     */
    static class ThreadInfo {
        long tid;
        String threadName;
        String threadGroupName;
        boolean isMain;
        String state;

        public ThreadInfo(Thread thread) {
            this.tid = thread.getId(); //VALIDATE: It was Process.myTid()
            this.threadName = thread.getName();
            this.threadGroupName = thread.getThreadGroup().getName();
            this.isMain = ThreadUtils.isMain(thread);
            this.state = thread.getState().name();
        }
    }

    /**
     * Wrapper class for exceptions caught in the background
     */
    static class BackgroundException extends RuntimeException {

        ThreadInfo threadInfo;

        /**
         * @param e original exception
         * @param threadInfo object wrapping thread information
         */
        BackgroundException(ThreadInfo threadInfo, Throwable e) {
            super(e);
            this.threadInfo = threadInfo;
        }
    }

    /*void showCrashDisplayActivity(Throwable e) {
        Intent i = new Intent(context, CrashDialogActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("e", e);
        context.startActivity(i);
    }*/
}
