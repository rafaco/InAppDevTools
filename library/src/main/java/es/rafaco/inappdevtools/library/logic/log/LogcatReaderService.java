package es.rafaco.inappdevtools.library.logic.log;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.prefs.utils.LastLogcatUtil;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogLine;

public class LogcatReaderService extends IntentService {

    public final static String START_ACTION = "start_action";
    public final static String CANCEL_ACTION = "cancel_action";
    public final static String PARAM_KEY = "param";

    public static final String LOGCAT_COMMAND = "logcat -v time";
    public static final String BASH_PATH = "/system/bin/sh";
    public static final String BASH_ARGS = "-c";
    private static final int QUEUE_MAX_SIZE = 500;
    private static final int QUEUE_MAX_TIME = 2000;
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

    private int ignoredCounter = 0;
    private int readCounter = 0;
    private int nullDiscardCounter = 0;
    private int sameDiscardCounter = 0;
    private int insertedCounter = 0;
    private Intent bindingIntent;

    public LogcatReaderService() {
        super("LogcatReaderService");
    }


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


    @Override
    protected void onHandleIntent(@Nullable Intent intent){
        performAction(intent.getAction(), intent.getStringExtra(PARAM_KEY));
    }

    public void performAction(String action, String param) {
        Log.d(Iadt.TAG, ThreadUtils.formatRunningOnString("LogcatReaderService performAction"));
        Log.v(Iadt.TAG, "LogcatReaderService action: " + action + " param: " + param);
        
        if (action != null && action.equals(CANCEL_ACTION)) {
            onCancelled(param);
        }else{
            onStarted(param);
        }
    }

    private void onCancelled(String param) {
        isReaderRunning = false;
        isCancelled = true;
        if (logcatProcess != null)
            logcatProcess.destroy();

        Log.v(Iadt.TAG, "LogcatReaderService onCancelled");
        Log.i(Iadt.TAG, String.format("Inserted %s of %s read lines (ignored %s, filtered %s nulls and %s duplicated)",
                insertedCounter, readCounter, ignoredCounter, nullDiscardCounter, sameDiscardCounter));
    }

    private void onStarted(String param) {
        if (isReaderRunning){
            Log.w(Iadt.TAG, "LogcatReaderService Skipped, already running" );
            return;
        }

        //String result = run(param);
        //sendResult(result);

        gatherLog();
    }

    private void gatherLog(){
        isCancelled = false;
        isReaderRunning = true;
        queue = new LinkedList<>();

        String command = LOGCAT_COMMAND;
        if (true){
            command += " -d";
        }
        long lastLogcat = LastLogcatUtil.get();
        if (lastLogcat != 0){
            String logcatTime = DateUtils.formatLogcatDate(lastLogcat);
            command += " -t '" + logcatTime + "'";
        }
        String[] fullCommand = new String[] { BASH_PATH, BASH_ARGS, command};

        Log.v(Iadt.TAG, "LogcatReaderService executing command: " + command);

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

                if (!checkEmptyLines(line)) {
                    queue.add(line);
                    processQueueIfNeeded();
                }
            }
        }
        catch (IOException e) {
            FriendlyLog.logException("Exception", e);
            isReaderRunning = false;
        }

        isReaderRunning = false;
        Log.i(Iadt.TAG, "LogcatReaderService finished!");

        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogcatReaderService.start(getApplicationContext(), "Update");
            }
        }, QUEUE_MAX_TIME * 2);

        logcatProcess.destroy();
    }

    private void processQueueIfNeeded() {
        if(isInjectorRunning || queue.isEmpty()){
            return;
        }

        if (queue.size() > QUEUE_MAX_SIZE) {
            processQueue();
        }else{
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
                processQueueTimerTask.cancel();
                processQueueTimerTask = null;
            }
        };

        processQueueTimer.schedule(processQueueTimerTask, QUEUE_MAX_TIME);
    }

    private void processQueue() {
        isInjectorRunning = true;
        List<Friendly> logsToInsert = new ArrayList<>();
        while (!isCancelled
                && !(queue.isEmpty() || logsToInsert.size() > QUEUE_MAX_SIZE)) {
            Friendly log = parseLine(queue.poll());
            if (log!=null){
                logsToInsert.add(log);
            }else{
                //TODO: log not added
            }
        }

        if (!isCancelled){
            if (!logsToInsert.isEmpty()){
                insertedCounter += logsToInsert.size();
                IadtController.getDatabase().friendlyDao().insertAll(logsToInsert);
                Friendly newerLog = logsToInsert.get(0); //TODO
                long newLastLogcatString = newerLog.getDate();
                LastLogcatUtil.set(newLastLogcatString);
                lastInsertTime = DateUtils.getLong();
                FriendlyLog.log("V", "Iadt", "LogcatReaderService", "processQueue Inserted " + logsToInsert.size() + " lines");
            }else{
                FriendlyLog.log("V", "Iadt", "LogcatReaderService", "processQueue No lines inserted");
            }
        }
        isInjectorRunning = false;
    }

    private Friendly parseLine(String newLine) {
        if (checkEmptyLines(newLine)) return null;
        //if (checkDuplicates(newLine)) return null;

        lastLine = newLine;
        LogLine logLine = LogLine.newLogLine(newLine, false);
        return logLine.parseToFriendly();
    }

    private boolean checkIgnored(String line) {
        if(line.contains("setTypeface with style")) {
            ignoredCounter++;
            Log.w(Iadt.TAG, "LogcatReaderService detected a ignored line ("+ ignoredCounter +")");
            return true;
        }
        return false;
    }

    private boolean checkDuplicates(String newLine) {
        if(lastLine != null){
            if(newLine.equals(lastLine)){
                //TODO: Add a multiplicity counter to LogLine and increment it
                sameDiscardCounter++;
                Log.w(Iadt.TAG, "LogcatReaderService detected a duplicated line ("+ sameDiscardCounter +"): " + newLine);
                return true;
            }
        }
        return false;
    }

    private boolean checkEmptyLines(String newLine) {
        if(TextUtils.isEmpty(newLine)) {
            nullDiscardCounter++;
            Log.w(Iadt.TAG, "LogcatReaderService detected a null line ("+ nullDiscardCounter +")");
            return true;
        }
        return false;
    }




    public static void start(Context context, String param) {
        Log.d(Iadt.TAG, "LogcatReaderService start" );
        Intent intent =  new Intent();
        intent.setClass(context, LogcatReaderService.class);
        //intent.putExtra(REQUEST_PARAM_KEY, param);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    /*public static void cancel(Context context) {
        Log.d(Iadt.TAG, "LogcatReaderService cancel" );
        Intent intent =  new Intent();
        intent.setClass(context, LogcatReaderService.class);
        intent.setAction(CANCEL_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    private String run(String param){
        LogcatFillerHelper helper = new LogcatFillerHelper();
        List<Friendly> data = helper.getData();
        helper.injectData(data);
        return param;
    }

    private void sendResult(String msg){
        Intent intent = new Intent();
        intent.setAction(RESPONSE_ACTION);
        intent.putExtra(RESPONSE_PARAM_KEY,msg);
        sendBroadcast(intent);
    }*/



    //region [ LIFECYCLE ]

    @Override
    public void onCreate() {
        Log.d(Iadt.TAG, "LogcatReaderService onCreate" );
        super.onCreate();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Log.d(Iadt.TAG, "LogcatReaderService onStarted" );
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(Iadt.TAG, "LogcatReaderService onStartCommand" );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(Iadt.TAG, "LogcatReaderService onDestroy" );
        Log.d(Iadt.TAG, "LogcatReaderService isReaderRunning = " + (isReaderRunning ==true ? "true" : "false"));
        Log.i(Iadt.TAG, String.format("Inserted %s of %s read lines (ignored %s, filtered %s nulls and %s duplicated)",
                insertedCounter, readCounter, ignoredCounter, nullDiscardCounter, sameDiscardCounter));
        super.onDestroy();
    }

    //endregion

    /*private void addLifecycleObserver() {
        lifecycleObserver = new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            public void start() {
                //TODO: if !isRunning...
                //LogcatReaderService.start(getContext(), "FriendlyLogScreen");
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
