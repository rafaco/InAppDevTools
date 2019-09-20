package es.rafaco.inappdevtools.library.logic.events.detectors.crash;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Logcat;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.storage.db.entities.SourcetraceDao;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.view.notifications.NotificationService;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotHelper;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

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
        db = IadtController.get().getDatabase();
        Log.v(Iadt.TAG, "CrashHandler: processing uncaughtException");

        try {
            friendlyLogId = FriendlyLog.logCrash(ex.getMessage());
            //stopDevToolsServices();
            Crash crash = buildCrash(thread, ex);
            printLogcatError(thread, crash);
            long crashId = storeCrash(crash);
            PendingCrashUtil.savePending();

            IadtController.get().beforeClose();
            saveLogcat(crashId);
            saveScreenshot();
            saveDetailReport();
            saveStacktrace(crashId, ex);

            Log.v(Iadt.TAG, "CrashHandler: process finished on " + (new Date().getTime() - startTime) + " ms");
            onCrashStored( thread, ex);
        }
        catch (Exception e) {
            Log.e(Iadt.TAG, "CrashHandler: exception while processing uncaughtException on " + Humanizer.getElapsedTime(startTime));
            Log.e(Iadt.TAG, "EXCEPTION: " + e.getCause() + " -> " + e.getMessage());
            Log.e(Iadt.TAG, String.valueOf(e.getStackTrace()));
            FriendlyLog.logException("Exception", e);
        }
    }

    private void onCrashStored(Thread thread, Throwable ex) {
        if (IadtController.get().getConfig().getBoolean(BuildConfig.CALL_DEFAULT_CRASH_HANDLER)){
            Log.i(Iadt.TAG, "CrashHandler: Let the exception propagate to default handler");
            previousHandle.uncaughtException(thread, ex);
        }else{
            Log.e(Iadt.TAG, "CrashHandler: Restarting app");
            IadtController.get().restartApp(true);
        }
    }

    //TODO: Close our services before to prevent "Schedule restart"
    private void stopDevToolsServices() {
        Intent in = new Intent(context, OverlayService.class);
        context.stopService(in);
        in = new Intent(context, NotificationService.class);
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
        ActivityEventDetector activityWatcher = (ActivityEventDetector) IadtController.get().getEventManager()
                .getEventDetectorsManager().get(ActivityEventDetector.class);
        crash.setStacktrace(Log.getStackTraceString(ex));
        crash.setThreadId(thread.getId());
        crash.setMainThread(ThreadUtils.isMain(thread));
        crash.setThreadName(thread.getName());
        crash.setThreadGroupName(thread.getThreadGroup().getName());
        crash.setForeground(!activityWatcher.isInBackground());
        crash.setLastActivity(activityWatcher.getLastActivityResumed());
        return crash;
    }

    private void printLogcatError(Thread thread, Crash crash) {
        Log.e(Iadt.TAG, "EXCEPTION: " + crash.getException() + " -> " + crash.getMessage());
        Log.e(Iadt.TAG, crash.getStacktrace());
        Log.e(Iadt.TAG, String.format("Thread %s [%s] is %s. Main: %s",
                thread.getName(),
                thread.getId(),
                thread.getState().name(),
                String.valueOf(ThreadUtils.isMain(thread))));
    }

    //TODO: currentCrashId never get used, why?
    private long storeCrash(final Crash crash) {
        currentCrashId = db.crashDao().insert(crash);
        FriendlyLog.logCrashDetails(friendlyLogId, currentCrashId, crash);
        return currentCrashId;
    }

    private Boolean saveScreenshot(){
        ScreenshotHelper helper = new ScreenshotHelper();
        Screenshot screenshot = helper.takeScreenIntoFile(true);
        if (screenshot != null){
            long screenId = db.screenshotDao().insert(screenshot);
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
        LogcatHelper helper = new LogcatHelper();
        Log.d(Iadt.TAG, "Extracting logcat");

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
        IadtController.get().getDatabase().crashDao().update(current);

        return true;
    }

    private Boolean saveStacktrace(long crashId, Throwable ex){
        Log.d(Iadt.TAG, "Storing stacktrace");
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
        Log.d(Iadt.TAG, "Stored " + traces.size() + " traces");
        return true;
    }
}
