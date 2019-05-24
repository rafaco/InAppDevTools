package es.rafaco.inappdevtools.library.logic.watcher.crash;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

//#ifdef MODERN
import androidx.annotation.NonNull;
//#else
//@import android.support.annotation.NonNull;
//#endif

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.initialization.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Logcat;
import es.rafaco.inappdevtools.library.storage.db.entities.Screen;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.storage.db.entities.SourcetraceDao;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.view.notifications.NotificationUIService;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenHelper;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler previousHandle;
    private final Context context;
    private long currentCrashId;
    private long friendlyLogId;
    private DevToolsDatabase db;

    public CrashHandler(Context context, Thread.UncaughtExceptionHandler previousHandler) {
        this.context = context;
        this.previousHandle = previousHandler;
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        long startTime = new Date().getTime();
        db = DevTools.getDatabase();
        Log.v(DevTools.TAG, "CrashHandler: processing uncaughtException");

        try {
            friendlyLogId = FriendlyLog.logCrash(ex.getMessage());
            //stopDevToolsServices();
            Crash crash = buildCrash(thread, ex);
            printLogcatError(thread, crash);
            long crashId = storeCrash(crash);
            PendingCrashUtil.savePending();

            DevTools.beforeClose();
            saveLogcat(crashId);
            saveScreenshot();
            saveDetailReport();
            saveStacktrace(crashId, ex);

            Log.v(DevTools.TAG, "CrashHandler: process finished on " + (new Date().getTime() - startTime) + " ms");
            onCrashStored( thread, ex);
        }
        catch (Exception e) {
            Log.e(DevTools.TAG, "CrashHandler: exception while processing uncaughtException on " + DateUtils.getElapsedTime(startTime));
            Log.e(DevTools.TAG, "EXCEPTION: " + e.getCause() + " -> " + e.getMessage());
            Log.e(DevTools.TAG, String.valueOf(e.getStackTrace()));
            FriendlyLog.logException("Exception", e);
        }
    }

    private void onCrashStored(Thread thread, Throwable ex) {
        if (DevTools.getConfig().crashHandlerCallDefaultHandler){
            Log.i(DevTools.TAG, "CrashHandler: Let the exception propagate to default handler");
            previousHandle.uncaughtException(thread, ex);
        }else{
            Log.e(DevTools.TAG, "CrashHandler: Restarting app");
            DevTools.restartApp(true);
        }
    }

    //TODO: Close our services before to prevent "Schedule restart"
    private void stopDevToolsServices() {
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

        crash.setStacktrace(Log.getStackTraceString(ex));
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

    //TODO: currentCrashId never get used, why?
    private long storeCrash(final Crash crash) {
        currentCrashId = db.crashDao().insert(crash);
        FriendlyLog.logCrashDetails(friendlyLogId, currentCrashId, crash);
        return currentCrashId;
    }

    private Boolean saveScreenshot(){
        ScreenHelper helper = new ScreenHelper();
        Screen screen = helper.takeScreenIntoFile(true);
        if (screen != null){
            long screenId = db.screenDao().insert(screen);
            if (screenId > 0){
                Crash current = db.crashDao().getLast();
                current.setScreenId(screenId);
                db.crashDao().update(current);
                return true;
            }
        }
        return false;
    }

    private Boolean saveLogcat(long crashId){
        LogHelper helper = new LogHelper();
        Log.d(DevTools.TAG, "Extracting logcat");

        Logcat logcat = helper.buildCrashReport(crashId);
        if (logcat != null){
            long logcatId = db.logcatDao().insert(logcat);
            if (logcatId > 0){
                Crash current = db.crashDao().getLast();
                current.setLogcatId(logcatId);
                db.crashDao().update(current);
                return true;
            }
        }
        return false;
    }

    private boolean saveDetailReport() {
        CrashHelper helper = new CrashHelper();
        Crash current = db.crashDao().getLast();

        String report = helper.parseToInfoGroup(current).toString();
        String filePath = DevToolsFiles.storeCrashDetail(current.getUid(), report);
        current.setReportPath(filePath);
        DevTools.getDatabase().crashDao().update(current);

        return true;
    }

    private Boolean saveStacktrace(long crashId, Throwable ex){
        Log.d(DevTools.TAG, "Storing stacktrace");
        SourcetraceDao stacktraceDao = db.sourcetraceDao();
        List<Sourcetrace> traces = new ArrayList<>();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        int i=0;
        for (; i<stackTrace.length; i++){
            StackTraceElement current = stackTrace[i];
            Sourcetrace trace = new Sourcetrace();
            trace.setMethodName(current.getMethodName());
            trace.setClassName(current.getClassName());
            trace.setFileName(current.getFileName());
            trace.setLineNumber(current.getLineNumber());
            trace.setLinkedId(crashId);
            trace.setLinkedType("crash");
            trace.setLinkedIndex(i);
            if (i==0) trace.setExtra("crash");
            traces.add(trace);
        }

        if (ex.getCause()!=null){
            StackTraceElement[] causeTrace = ex.getCause().getStackTrace();
            for (int j=0; j<causeTrace.length; j++){
                StackTraceElement current = causeTrace[j];
                Sourcetrace trace = new Sourcetrace();
                trace.setMethodName(current.getMethodName());
                trace.setClassName(current.getClassName());
                trace.setFileName(current.getFileName());
                trace.setLineNumber(current.getLineNumber());
                trace.setLinkedId(crashId);
                trace.setLinkedType("crash");
                trace.setLinkedIndex(i+j);
                if (j==0) trace.setExtra("cause");
                traces.add(trace);
            }
        }

        stacktraceDao.insertAll(traces);
        Log.d(DevTools.TAG, "Stored " + traces.size() + " traces");
        return true;
    }
}
