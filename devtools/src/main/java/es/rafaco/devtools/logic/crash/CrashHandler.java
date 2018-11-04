package es.rafaco.devtools.logic.crash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.logic.utils.AppUtils;
import es.rafaco.devtools.logic.utils.DateUtils;
import es.rafaco.devtools.logic.utils.FriendlyLog;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.storage.db.DevToolsDatabase;
import es.rafaco.devtools.storage.db.entities.Crash;
import es.rafaco.devtools.view.notifications.NotificationUIService;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.screens.log.LogHelper;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreenHelper;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1

    private final Thread.UncaughtExceptionHandler previousHandle;
    private Context appContext;
    private Context baseContext;
    private long crashId;

    public CrashHandler(Context appContext, Context baseContext, Thread.UncaughtExceptionHandler previousHandler) {
        this.appContext = appContext;
        this.baseContext = baseContext;
        this.previousHandle = previousHandler;
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        long startTime = new Date().getTime();
        Log.d(DevTools.TAG, "CrashHandler: processing uncaughtException");

        try {
            //stopDevToolsServices();
            Crash crash = buildCrash(thread, ex);
            Log.d(DevTools.TAG, "printLogcatError on " + String.valueOf(new Date().getTime() - startTime));
            printLogcatError(thread, crash);
            Log.d(DevTools.TAG, "storeCrash on " + String.valueOf(new Date().getTime() - startTime));
            storeCrash(crash);
            Log.d(DevTools.TAG, "savePending on " + String.valueOf(new Date().getTime() - startTime));
            PendingCrashUtil.savePending();
            Log.d(DevTools.TAG, "saveLogcat on " + String.valueOf(new Date().getTime() - startTime));
            saveLogcat();
            Log.d(DevTools.TAG, "saveScreenshot on " + String.valueOf(new Date().getTime() - startTime));
            saveScreenshot();

            Log.d(DevTools.TAG, "CrashHandler: process finished ok on " + String.valueOf(new Date().getTime() - startTime));
            onCrashStored( thread, ex, crash);

            //TODO: RESEARCH handleApplicationCrash
            /*// Bring up crash dialog, wait for it to be dismissed
            ActivityManagerNative.getDefault().handleApplicationCrash(
                    mApplicationObject, new ApplicationErrorReport.CrashInfo(e));*/
        }
        catch (Exception e) {
            Log.e(DevTools.TAG, "CrashHandler: exception while processing uncaughtException on " + DateUtils.getElapsedTime(startTime));
            Log.e(DevTools.TAG, "EXCEPTION: " + e.getCause() + " -> " + e.getMessage());
            Log.e(DevTools.TAG, String.valueOf(e.getStackTrace()));
            e.printStackTrace();
        }
    }

    //TODO: Close our services before to prevent "Schedule restart"
    private void stopDevToolsServices() {
        Context context = DevTools.getAppContext();
        Intent in = new Intent(context, OverlayUIService.class);
        context.stopService(in);
        in = new Intent(context, NotificationUIService.class);
        context.stopService(in);
    }

    @NonNull
    private Crash buildCrash(Thread thread, Throwable ex) {
        final Crash crash = new Crash();
        crash.setDate(new Date().getTime());
        crash.setException(ex.getClass().getSimpleName());
        crash.setExceptionAt(ex.getStackTrace()[1].toString());
        crash.setMessage(ex.getMessage());

        Throwable cause = ex.getCause();
        if (cause != null){
            crash.setCauseException(cause.getClass().getSimpleName());
            crash.setCauseMessage(cause.getMessage());
            if (cause.getStackTrace() != null && cause.getStackTrace().length > 1){
                crash.setCauseExceptionAt(cause.getStackTrace()[1].toString());
            }
        }

        //TODO: crash.setWhere(ex.getStackTrace()[0].toString());
        //ex.getStackTrace()[0].getLineNumber()
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stackTraceString = sw.toString();
        crash.setStacktrace(stackTraceString);

        crash.setThreadId(thread.getId());
        crash.setMainThread(ThreadUtils.isTheUiThread(thread));
        crash.setThreadName(thread.getName());
        crash.setThreadGroupName(thread.getThreadGroup().getName());

        crash.setForeground(!DevTools.getActivityLogManager().isInBackground());
        crash.setLastActivity(DevTools.getActivityLogManager().getLastActivityResumed());
        return crash;
    }

    private void printLogcatError(Thread thread, Crash crash) {
        Log.e(DevTools.TAG, "EXCEPTION: " + crash.getException() + " -> " + crash.getMessage());
        Log.e(DevTools.TAG, crash.getStacktrace());
        Log.e(DevTools.TAG, String.format("Thread %s [%s] is %s. Main: %s",
                thread.getName(),
                thread.getId(),
                thread.getState().name(),
                String.valueOf(ThreadUtils.isTheUiThread(thread))));
    }

    private void storeCrash(final Crash crash) {
        DevToolsDatabase db = DevTools.getDatabase();
        crashId = db.crashDao().insert(crash);
        //TODO
        //FriendlyLog.logCrash(crashId, crash);
        Log.d(DevTools.TAG, "Crash stored in db");
    }

    private Boolean saveScreenshot(){
        ScreenHelper helper = new ScreenHelper();
        byte[] screen = helper.buildPendingData();
        if (screen != null){
            DevToolsDatabase db = DevTools.getDatabase();
            Crash current = db.crashDao().getLast();
            current.setRawScreen(screen);
            db.crashDao().update(current);
            Log.d(DevTools.TAG, "Raw screen stored in crash");
            return true;
        }

        /*Screen screen = helper.takeScreen();
        if (screen != null){
            DevToolsDatabase db = DevTools.getDatabase();
            long screenId = db.screenDao().insert(screen);
            if (screenId > 0){
                Crash current = db.crashDao().getLast();
                current.setScreenId(screenId);
                db.crashDao().update(current);
                Log.d(DevTools.TAG, "Crash screen stored in db");
                return true;
            }
        }*/
        return false;
    }

    private Boolean saveLogcat(){
        LogHelper helper = new LogHelper();
        String logcat = helper.buildRawReport();
        if (!StringUtils.isEmpty(logcat)){
            DevToolsDatabase db = DevTools.getDatabase();
            Crash current = db.crashDao().getLast();
            current.setRawLogcat(logcat);
            db.crashDao().update(current);
            Log.d(DevTools.TAG, "Raw logcat stored in crash");
            return true;
        }
        return false;
    }

    private void onCrashStored(Thread thread, Throwable ex, final Crash crash) {
        //showDialog(exClass, exMessage);
        //startExceptionActivity(crash.getException(), crash.getMessage(), crash);

        if (DevTools.getConfig().crashHandlerCallDefaultHandler){
            Log.i(DevTools.TAG, "CrashHandler: Let the exception propagate to default handler");
            previousHandle.uncaughtException(thread, ex);
        }else{
            Log.e(DevTools.TAG, "CrashHandler: Restarting app");
            AppUtils.programRestart(DevTools.getAppContext());
            AppUtils.killMyProcess();
        }
    }



    //TODO: REMOVE AFTER REVIEW
    //region [ OLD STUFF TO REMOVE ]

    private void startExceptionActivity(String exClass, String exMessage, Crash crash) {
        Log.e(DevTools.TAG, "Requesting Exception Dialog...");
        Intent intent = new Intent(appContext, CrashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("TITLE", "DevTools caught a crash");
        intent.putExtra("MESSAGE", exClass + ": " + exMessage);
        intent.putExtra("CRASH", crash);
        appContext.startActivity(intent);
    }

    private void callService() {
        Intent intent = new Intent(appContext, OverlayUIService.class);
        intent.putExtra(OverlayUIService.EXTRA_INTENT_ACTION, OverlayUIService.IntentAction.EXCEPTION);
        appContext.startService(intent);
    }

    private void showDialog(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext)
                .setTitle(title)
                //.setTitle("Ups, I did it again")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("REPORT",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: report
                    }
                })
                .setNegativeButton("RESTART_APP",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        AppUtils.programRestart(appContext);
                        AppUtils.exit();
                    }
                })
                .setNeutralButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        AppUtils.exit();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        alertDialog.show();
    }

    private String getErrorMessgae(Throwable e) {
        StackTraceElement[] stackTrackElementArray = e.getStackTrace();
        String crashLog = e.toString() + "\n\n";
        crashLog += "--------- Stack trace ---------\n\n";
        for (int i = 0; i < stackTrackElementArray.length; i++) {
            crashLog += "    " + stackTrackElementArray[i].toString() + "\n";
        }
        crashLog += "-------------------------------\n\n";

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        crashLog += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if (cause != null) {
            crashLog += cause.toString() + "\n\n";
            stackTrackElementArray = cause.getStackTrace();
            for (int i = 0; i < stackTrackElementArray.length; i++) {
                crashLog += "    " + stackTrackElementArray[i].toString()
                        + "\n";
            }
        }
        crashLog += "-------------------------------\n\n";
        return crashLog;
    }

    //endregion
}