package es.rafaco.inappdevtools.library.logic.watcher.anr;

import android.os.AsyncTask;
import android.util.Log;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;

public class AnrLogger {

    private ANRWatchDog watcher;

    public AnrLogger() {
        watcher = new ANRWatchDog()
                .setANRListener(new ANRWatchDog.ANRListener() {
                    @Override
                    public void onAppNotResponding(ANRError error) {
                        onAnrDetected(error);
                    }
                })
                .setIgnoreDebugger(true);
        watcher.start();
        Log.d(DevTools.TAG, "AnrLogger started");
    }

    public void destroy(){
        watcher.interrupt();
    }

    private void onAnrDetected(ANRError error) {
        String errorString;
        errorString = String.format("ANR ERROR: %s - %s", error.getMessage(), error.getCause());
        DevTools.showMessage(errorString);
        Log.e(DevTools.TAG, errorString);

        Anr anr = new Anr();
        anr.setDate(new Date().getTime());
        anr.setMessage(error.getMessage());
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
                long anrId = db.anrDao().insert(anr);
                FriendlyLog.logAnr(anrId, anr);
            }
        });
    }
}
