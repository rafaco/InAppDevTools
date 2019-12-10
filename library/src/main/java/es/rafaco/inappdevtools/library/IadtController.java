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

package es.rafaco.inappdevtools.library;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.Nullable;
//#else
//#endif

import java.util.TooManyListenersException;

import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.detectors.crash.ForcedRuntimeException;
import es.rafaco.inappdevtools.library.logic.info.InfoManager;
import es.rafaco.inappdevtools.library.logic.log.reader.LogcatReaderService;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationManager;
import es.rafaco.inappdevtools.library.logic.navigation.OverlayHelper;
import es.rafaco.inappdevtools.library.logic.session.SessionManager;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;

import com.readystatesoftware.chuck.CustomChuckInterceptor;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableManager;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.files.FileProviderUtils;
import es.rafaco.inappdevtools.library.storage.prefs.utils.NewBuildUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PrivacyConsentUtil;
import es.rafaco.inappdevtools.library.view.activities.IadtDialogActivity;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.activities.ReportDialogActivity;
import es.rafaco.inappdevtools.library.view.notifications.NotificationService;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatHelper;
import es.rafaco.inappdevtools.library.logic.reports.ReportHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotHelper;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public final class IadtController {

    private static IadtController INSTANCE;

    private Context context;
    private ConfigManager configManager;
    private SessionManager sessionManager;
    private EventManager eventManager;
    private SourcesManager sourcesManager;
    private RunnableManager runnableManager;
    private NavigationManager navigationManager;
    private InfoManager infoManager;
    public boolean isPendingInitFull;
    private OverlayHelper overlayHelper;
    private boolean sessionStartTimeImproved = false;

    protected IadtController(Context context) {
        if (INSTANCE != null) {
            throw new RuntimeException("IadtController is already initialized");
        }
        INSTANCE = this;
        init(context);
    }

    public static IadtController get() {
        if (INSTANCE == null) {
            return null;
        }
        return INSTANCE;
    }


    //region [ TWO-STEP INITIALIZATION ]

    private void init(Context context) {
        this.context = context.getApplicationContext();

        ThreadUtils.printOverview("IadtController init");

        boolean isEssentialUp = initEssential();
        if (!isEssentialUp)
            return;

        if (shouldDelayInitFull()){
            isPendingInitFull = true;
        }else{
            initFull();
        }

        if (shouldShowInitialDialog()){
            IadtDialogActivity.open(IadtDialogActivity.IntentAction.AUTO,
                    new Runnable() {
                        @Override
                        public void run() {
                            initFullIfPending();
                        }
                    },
                    null);
        }
    }

    private boolean initEssential() {
        configManager = new ConfigManager(context);

        if (!isEnabled()){
            Log.w(Iadt.TAG, "Iadt DISABLED by configuration");
            return false;
        }

        if (isDebug())
            Log.d(Iadt.TAG, "IadtController init essential");

        sessionManager = new SessionManager(context);
        eventManager = new EventManager(context);
        runnableManager = new RunnableManager((context));

        if (isDebug()){
            ThreadUtils.runOnBack("Iadt-InitBack",
                    new Runnable() {
                @Override
                public void run() {
                    DevToolsDatabase.getInstance().printOverview();
                }
            });
        }
        
        return true;
    }

    private boolean shouldDelayInitFull() {
        return !AppUtils.isForegroundImportance(context) ||
                !PrivacyConsentUtil.isAccepted() ||
                !PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY);
    }

    private boolean shouldShowInitialDialog() {
        Session session = getSessionManager().getCurrent();
        if (session.isNewBuild() && !NewBuildUtil.isBuildInfoSkipped() && !NewBuildUtil.isBuildInfoShown() ||
                !PrivacyConsentUtil.isAccepted() ||
                !PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            return true;
        }
        return false;
    }

    public void initFullIfPending(){
        if (shouldDelayInitFull()){
            return;
        }
        else if (isPendingInitFull){
            initFull();
        }
        else if (!OverlayService.isRunning && PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            //TODO: Research this hack, it smell bad
            if (isDebug())
                Log.d(Iadt.TAG, "Restarting OverlayHelper. Doze close it");
            overlayHelper = new OverlayHelper(getContext());
        }
    }

    private void initFull(){
        if (isDebug())
            Log.d(Iadt.TAG, "IadtController init full");

        initDelayedBackground();
        initForeground();
        isPendingInitFull = false;
    }

    public void initDelayedBackground() {
        if (isDebug())
            Log.d(Iadt.TAG, "IadtController initDelayedBackground");

        sourcesManager = new SourcesManager(getContext());
        infoManager = new InfoManager(getContext());

        Intent intent = LogcatReaderService.getStartIntent(getContext(), "Started from IadtController");
        LogcatReaderService.enqueueWork(getContext(), intent);
    }

    private void initForeground(){
        if (isDebug())
            Log.d(Iadt.TAG, "IadtController initForeground");

        if (getConfig().getBoolean(BuildConfig.OVERLAY_ENABLED)){
            navigationManager = new NavigationManager();
            overlayHelper = new OverlayHelper(context);
        }

        if (getConfig().getBoolean(BuildConfig.INVOCATION_BY_NOTIFICATION)){
            Intent intent = new Intent(getContext(), NotificationService.class);
            intent.setAction(NotificationService.ACTION_START_FOREGROUND_SERVICE);
            getContext().startService(intent);
        }
    }


    //endregion

    //region [ GETTERS ]

    public Context getContext() {
        return context;
    }

    public ConfigManager getConfig() {
        return configManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public RunnableManager getRunnableManager() {
        return runnableManager;
    }

    public SourcesManager getSourcesManager() {
        return sourcesManager;
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public OverlayHelper getOverlayHelper() {
        return overlayHelper;
    }

    public InfoManager getInfoManager() {
        return infoManager;
    }

    public static DevToolsDatabase getDatabase() {
        return DevToolsDatabase.getInstance();
    }

    public boolean isEnabled() {
        return getConfig().getBoolean(BuildConfig.ENABLED);
    }

    public boolean isDebug() {
        return getConfig().getBoolean(BuildConfig.DEBUG);
    }


    public OkHttpClient getOkHttpClient() {

        //TODO: relocate an create a unique interceptor, and a method to return it
        CustomChuckInterceptor httpGrabberInterceptor = new CustomChuckInterceptor(getContext());
        httpGrabberInterceptor.showNotification(false);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpGrabberInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .build();
        return client;
    }

    //endregion

    //region [ METHODS FOR FEATURES ]

    public void takeScreenshot() {
        if (!isEnabled()) return;

        Screenshot screenshot = new ScreenshotHelper().takeAndSaveScreen();
        FriendlyLog.log("I", "Iadt", "Screenshot","Screenshot taken");

        if(getConfig().getBoolean(BuildConfig.OVERLAY_ENABLED) && OverlayService.isInitialize()){
            getOverlayHelper().showIcon();
        }
        FileProviderUtils.openFileExternally(getContext(), screenshot.getPath());
    }

    public void startReportDialog() {
        Intent intent = new Intent(getContext(), ReportDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    public void sendReport(ReportHelper.ReportType type, final Object param) {
        switch (type){
            case CRASH:
                ThreadUtils.runOnBack("Iadt-CrashReport",
                        new Runnable() {
                    @Override
                    public void run() {
                        Crash crash;
                        if (param == null) {
                            crash = getDatabase().crashDao().getLast();
                        } else {
                            crash = getDatabase().crashDao().findById((long) param);
                        }
                        if (crash == null) {
                            Iadt.showError("Unable to found a crash to report");
                        }
                        else {
                            new ReportHelper().start(ReportHelper.ReportType.CRASH, crash);
                        }
                    }
                });
                break;

            case SESSION:
                ThreadUtils.runOnBack("Iadt-SessionReport",
                        new Runnable() {
                    @Override
                    public void run() {
                        //ArrayList<Uri> files = (ArrayList<Uri>)params;
                        //TODO: Session report
                        new ReportHelper().start(ReportHelper.ReportType.SESSION, param);
                    }
                });
                break;
        }
    }

    public static void cleanSession() {
        LogcatHelper.clearLogcatBuffer();
    }

    //endregion

    //region [ RESTART AND FORCE CLOSE ]

    public void restartApp(boolean isCrash){
        AppUtils.programRestart(getContext(), isCrash);

        forceCloseApp(isCrash);
    }

    public void forceCloseApp(boolean isCrash){
        FriendlyLog.log( "I", "App", "ForceStop", "Force Stop");

        if (!isCrash)
            beforeClose(); //on crash is performed by CrashHandler

        ThreadUtils.runOnBack(new Runnable() {
            @Override
            public void run() {
                AppUtils.exit();
            }
        }, 100);
    }

    public void beforeClose(){

        if (isDebug()) Log.d(Iadt.TAG, "Before force close");

        if(getRunnableManager().getForceCloseRunnable() != null){
            Log.i(Iadt.TAG, "Calling custom ForceCloseRunnable");
            getRunnableManager().getForceCloseRunnable().run();
        }

        if (isDebug()) Log.v(Iadt.TAG, "Stopping watchers");
        sessionManager.destroy();
        eventManager.destroy();

        /*<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
        ActivityManager am = (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getContext().getPackageName());*/

        if (isDebug()) Log.v(Iadt.TAG, "Stopping Notification Service");
        NotificationService.stop();

        if (isDebug()) Log.v(Iadt.TAG, "Stopping OverlayUI Service");
        OverlayService.stop();

        if (isDebug()) Log.v(Iadt.TAG, "Stopping LogcatReaderService");
        Intent intent = LogcatReaderService.getStopIntent(getContext());
        LogcatReaderService.enqueueWork(getContext(), intent);
    }

    //endregion


    public void crashUiThread() {
        Log.i(Iadt.TAG, "Crashing the UI thread...");
        final Exception cause = new TooManyListenersException(getContext().getString(R.string.simulated_crash_cause));
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                throw new ForcedRuntimeException(cause);
            }
        });
    }

    public void crashBackgroundThread() {
        Log.i(Iadt.TAG, "Crashing a background thread...");
        final Exception cause = new TooManyListenersException(getContext().getString(R.string.simulated_crash_cause));
        ThreadUtils.runOnBack(new Runnable() {
            @Override
            public void run() {
                throw new ForcedRuntimeException(cause);
            }
        });
    }

    public void improveSessionStart() {
        if (isEnabled() && !sessionStartTimeImproved){

            int pid = ThreadUtils.myPid();
            Session currentSession = getSessionManager().getCurrent();

            if (currentSession.getDate() != currentSession.getDetectionDate()){
                sessionStartTimeImproved = true;
                return;
            }

            long detectionDate = currentSession.getDetectionDate();
            String threadFilter = "%" + "Process: " + pid + "%";
            Friendly firstSessionLog = getDatabase().friendlyDao().getFirstSessionLog(threadFilter, detectionDate);

            if (firstSessionLog != null
                    && firstSessionLog.getDate()<detectionDate){

                //Update session item
                long improvedDate = firstSessionLog.getDate();
                currentSession.setDate(improvedDate);
                getSessionManager().updateCurrent(currentSession);

                //Update 'new session' log item
                String message = "Session " + currentSession.getUid() +" started";
                Friendly newSessionLog = getDatabase().friendlyDao().getNewSessionLog(message);
                if (newSessionLog != null
                        && improvedDate < newSessionLog.getDate()){
                    newSessionLog.setDate(improvedDate);
                    getDatabase().friendlyDao().update(newSessionLog);
                }

                sessionStartTimeImproved = true;
                if (isDebug()) Log.i(Iadt.TAG, "Improved session start time! "
                        + Humanizer.getElapsedTime(improvedDate, detectionDate));
            }
            else{
                Log.w(Iadt.TAG, "Unable to improve session start time");
            }
        }
    }
}

