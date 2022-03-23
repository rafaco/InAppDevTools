/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.crash;

import android.content.Context;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import org.inappdevtools.library.logic.config.BuildConfigField;
import org.inappdevtools.library.logic.documents.DocumentRepository;
import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventDetectorsManager;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.events.detectors.app.ErrorAnrEventDetector;
import org.inappdevtools.library.logic.session.ActivityTracker;
import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.Crash;
import org.inappdevtools.library.storage.db.entities.Screenshot;
import org.inappdevtools.library.storage.db.entities.Session;
import org.inappdevtools.library.storage.db.entities.Sourcetrace;
import org.inappdevtools.library.storage.files.utils.ScreenshotUtils;
import org.inappdevtools.library.storage.prefs.utils.PendingCrashPrefs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.log.FriendlyLog;
import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.screens.crash.CrashScreen;
import org.inappdevtools.library.view.utils.Humanizer;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final int MAX_TRACES_PER_EXCEPTION = 100;
    public static final int MAX_STRING_LENGTH = 1000000;
    private final Thread.UncaughtExceptionHandler nextHandler; //pandora
    private final Context context;

    public CrashHandler(Context context, Thread.UncaughtExceptionHandler nextHandler) {
        this.context = context;
        this.nextHandler = nextHandler;
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        long startTime = new Date().getTime();
        Log.v(Iadt.TAG, "CrashHandler: processing uncaught exception");

        try {
            if (isFullRestart())
                stopAnrDetector();

            boolean isBackgroundException = ex instanceof CrashInterceptor.BackgroundException;
            Throwable exception = isBackgroundException ? ex.getCause() : ex;

            long friendlyLogId = FriendlyLog.logCrashInitial(exception.getMessage());
            Crash crash = buildCrash(exception);
            crash.setLogcatId(friendlyLogId);
            CrashInterceptor.ThreadInfo threadInfo;
            if (isBackgroundException) {
                threadInfo = ((CrashInterceptor.BackgroundException) ex).threadInfo;
            }else{
                threadInfo = new CrashInterceptor.ThreadInfo(thread);
            }
            addThreadInfo(crash, threadInfo);
            printLogcatError(crash);
            saveCrash(crash);
            updateSessionManager(crash);
            FriendlyLog.logCrashDetails(crash);

            if (isFullRestart()){
                PendingCrashPrefs.savePending();
                IadtController.get().beforeClose();
            }

            storeScreenshot(crash);
            storeDetailReport(crash);
            storeSourcetraces(crash, exception);

            if (isDebug())
                Log.v(Iadt.TAG, "CrashHandler: processing finished on "
                        + (new Date().getTime() - startTime) + " ms");

            onHandlerFinished(thread, ex);
        }
        catch (Exception e) {
            Log.e(Iadt.TAG, "CrashHandler CRASHED! exception while processing uncaughtException");
            Log.e(Iadt.TAG, "EXCEPTION: " + e.getCause() + " -> " + e.getMessage());
            Log.e(Iadt.TAG, Log.getStackTraceString(e));
            FriendlyLog.logException("Exception", e);
        }
    }


    private void stopAnrDetector() {
        // Early stop of AnrDetector due it start new threads which is currently forbidden.
        // Other detector will get stopped later on by beforeClose().
        EventManager eventManager = IadtController.get().getEventManager();
        if (eventManager == null) return;

        EventDetectorsManager eventDetectorsManager = eventManager.getEventDetectorsManager();
        if (eventDetectorsManager == null) return;

        EventDetector anrDetector = eventDetectorsManager.get(ErrorAnrEventDetector.class);
        if (anrDetector==null) return;

        anrDetector.stop();
    }

    private IadtDatabase getDb(){
        return IadtDatabase.get();
    }

    private boolean isDebug(){
        return IadtController.get().isDebug();
    }

    private boolean isFullRestart(){
        //TODO: logic to decide next action: fullRestart, activityRestart, serviceRestart or nothing
        // For nothing, CrashInterceptor will restore the main loop
        return true;
    }

    private boolean shouldPropagate() {
        return IadtController.get().getConfig().getBoolean(BuildConfigField.CALL_DEFAULT_CRASH_HANDLER);
    }

    private boolean shouldPropagateToSystem() {
        return false;
    }

    private void onHandlerFinished(Thread thread, Throwable ex) {

        if (shouldPropagate()){
            if (shouldPropagateToSystem()){
                if (isDebug()) Log.d(Iadt.TAG, "CrashHandler finish. calling system handler");
                CrashInterceptor.getSystemHandler().uncaughtException(thread, ex);
            }
            else{
                if (isDebug()) Log.d(Iadt.TAG, "CrashHandler finish. Calling default handler");
                nextHandler.uncaughtException(thread, ex);
            }
        }else{
            if (isDebug()) Log.d(Iadt.TAG, "CrashHandler finish.");

            if (isFullRestart())
                IadtController.get().restartApp(true);
            else
                OverlayService.performNavigation(CrashScreen.class);
        }
    }

    //region [ PROCESS CRASH ]

    @NonNull
    private Crash buildCrash(Throwable ex) {
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

        String stacktrace = Humanizer.truncateLines(Log.getStackTraceString(ex),
                MAX_TRACES_PER_EXCEPTION, true);
        crash.setStacktrace(stacktrace);

        ActivityTracker activityTracker = IadtController.get().getActivityTracker();
        crash.setForeground(!activityTracker.isInBackground());
        crash.setLastActivity(activityTracker.getCurrentName());
        return crash;
    }

    private void addThreadInfo(Crash crash, CrashInterceptor.ThreadInfo threadInfo) {
        crash.setThreadId(threadInfo.tid);
        crash.setMainThread(threadInfo.isMain);
        crash.setThreadName(threadInfo.threadName);
        crash.setThreadGroupName(threadInfo.threadGroupName);
    }

    private void printLogcatError(Crash crash) {
        Log.e(Iadt.TAG, "EXCEPTION: " + crash.getException());
        Log.e(Iadt.TAG, "MESSAGE: " + crash.getMessage());
        Log.e(Iadt.TAG, String.format("THREAD: [%s] %s from group %s",
                crash.getThreadId(),
                crash.getThreadName(),
                crash.getThreadGroupName()));
        Log.e(Iadt.TAG, "STACKTRACE: " + crash.getStacktrace());
    }

    private void saveCrash(Crash crash) {
        long sessionId = IadtController.get().getSessionManager().getCurrentUid();
        crash.setSessionId(sessionId);
        long crashId = getDb().crashDao().insert(crash);
        crash.setUid(crashId);
    }

    private void updateSessionManager(Crash crash) {
        Session session = IadtController.get().getSessionManager().getCurrent();
        session.setCrashId(crash.getUid());
        IadtController.get().getSessionManager().updateCurrent(session);
    }

    private void storeScreenshot(Crash crash) {
        long screenshotId = saveScreenshot(crash);
        crash.setScreenId(screenshotId);
        getDb().crashDao().update(crash);
    }

    private long saveScreenshot(Crash crash){
        Screenshot screenshot = ScreenshotUtils.take(true);
        screenshot.setCrashId(crash.getUid());
        if (screenshot != null){
            long screenId = getDb().screenshotDao().insert(screenshot);
            if (screenId > 0){
                return screenId;
            }
        }
        return -1;
    }

    private boolean storeDetailReport(Crash crash) {
        String docPath = DocumentRepository.storeDocument(DocumentType.CRASH, crash);
        crash.setReportPath(docPath);
        getDb().crashDao().update(crash);
        return true;
    }

    private Boolean storeSourcetraces(Crash crash, Throwable ex){
        if (isDebug()) Log.d(Iadt.TAG, "Storing stacktrace");
        List<Sourcetrace> traces = new ArrayList<>();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        boolean needTruncation = stackTrace.length > MAX_TRACES_PER_EXCEPTION;
        int tracesToAdd = stackTrace.length;
        if (needTruncation) {
            Log.v(Iadt.TAG, String.format("CrashHandle: Truncated exception traces from %s to %s.",
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
            trace.setLinkedId(crash.getUid());
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
                trace.setLinkedId(crash.getUid());
                trace.setLinkedType("crash");
                trace.setLinkedIndex(i+j);
                trace.setExtra("cause");
                traces.add(trace);
            }
        }
        //TODO: add truncated trace to show it has been truncated
        //if (needTruncation) traces.add(new Sourcetrace().isTruncated());

        getDb().sourcetraceDao().insertAll(traces);
        if (isDebug()) Log.d(Iadt.TAG, "Stored " + traces.size() + " traces");
        return true;
    }

    //endregion
}
