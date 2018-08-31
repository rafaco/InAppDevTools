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
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.view.NotificationUIService;
import es.rafaco.devtools.view.OverlayUIService;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.utils.AppUtils;
import es.rafaco.devtools.utils.ThreadUtils;
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

        try {
            Log.d(DevTools.TAG, "CrashHandler.uncaughtException() called");

            stopDevToolsServices();
            Crash crash = buildCrash(thread, ex);
            printLogcatError(thread, crash);
            storeCrash(crash);
            PendingCrashUtil.savePending();

            saveLogcat();
            saveScreenshot();

            onCrashStored( thread, ex, crash);

            //TODO: RESEARCH handleApplicationCrash
            /*// Bring up crash dialog, wait for it to be dismissed
            ActivityManagerNative.getDefault().handleApplicationCrash(
                    mApplicationObject, new ApplicationErrorReport.CrashInfo(e));*/

            Log.d(DevTools.TAG, "CrashHandler.uncaughtException() finished ok");
        } catch (Exception e) {
            Log.e(DevTools.TAG, "CrashHandler got an exception");
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
        crash.setMessage(ex.getMessage());
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String stackTraceString = sw.toString();
        //Reduce data to 128KB so we don't get a TransactionTooLargeException when sending the intent.
        //The limit is 1MB on Android but some devices seem to have it lower.
        if (stackTraceString.length() > MAX_STACK_TRACE_SIZE) {
            String disclaimer = " [stack trace too large]";
            //stackTraceString = stackTraceString.substring(0, MAX_STACK_TRACE_SIZE - disclaimer.length()) + disclaimer;
        }
        crash.setStacktrace(sw.toString());

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
        Log.d(DevTools.TAG, "Crash stored in db");
    }

    private Boolean saveScreenshot(){
        ScreenHelper helper = new ScreenHelper();
        byte[] screen = helper.takeScreenAsByteArray();
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
            Log.i(DevTools.TAG, "onCrashStored - Let the exception propagate to default handler");
            previousHandle.uncaughtException(thread, ex);
        }else{
            Log.e(DevTools.TAG, "onCrashStored - Destroy app");
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
                .setNegativeButton("RESTART",new DialogInterface.OnClickListener() {
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