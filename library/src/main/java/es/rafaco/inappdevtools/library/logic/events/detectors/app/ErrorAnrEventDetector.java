package es.rafaco.inappdevtools.library.logic.events.detectors.app;

import android.os.AsyncTask;
import android.util.Log;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;

public class ErrorAnrEventDetector extends EventDetector {

    private ANRWatchDog watchDog;

    public ErrorAnrEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void start() {

        watchDog = new ANRWatchDog()
                .setANRListener(new ANRWatchDog.ANRListener() {
                    @Override
                    public void onAppNotResponding(ANRError error) {
                        Anr anr = parseAnr(error);
                        storeAnr(anr);
                        eventManager.fire(Event.ERROR_ANR, anr);
                    }
                })
                .setIgnoreDebugger(true);
        watchDog.start();
    }

    @Override
    public void stop() {
        watchDog.interrupt();
    }

    private Anr parseAnr(ANRError error) {
        String errorString;
        errorString = String.format("ANR ERROR: %s - %s", error.getMessage(), error.getCause());
        Iadt.showMessage(errorString);
        Log.e(Iadt.TAG, errorString);

        Anr anr = new Anr();
        anr.setDate(new Date().getTime());
        anr.setMessage(error.getMessage());
        anr.setCause(error.getCause().toString());

        StringWriter sw = new StringWriter();
        error.printStackTrace(new PrintWriter(sw));
        String stackTraceString = sw.toString();
        anr.setStacktrace(stackTraceString);

        return anr;
    }

    private void storeAnr(final Anr anr) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = IadtController.get().getDatabase();
                long anrId = db.anrDao().insert(anr);
                FriendlyLog.logAnr(anrId, anr);
            }
        });
    }
}
