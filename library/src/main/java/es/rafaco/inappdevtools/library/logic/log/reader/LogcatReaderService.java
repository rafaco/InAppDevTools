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

package es.rafaco.inappdevtools.library.logic.log.reader;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.core.app.JobIntentService;
//#else
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
//#endif

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationManager;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.prefs.utils.LastLogcatUtil;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.Shell;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatLine;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogcatReaderService extends JobIntentService {

    private static final int JOB_ID = 3003;
    private static final long LIVE_MAX_TIME = 2 * 1000;
    private static final long BACKGROUND_MAX_TIME = 5 * 60 * 1000;
    private static final int QUEUE_MAX_SIZE = 500;
    private final int BUFFER_SIZE = 1024;
    public final static String START_ACTION = "start_action";
    public final static String CANCEL_ACTION = "cancel_action";
    public final static String PARAM_KEY = "param";
    public static final String LOGCAT_COMMAND = "logcat -v long";

    private static boolean isReaderDebug = false;
    private boolean isReaderRunning = false;
    private boolean isInjectorRunning = false;
    private boolean isCancelled;
    private Process logcatProcess;
    private BufferedReader reader;
    private Queue<String> queue;
    private String lastLine;
    private Long lastInsertTime = DateUtils.getLong();
    private Timer processQueueTimer;
    private TimerTask processQueueTimerTask;

    private int ignoredCounter = 0;
    private int readCounter = 0;
    private int nullDiscardCounter = 0;
    private int sameDiscardCounter = 0;
    private int insertedCounter = 0;

    private List<Friendly> previousStartDateLines;
    private boolean startDateDuplicationPassed;
    private int startDateDiscardCounter = 0;

    //region [ PUBLIC STATIC ACCESS ]

    public static void enqueueWork(Context context, Intent intent) {
        if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService enqueueWork" );
        enqueueWork(context, LogcatReaderService.class, JOB_ID, intent);
    }

    public static Intent getStartIntent(Context context, String param) {
        if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService prepared start intent" );
        Intent intent =  new Intent();
        intent.setClass(context, LogcatReaderService.class);
        intent.setAction(START_ACTION);
        intent.putExtra(PARAM_KEY, param);
        return intent;
    }

    public static Intent getStopIntent(Context context) {
        if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService prepared stop intent" );
        Intent intent =  new Intent();
        intent.setClass(context, LogcatReaderService.class);
        intent.setAction(CANCEL_ACTION);
        return intent;
    }

    //endregion

    //region [ LIFECYCLE ]

    @Override
    public void onCreate() {
        if (isReaderDebug()){
            Log.v(Iadt.TAG, "LogcatReaderService onCreate" );
        }
        super.onCreate();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        ThreadUtils.setName("Iadt-LogWork");
        String action = intent.getAction();
        String param = intent.getStringExtra(PARAM_KEY);
        if (isReaderDebug()) Log.d(Iadt.TAG, ThreadUtils.formatOverview("LogcatReaderService onHandleWork(" + action + ", " + param + ")"));

        if (action != null && action.equals(CANCEL_ACTION)) {
            onStop(param);
        }else{
            onStart(param);
        }
    }

    private void onStart(String param) {
        if (isReaderDebug()) Log.d(Iadt.TAG, ThreadUtils.formatOverview("LogcatReaderService onStart"));

        if (restartRunnable!= null){
            removeRestartHandler();
            //exit();
            //return;
        }

        if (isReaderRunning){
            if (isReaderDebug()) Log.w(Iadt.TAG, "LogcatReaderService start skipped, reader already running" );
            return;
        }

        if (isInjectorRunning){
            if (isReaderDebug()) Log.w(Iadt.TAG, "LogcatReaderService start skipped, injector already running" );
            return;
        }

        gatherLog();
    }

    private void onStop(String param) {
        isReaderRunning = false;
        isCancelled = true;

        if (isReaderDebug()){
            Log.v(Iadt.TAG, "LogcatReaderService onStop");
            Log.v(Iadt.TAG, String.format("LogcatReaderService cancelled but inserted %s of %s read"
                    + " lines (ignored %s, filtered %s nulls and %s duplicated)",
                    insertedCounter, readCounter, ignoredCounter, nullDiscardCounter, sameDiscardCounter));
        }
        exit();
    }

    private void exit() {
        try {
            if (logcatProcess != null) {
                logcatProcess.exitValue();
                logcatProcess = null;
            }
        }
        catch(IllegalThreadStateException e) {
            logcatProcess.destroy();
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (Iadt.isDebug()){
            Log.d(Iadt.TAG, String.format("LogcatReaderService onDestroy - Inserted %s of %s read lines (ignored %s, filtered %s nulls and %s duplicated)",
                    insertedCounter, readCounter, ignoredCounter, nullDiscardCounter, sameDiscardCounter));
        }
        super.onDestroy();
    }
    
    //endregion


    //region [ STEP 1: FILL QUEUE ]

    private void gatherLog(){
        isCancelled = false;
        isReaderRunning = true;
        queue = new LinkedList<>();

        String command = LOGCAT_COMMAND;
        if (true){
            command += " -d";
        }

        boolean filterByDate = LastLogcatUtil.get() != 0;
        if (filterByDate){
            String logcatTime = DateUtils.formatLogcatDate(LastLogcatUtil.get());
            command += " -t '" + logcatTime + "'";
            previousStartDateLines = IadtController.getDatabase().friendlyDao().filterByDate(LastLogcatUtil.get());
        }else{
            previousStartDateLines = new ArrayList<>();
        }

        String[] fullCommand = Shell.formatBashCommand(command);
        if (isReaderDebug()){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < fullCommand.length; i++) {
                stringBuilder.append(fullCommand[i] + " ");
            }
            ThreadUtils.printOverview("LogcatReaderService");
            Log.v(Iadt.TAG, "LogcatReaderService executing command: " + command);
        }

        if (logcatProcess != null)
            logcatProcess.destroy();
        
        try {
            logcatProcess = Runtime.getRuntime().exec(fullCommand);
        }
        catch (Exception e) {
            FriendlyLog.logException("Exception", e);
            isReaderRunning = false;
        }

        try {
            reader = new BufferedReader(new InputStreamReader(
                    logcatProcess.getInputStream()),BUFFER_SIZE);
        }
        catch(IllegalArgumentException e){
            FriendlyLog.logException("Exception", e);
            isReaderRunning = false;
        }

        String line;
        try {
            while (isReaderRunning
                    && !isCancelled
                    && (line = reader.readLine())!= null) {

                readCounter ++;
                queue.add(line);
                if (TextUtils.isEmpty(line)) {
                    processQueueIfNeeded();
                }
            }
        }
        catch (IOException e) {
            FriendlyLog.logException("Exception", e);
            isReaderRunning = false;
        }

        if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService reader finished. Read: " + readCounter);
        isReaderRunning = false;
        if (logcatProcess!=null) logcatProcess.destroy();

        onPartFinished();
    }

    private void processQueueIfNeeded() {
        if(isInjectorRunning || queue.isEmpty() || isCancelled){
            return;
        }

        if (queue.size() > QUEUE_MAX_SIZE || DateUtils.getLong() > (lastInsertTime + getMaxQueueTime()) ) {
            processQueue();
        }
        /*else if (queue.size() > 0) {
            startUpdateTimer();
        }*/
    }

    private void startUpdateTimer() {
        if (processQueueTimer!=null){
            destroyTimer();
        }

        processQueueTimerTask = new TimerTask() {
            @Override
            public void run() {
                ThreadUtils.setName("Iadt-LogQueue");
                if (isReaderDebug())
                    Log.v(Iadt.TAG, "Log reader processQueueTimerTask running on "
                            + ThreadUtils.formatThread());
                processQueue();
            }
        };
        if (isReaderDebug()) Log.v(Iadt.TAG, "Log reader processQueueTimer created from "
                + ThreadUtils.formatThread());
        processQueueTimer = new Timer("Iadt-LogReader-Timer", false);
        processQueueTimer.schedule(processQueueTimerTask, getMaxQueueTime());
    }

    private void cancelTimerTask() {
        if (processQueueTimerTask!=null){
            processQueueTimerTask.cancel();
            processQueueTimerTask = null;
        }
    }

    private void destroyTimer() {
        cancelTimerTask();
        if (processQueueTimer!=null){
            processQueueTimer.cancel();
            processQueueTimer.purge();
            processQueueTimer=null;
        }
    }

    //endregion

    //region [ STEP 2: PROCESS QUEUE ]

    private void processQueue() {
        if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService processQueue started with " +  queue.size() + " items");
        isInjectorRunning = true;
        cancelTimerTask();
        List<Friendly> logsToInsert = new ArrayList<>();
        Friendly lastInsertion = null;
        String outputChannel = "";

        LongLogcatParser parser = new LongLogcatParser();
        while (!isCancelled
                && !(queue.isEmpty() || logsToInsert.size() > QUEUE_MAX_SIZE)) {

            String line = queue.poll();
            LogcatLine parse = parser.parse(line);
            if (parse == null) {
                continue;
            }

            /*if (!checkEmptyLines(line)
                    && !checkIsIgnored(line)
                    && !checkIsStartDateDuplication(line)) {

            }*/
            Friendly log = parse.parseToFriendly();
            if (log != null){
                if(!checkIsIgnored(log)
                        && !checkIsStartDateDuplication(log)
                        && !checkIsTabbedMultiline(log, lastInsertion)){
                    lastInsertion = log;
                    logsToInsert.add(log);
                }
            }else{
                Log.w("LOGS", "LogcatLine not added");
            }
        }

        onQueueProcessed(logsToInsert);
    }

    private boolean checkIsIgnored(Friendly newLog) {
        if(newLog.getMessage().contains("setTypeface with style")) {
            ignoredCounter++;
            if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService detected and ignored a line. Total is "+ ignoredCounter);
            return true;
        }
        return false;
    }

    private boolean checkIsStartDateDuplication(Friendly newLog) {
        if(startDateDuplicationPassed){
            return false;
        }

        if (previousStartDateLines.size()<1){
            startDateDuplicationPassed = true;
            return false;
        }

        for (Friendly line: previousStartDateLines) {
            if (newLog.equalContent(line)){
                startDateDiscardCounter++;
                previousStartDateLines.remove(line);
                return true;
            }
        }
        return false;
    }

    //Collapsed tabbed multiline like printed stacktrace into the initial line
    private boolean checkIsTabbedMultiline(Friendly currentLog, Friendly previousLog) {
        if (previousLog == null){
            return false;
        }

        if (currentLog.equalContentForCollapsing(previousLog)
                && currentLog.getMessage().startsWith("\t")){
            previousLog.setMessage(previousLog.getMessage() + Humanizer.newLine() + currentLog.getMessage());
            return true;
        }
        return false;
    }

    private boolean checkDuplicates(String newLine) {
        if(lastLine != null){
            if(newLine.equals(lastLine)){
                //TODO: Add a multiplicity counter to LogcatLine and increment it
                sameDiscardCounter++;
                if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService detected a duplicated line. Total is " + sameDiscardCounter);
                return true;
            }
        }
        return false;
    }

    private boolean checkEmptyLines(String newLine) {
        if(TextUtils.isEmpty(newLine)) {
            nullDiscardCounter++;
            if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService detected a null line ("+ nullDiscardCounter +")");
            return true;
        }
        return false;
    }

    //endregion

    //region [ STEP 3: INJECT TO DB ]

    private void onQueueProcessed(List<Friendly> logsToInsert) {
        if (!isCancelled){
            if (!logsToInsert.isEmpty()){
                IadtController.getDatabase().friendlyDao().insertAll(logsToInsert);

                insertedCounter += logsToInsert.size();
                lastInsertTime = DateUtils.getLong();

                long lastLogcatData = logsToInsert.get(logsToInsert.size()-1).getDate();
                LastLogcatUtil.set(lastLogcatData);

                if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService processQueue Inserted " + logsToInsert.size() + " lines");
            }
            else{
                if (isReaderDebug()) Log.w(Iadt.TAG, "LogcatReaderService processQueue No lines inserted");
            }
        }
        isInjectorRunning = false;

        if (insertedCounter>0){
            IadtController.get().improveSessionStart();
        }
        onPartFinished();
    }

    private void onPartFinished() {
        if(isCancelled){
            exit();
        }
        else if (!queue.isEmpty() && !isInjectorRunning) {
            processQueue();
        }
        else if (!isInjectorRunning && !isReaderRunning) {
            programNextExecution();
            exit();
        }
    }

    //endregion

    //region [ PROGRAM NEXT ]

    private Handler restartHandler = new Handler(Looper.getMainLooper());
    private static Runnable restartRunnable = null;

    private void programNextExecution() {
        if (isReaderDebug()) Log.v(Iadt.TAG, "LogcatReaderService programNextExecution() on " + getMaxQueueTime() + " ms");
        removeRestartHandler();
        restartRunnable = new Runnable() {
            @Override
            public void run() {
                restartRunnable = null;
                Context context = IadtController.get().getContext();
                Intent intent = LogcatReaderService.getStartIntent(context, "Next execution");
                LogcatReaderService.enqueueWork(context, intent);
            }
        };
        restartHandler.postDelayed(restartRunnable, getMaxQueueTime());
    }

    private void removeRestartHandler() {
        if (restartRunnable != null) {
            Log.d(Iadt.TAG, "LogcatReaderService removed restartHandler" );
            restartHandler.removeCallbacks(restartRunnable);
            restartRunnable = null;
        }
    }

    //endregion


    private static boolean isReaderDebug() {
        //WARNING: it output too much noise at log
        return isReaderDebug; //IadtController.get().isDebug();
    }

    private long getMaxQueueTime(){
        NavigationManager navigationManager = IadtController.get().getNavigationManager();
        if (navigationManager != null
                && AppUtils.isForegroundImportance(getApplicationContext())
                && navigationManager.isCurrentScreen(LogScreen.class)){
            return LIVE_MAX_TIME;
        }else{
            return BACKGROUND_MAX_TIME;
        }
    }

    /*private void addLifecycleObserver() {
        lifecycleObserver = new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            public void start() {
                //TODO: if !isRunning...
                //LogcatReaderService.start(getContext(), "LogScreen");
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            public void cancel() {
                LogcatReaderService.cancel(getContext());
            }
        };

        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);
    }
    protected void onStop() {
        //TODO: cancel process
        ProcessLifecycleOwner.get().getLifecycle().removeObserver(lifecycleObserver);
        //TODO: destroy service
    }
    */
}
