package es.rafaco.inappdevtools.library.logic.log.reader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.Nullable;
//#else
import android.support.annotation.Nullable;
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
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.prefs.utils.LastLogcatUtil;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatLine;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogcatReaderService extends IntentService {

    public final static String START_ACTION = "start_action";
    public final static String CANCEL_ACTION = "cancel_action";
    public final static String PARAM_KEY = "param";
    public static final String LOGCAT_COMMAND = "logcat -v long";
    public static final String BASH_PATH = "/system/bin/sh";
    public static final String BASH_ARGS = "-c";
    private static final long LIVE_MAX_TIME = 2 * 1000;
    private static final long BACKGROUND_MAX_TIME = 5 * 60 * 1000;
    private static final int QUEUE_MAX_SIZE = 500;

    private final int BUFFER_SIZE = 1024;
    private boolean isReaderRunning = false;
    private boolean isInjectorRunning = false;
    private boolean isCancelled;
    private Process logcatProcess;
    private BufferedReader reader;
    private Queue<String> queue;
    private String lastLine;
    private Long lastInsertTime = DateUtils.getLong();
    private Timer processQueueTimer = new Timer(false);
    private TimerTask processQueueTimerTask;
    private Intent bindingIntent;

    private int ignoredCounter = 0;
    private int readCounter = 0;
    private int nullDiscardCounter = 0;
    private int sameDiscardCounter = 0;
    private int insertedCounter = 0;

    private List<Friendly> previousStartDateLines;
    private boolean startDateDuplicationPassed;
    private int startDateDiscardCounter = 0;

    public LogcatReaderService() {
        super("LogcatReaderService");
    }

    //region [ BINDING ]

    private final IBinder mBinder = new LocalService();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.bindingIntent = intent;
        return mBinder;
    }

    public class LocalService extends Binder {
        public LogcatReaderService getService(){
            return LogcatReaderService.this;
        }
    }

    //endregion

    //region [ PUBLIC STATIC ACCESS ]

    public static void start(Context context, String param) {
        if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService static start" );
        Intent intent =  new Intent();
        intent.setClass(context, LogcatReaderService.class);
        intent.setAction(START_ACTION);
        intent.putExtra(PARAM_KEY, param);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    public static void cancel(Context context) {
        if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService static cancel" );
        Intent intent =  new Intent();
        intent.setClass(context, LogcatReaderService.class);
        intent.setAction(CANCEL_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    //endregion

    //region [ ACTION CONTROLLER ]

    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        performAction(intent.getAction(), intent.getStringExtra(PARAM_KEY));
    }

    public void performAction(String action, String param) {
        if (isDebug()) Log.d(Iadt.TAG, ThreadUtils.formatOverview("LogcatReaderService " + action + "-" + param));
        
        if (action != null && action.equals(CANCEL_ACTION)) {
            onCancelled(param);
        }else{
            onStarted(param);
        }
    }

    private void onCancelled(String param) {
        isReaderRunning = false;
        isCancelled = true;

        if (isDebug()){
            Log.v(Iadt.TAG, "LogcatReaderService onCancelled");
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

    private void onStarted(String param) {
        if (isReaderRunning){
            if (isDebug()) Log.w(Iadt.TAG, "LogcatReaderService start skipped, already running" );
            return;
        }

        gatherLog();

        //sendResult(result);
    }
    
    //endregion

    //region [ FILL QUEUE ]

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

        String[] fullCommand = new String[] { BASH_PATH, BASH_ARGS, command};
        if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService executing command: " + command);

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

        if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService reader finished. Read: " + readCounter);
        isReaderRunning = false;
        logcatProcess.destroy();

        onPartFinished();
    }

    private void processQueueIfNeeded() {
        if(isInjectorRunning || queue.isEmpty() || isCancelled){
            return;
        }

        if (queue.size() > QUEUE_MAX_SIZE || DateUtils.getLong() > (lastInsertTime + getMaxQueueTime()) ) {
            processQueue();
        }
        else if (queue.size() > 0) {
            startUpdateTimerIfNeeded();
        }
    }

    private void startUpdateTimerIfNeeded() {
        if (processQueueTimerTask!=null){
            return;
        }

        processQueueTimerTask = new TimerTask() {
            @Override
            public void run() {
                processQueue();
                cancelUpdateTimer();
            }
        };

        processQueueTimer.schedule(processQueueTimerTask, getMaxQueueTime());
    }

    private void cancelUpdateTimer() {
        if (processQueueTimerTask!=null){
            processQueueTimerTask.cancel();
            processQueueTimerTask = null;
        }
    }

    //endregion

    //region [ PROCESS QUEUE ]

    private void processQueue() {
        if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService processQueue started with " +  queue.size() + " items");
        isInjectorRunning = true;
        cancelUpdateTimer();
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
            if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService detected and ignored a line. Total is "+ ignoredCounter);
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
                if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService detected a duplicated line. Total is " + sameDiscardCounter);
                return true;
            }
        }
        return false;
    }

    private boolean checkEmptyLines(String newLine) {
        if(TextUtils.isEmpty(newLine)) {
            nullDiscardCounter++;
            if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService detected a null line ("+ nullDiscardCounter +")");
            return true;
        }
        return false;
    }

    //endregion

    //region [ INJECT TO DB ]

    private void onQueueProcessed(List<Friendly> logsToInsert) {
        if (!isCancelled){
            if (!logsToInsert.isEmpty()){
                IadtController.getDatabase().friendlyDao().insertAll(logsToInsert);

                insertedCounter += logsToInsert.size();
                lastInsertTime = DateUtils.getLong();

                long lastLogcatData = logsToInsert.get(logsToInsert.size()-1).getDate();
                LastLogcatUtil.set(lastLogcatData);

                if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService processQueue Inserted " + logsToInsert.size() + " lines");
            }
            else{
                if (isDebug()) Log.w(Iadt.TAG, "LogcatReaderService processQueue No lines inserted");
            }
        }
        isInjectorRunning = false;
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

    private void programNextExecution() {
        if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService programNextExecution()");
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                LogcatReaderService.start(getApplicationContext(), "Update");
            }
        }, getMaxQueueTime());
    }

    //endregion

    //region [ LIFECYCLE ]

    @Override
    public void onCreate() {
        if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService onCreate" );
        super.onCreate();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        if (isDebug()) Log.v(Iadt.TAG, "LogcatReaderService onStarted" );
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (isDebug()) Log.d(Iadt.TAG, "LogcatReaderService onStartCommand" );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (isDebug()){
            Log.v(Iadt.TAG, "LogcatReaderService onDestroy" );
            Log.v(Iadt.TAG, "LogcatReaderService isReaderRunning = " + (isReaderRunning ==true ? "true" : "false"));
            Log.v(Iadt.TAG, "LogcatReaderService isInjectorRunning = " + (isInjectorRunning ==true ? "true" : "false"));
            Log.d(Iadt.TAG, String.format("LogcatReaderService inserted %s of %s read lines (ignored %s, filtered %s nulls and %s duplicated)",
                    insertedCounter, readCounter, ignoredCounter, nullDiscardCounter, sameDiscardCounter));
        }
        super.onDestroy();
    }

    //endregion


    private static boolean isDebug() {
        return Iadt.isDebug();
    }

    private long getMaxQueueTime(){
        String currentOverlay = IadtController.get().getCurrentOverlay();
        if (currentOverlay == null || !currentOverlay.equals(LogScreen.class.getSimpleName())){
            return BACKGROUND_MAX_TIME;
        }else{
            return LIVE_MAX_TIME;
        }
    }


    /*
    private void sendResult(String msg){
        Intent intent = new Intent();
        intent.setAction(RESPONSE_ACTION);
        intent.putExtra(RESPONSE_PARAM_KEY,msg);
        sendBroadcast(intent);
    }*/



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
