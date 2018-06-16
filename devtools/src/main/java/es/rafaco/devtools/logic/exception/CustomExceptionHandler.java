package es.rafaco.devtools.logic.exception;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.DevToolsService;
import es.rafaco.devtools.db.Crash;
import es.rafaco.devtools.utils.AppUtils;
import es.rafaco.devtools.utils.ThreadUtils;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final int MAX_STACK_TRACE_SIZE = 131071; //128 KB - 1

    private final Thread.UncaughtExceptionHandler previousHandle;
    private Context appContext;
    private Context baseContext;

    public CustomExceptionHandler(Context appContext, Context baseContext, Thread.UncaughtExceptionHandler previousHandler) {
        this.appContext = appContext;
        this.baseContext = baseContext;
        this.previousHandle = previousHandler;
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        Log.e(DevTools.TAG, "uncaughtException called");
        //Log.e(DevTools.TAG, "Custom message: " + getErrorMessgae(ex.getCause()));

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

        Log.e(DevTools.TAG, "EXCEPTION: " + crash.getException() + " -> " + crash.getMessage());
        Log.e(DevTools.TAG, stackTraceString);
        Log.e(DevTools.TAG, String.format("Thread %s [%s] is %s. Main:  ",
                thread.getName(),
                thread.getId(),
                thread.getState().name(),
                String.valueOf(ThreadUtils.isTheUiThread(thread))));

        //appContext.startService(DbService.buildCrashIntent(appContext, crash));

        /*AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                db.crashDao().insertAll(crash);
                Log.d(DevTools.TAG, "Crash db size is: " + db.crashDao().count());
                onCrashStored(thread, ex, crash);
            }
        });*/


        onCrashStored( thread, ex, crash);
        Log.e(DevTools.TAG, "uncaughtException finished");
    }

    private void onCrashStored(Thread thread, Throwable ex, Crash crash) {
        //showDialog(exClass, exMessage);
        startExceptionActivity(crash.getException(), crash.getMessage(), crash);

        if (DevTools.CALL_DEFAULT_EXCEPTION_HANDLER){
            Log.e(DevTools.TAG, "onCrashStored let the exception propagate");
            previousHandle.uncaughtException(thread, ex);
        }else{
            Log.e(DevTools.TAG, "onCrashStored destroy");
            AppUtils.exit();
        }
    }

    private void startExceptionActivity(String exClass, String exMessage, Crash crash) {
        Log.e(DevTools.TAG, "Requesting Exception Dialog...");
        Intent intent = new Intent(appContext, ExceptionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("TITLE", "DevTools caught a crash");
        intent.putExtra("MESSAGE", exClass + ": " + exMessage);
        intent.putExtra("CRASH", (Serializable) crash);
        appContext.startActivity(intent);
    }

    private void callService() {
        Intent intent = new Intent(appContext, DevToolsService.class);
        intent.putExtra(DevToolsService.EXTRA_INTENT_ACTION, DevToolsService.IntentAction.EXCEPTION);
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
}