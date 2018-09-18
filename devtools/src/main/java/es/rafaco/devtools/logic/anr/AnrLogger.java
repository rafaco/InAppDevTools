package es.rafaco.devtools.logic.anr;

import android.os.AsyncTask;
import android.util.Log;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.storage.db.DevToolsDatabase;
import es.rafaco.devtools.storage.db.entities.Anr;

public class AnrLogger {

    private ANRWatchDog watcher;

    public AnrLogger() {
        watcher = new ANRWatchDog()
                .setANRListener(new ANRWatchDog.ANRListener() {
                    @Override
                    public void onAppNotResponding(ANRError error) {
                        onAnrDetected(error, false);
                    }
                })
                .setIgnoreDebugger(true);
        watcher.start();
        Log.d(DevTools.TAG, "AnrLogger started");
    }

    public void destroy(){
        watcher.interrupt();
    }

    private void onAnrDetected(ANRError error, boolean isWarning) {
        String errorString;
        //if(isWarning) errorString = String.format("ANR WARNING: %s - %s", error.getMessage(), error.getCause());
        errorString = String.format("ANR ERROR: %s - %s", error.getMessage(), error.getCause());
        DevTools.showMessage(errorString);
        Log.e(DevTools.TAG, errorString);

        Anr anr = new Anr();
        anr.setDate(new Date().getTime());
        anr.setMessage(error.getMessage().toString());
        anr.setCause(error.getCause().toString());

        StringWriter sw = new StringWriter();
        error.printStackTrace(new PrintWriter(sw));
        String stackTraceString = sw.toString();
        anr.setStacktrace(stackTraceString);

        storeAnr(anr);
    }

    private void storeAnr(final Anr anr) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                db.anrDao().insertAll(anr);
                Log.d(DevTools.TAG, "AnrLogger - error stored");
            }
        });
    }
}
