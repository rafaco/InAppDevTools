/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.events.detectors.crash;

import android.content.Context;
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
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventDetectorsManager;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.detectors.app.ErrorAnrEventDetector;
import es.rafaco.inappdevtools.library.logic.log.reader.LogcatReaderService;
import es.rafaco.inappdevtools.library.logic.session.ActivityTracker;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.files.utils.ScreenshotUtils;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final int MAX_TRACES_PER_EXCEPTION = 100;
    public static final int MAX_STRING_LENGTH = 1000000;
    private final Thread.UncaughtExceptionHandler previousHandle;
    private final Context context;
    private long currentCrashId;
    private long friendlyLogId;
    private DevToolsDatabase db;

    public CrashHandler(Context context, Thread.UncaughtExceptionHandler previousHandler) {
        this.context = context;
        this.previousHandle = previousHandler;
    }

    private boolean isDebug(){
        return IadtController.get().isDebug();
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        long startTime = new Date().getTime();
        Log.v(Iadt.TAG, "CrashHandler: processing uncaughtException");
        db = IadtController.get().getDatabase();

        try {
            friendlyLogId = FriendlyLog.logCrash(ex.getMessage());
            stopAnrDetector();
            Crash crash = buildCrash(thread, ex);
            printLogcatError(thread, crash);
            long crashId = storeCrash(crash);
            crash.setUid(crashId);

            long sessionId = updateSession(crashId);
            crash.setSessionId(sessionId);
            db.crashDao().update(crash);
            PendingCrashUtil.savePending();

            long screenshotId = saveScreenshot(crash);
            crash.setScreenId(screenshotId);
            db.crashDao().update(crash);

            IadtController.get().beforeClose();
            //TODO:
            flushLogcat();
            saveDetailReport(crash);
            saveStacktrace(crashId, ex);

            if (isDebug())
                Log.v(Iadt.TAG, "CrashHandler: processing finished on "
                        + (new Date().getTime() - startTime) + " ms");

            onCrashStored(thread, ex);
        }
        catch (Exception e) {
            Log.e(Iadt.TAG, "CrashHandler CRASHED! exception while processing uncaughtException on " + Humanizer.getElapsedTime(startTime));
            Log.e(Iadt.TAG, "EXCEPTION: " + e.getCause() + " -> " + e.getMessage());
            Log.e(Iadt.TAG, Log.getStackTraceString(e));
            FriendlyLog.logException("Exception", e);
        }
    }

    private void flushLogcat() {
        LogcatReaderService.getStopIntent(context);
    }

    private void stopAnrDetector() {
        // Early stop of AnrDetector, other get stopped later on by IadtController.beforeClose().
        // Reason: AnrDetector start new threads which is currently forbidden.
        EventManager eventManager = IadtController.get().getEventManager();
        if (eventManager == null) return;

        EventDetectorsManager eventDetectorsManager = eventManager.getEventDetectorsManager();
        if (eventDetectorsManager == null) return;

        EventDetector anrDetector = eventDetectorsManager.get(ErrorAnrEventDetector.class);
        if (anrDetector==null) return;

        anrDetector.stop();
    }

    private void onCrashStored(Thread thread, Throwable ex) {
        if (IadtController.get().getConfig().getBoolean(BuildConfigField.CALL_DEFAULT_CRASH_HANDLER)){
            if (isDebug()) Log.d(Iadt.TAG, "CrashHandler finish. Calling default handler");
            previousHandle.uncaughtException(thread, ex);
        }else{
            if (isDebug()) Log.d(Iadt.TAG, "CrashHandler finish. Restarting app");
            IadtController.get().restartApp(true);
        }
    }

    @NonNull
    private Crash buildCrash(Thread thread, Throwable ex) {
        final Crash crash = new Crash();
        crash.setDate(new Date().getTime());
        crash.setException(ex.getClass().getSimpleName());
        if (ex.getStackTrace()!=null && ex.getStackTrace().length > 0) {
            // Some exceptions doesn't have stacktrace
            // i.e. Binary XML file ... You must supply a layout_height attribute.
            crash.setExceptionAt(ex.getStackTrace()[0].toString());
        }
        crash.setMessage(ex.getMessage());
        Throwable cause = ex.getCause();
        if (cause != null){
            crash.setCauseException(cause.getClass().getSimpleName());
            crash.setCauseMessage(cause.getMessage());
            if (cause.getStackTrace() != null && cause.getStackTrace().length > 0){
                crash.setCauseExceptionAt(cause.getStackTrace()[0].toString());
            }
        }

        String stacktrace = Log.getStackTraceString(ex);
        if (stacktrace.length()>MAX_STRING_LENGTH){
            Log.w(Iadt.TAG, String.format("CrashHandle: Truncated stacktrace from %s to %s.",
                    stacktrace.length(), MAX_STRING_LENGTH));
            stacktrace = Humanizer.truncate(stacktrace, MAX_STRING_LENGTH);
        }
        crash.setStacktrace(stacktrace);

        crash.setThreadId(thread.getId());
        crash.setMainThread(ThreadUtils.isMain(thread));
        crash.setThreadName(thread.getName());
        crash.setThreadGroupName(thread.getThreadGroup().getName());

        ActivityTracker activityTracker = IadtController.get().getActivityTracker();
        crash.setForeground(!activityTracker.isInBackground());
        crash.setLastActivity(activityTracker.getCurrentName());
        return crash;
    }

    private void printLogcatError(Thread thread, Crash crash) {
        Log.e(Iadt.TAG, "EXCEPTION: " + crash.getException());
        Log.e(Iadt.TAG, "MESSAGE: " + crash.getMessage());
        Log.e(Iadt.TAG, String.format("THREAD: %s [%s] is %s. Main: %s",
                thread.getName(),
                thread.getId(),
                thread.getState().name(),
                String.valueOf(ThreadUtils.isMain(thread))));
        Log.e(Iadt.TAG, "STACKTRACE: " + crash.getStacktrace());
    }

    //TODO: currentCrashId never get used, why?
    //TODO: double get sessionId
    private long storeCrash(final Crash crash) {
        long sessionId = IadtController.get().getSessionManager().getCurrentUid();
        crash.setSessionId(sessionId);
        currentCrashId = db.crashDao().insert(crash);
        FriendlyLog.logCrashDetails(friendlyLogId, currentCrashId, crash);
        return currentCrashId;
    }

    private long updateSession(long crashId) {
        Session session = IadtController.get().getSessionManager().getCurrent();
        session.setCrashId(crashId);
        IadtController.get().getSessionManager().updateCurrent(session);
        return session.getUid();
    }

    private long saveScreenshot(Crash crash){
        Screenshot screenshot = ScreenshotUtils.take(true);
        if (screenshot != null){
            long screenId = db.screenshotDao().insert(screenshot);
            if (screenId > 0){
                return screenId;
            }
        }
        return -1;
    }

    private boolean saveDetailReport(Crash crash) {
        String docPath = DocumentRepository.storeDocument(DocumentType.CRASH, crash);
        crash.setReportPath(docPath);
        db.crashDao().update(crash);
        return true;
    }

    private Boolean saveStacktrace(long crashId, Throwable ex){
        if (isDebug()) Log.d(Iadt.TAG, "Storing stacktrace");
        List<Sourcetrace> traces = new ArrayList<>();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        boolean needTruncation = stackTrace.length > MAX_TRACES_PER_EXCEPTION;
        int tracesToAdd = stackTrace.length;
        if (needTruncation) {
            Log.w(Iadt.TAG, String.format("CrashHandle: Truncated exception traces from %s to %s.",
                    tracesToAdd, MAX_TRACES_PER_EXCEPTION));
            tracesToAdd = MAX_TRACES_PER_EXCEPTION;
        }
        int i=0;
        for (; i<tracesToAdd; i++){
            StackTraceElement current = stackTrace[i];
            Sourcetrace trace = new Sourcetrace();
            trace.setMethodName(current.getMethodName());
            trace.setClassName(current.getClassName());
            trace.setFileName(current.getFileName());
            trace.setLineNumber(current.getLineNumber());
            trace.setLinkedId(crashId);
            trace.setLinkedType("crash");
            trace.setLinkedIndex(i);
            trace.setExtra("exception");
            traces.add(trace);
        }
        //TODO: add truncated trace to show it has been truncated
        //if (needTruncation) traces.add(new Sourcetrace().isTruncated());

        if (ex.getCause()!=null){
            StackTraceElement[] causeTrace = ex.getCause().getStackTrace();
            tracesToAdd = causeTrace.length;
            needTruncation = causeTrace.length > MAX_TRACES_PER_EXCEPTION;
            if (needTruncation) {
                Log.w(Iadt.TAG, String.format("CrashHandle: Truncated cause traces from %s to %s.",
                        tracesToAdd, MAX_TRACES_PER_EXCEPTION));
                tracesToAdd = MAX_TRACES_PER_EXCEPTION;
            }
            for (int j=0; j<tracesToAdd; j++){
                StackTraceElement current = causeTrace[j];
                Sourcetrace trace = new Sourcetrace();
                trace.setMethodName(current.getMethodName());
                trace.setClassName(current.getClassName());
                trace.setFileName(current.getFileName());
                trace.setLineNumber(current.getLineNumber());
                trace.setLinkedId(crashId);
                trace.setLinkedType("crash");
                trace.setLinkedIndex(i+j);
                trace.setExtra("cause");
                traces.add(trace);
            }
        }
        //TODO: add truncated trace to show it has been truncated
        //if (needTruncation) traces.add(new Sourcetrace().isTruncated());

        db.sourcetraceDao().insertAll(traces);
        if (isDebug()) Log.d(Iadt.TAG, "Stored " + traces.size() + " traces");
        return true;
    }
}
