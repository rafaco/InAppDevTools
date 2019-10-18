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
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.prefs.utils.FirstStartUtil;
import com.readystatesoftware.chuck.CustomChuckInterceptor;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableManager;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.files.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.activities.ReportDialogActivity;
import es.rafaco.inappdevtools.library.view.activities.WelcomeDialogActivity;
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
    private EventManager eventManager;
    private SourcesManager sourcesManager;
    private RunnableManager runnableManager;
    private NavigationManager navigationManager;
    private InfoManager infoManager;
    public boolean isPendingForegroundInit;
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

    //region [ INITIALIZATION ]

    private void init(Context context) {
        this.context = context.getApplicationContext();

        ThreadUtils.printOverview("IadtController init");

        boolean backgroundIsUp = initBackground();
        if (!backgroundIsUp)
            return;

        if (!AppUtils.isForegroundImportance(context)){
            isPendingForegroundInit = true;
        }else{
            initForeground();
        }
    }

    private boolean initBackground() {
        configManager = new ConfigManager(context);

        if (!isEnabled()){
            Log.w(Iadt.TAG, "Iadt DISABLED by configuration");
            return false;
        }

        if (isDebug())
            Log.d(Iadt.TAG, "Initializing background services...");

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

    public void initDelayedBackground() {
        sourcesManager = new SourcesManager(getContext());
        infoManager = new InfoManager(getContext());

        Intent intent = LogcatReaderService.getStartIntent(getContext(), "Started from IadtController");
        LogcatReaderService.enqueueWork(getContext(), intent);
    }

    public void initForegroundIfPending(){
        if (isPendingForegroundInit){
            initForeground();
        }
    }

    private void initForeground(){
        initDelayedBackground();

        ThreadUtils.printOverview("IadtController initForeground");
        if (FirstStartUtil.isFirstStart()){
            WelcomeDialogActivity.open(WelcomeDialogActivity.IntentAction.PRIVACY,
                    new Runnable() {
                        @Override
                        public void run() {
                            onInitForeground();
                        }
                    },
                    null);
            FirstStartUtil.saveFirstStart();
        }
        else if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            WelcomeDialogActivity.open(WelcomeDialogActivity.IntentAction.OVERLAY,
                    new Runnable() {
                        @Override
                        public void run() {
                            onInitForeground();
                        }
                    },
                    null);
        }
        else{
            onInitForeground();
        }
    }

    private void onInitForeground(){
        isPendingForegroundInit = false;

        if (isDebug())
            Log.d(Iadt.TAG, "Initializing foreground services...");

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
                            crash = IadtController.get().getDatabase().crashDao().getLast();
                        } else {
                            crash = IadtController.get().getDatabase().crashDao().findById((long) param);
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
        FriendlyLog.log( "I", "Run", "Restart", "Restart programmed");
        AppUtils.programRestart(getContext(), isCrash);

        forceCloseApp(isCrash);
    }

    public void forceCloseApp(boolean isCrash){
        FriendlyLog.log( "I", "Run", "ForceClose", "Force Close");

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

        if(getRunnableManager().getForceCloseRunnable() != null)
            getRunnableManager().getForceCloseRunnable().run();

        if (isDebug())
            Log.w(Iadt.TAG, "Stopping watchers");
        eventManager.destroy();

        /*<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
        ActivityManager am = (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getContext().getPackageName());*/

        if (isDebug())
            Log.w(Iadt.TAG, "Stopping Notification Service");
        NotificationService.stop();

        if (isDebug())
            Log.w(Iadt.TAG, "Stopping OverlayUI Service");
        OverlayService.stop();

        if (isDebug())
            Log.w(Iadt.TAG, "Stopping LogcatReaderService");
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
            Session currentSession = IadtController.getDatabase().sessionDao().getLast();

            if (currentSession.getDate() != currentSession.getDetectionDate()){
                sessionStartTimeImproved = true;
                return;
            }

            long detectionDate = currentSession.getDetectionDate();
            String threadFilter = "%" + "Process: " + pid + "%";
            Friendly firstSessionLog = IadtController.getDatabase().friendlyDao().getFirstSessionLog(threadFilter, detectionDate);

            if (firstSessionLog != null
                    && firstSessionLog.getDate()<detectionDate){

                //Improve session item
                long improvedDate = firstSessionLog.getDate();
                currentSession.setDate(improvedDate);
                IadtController.getDatabase().sessionDao().update(currentSession);

                //Improve new session Log
                String message = "Session " + currentSession.getUid() +" started";
                Friendly newSessionLog = IadtController.getDatabase().friendlyDao().getNewSessionLog(message);
                if (newSessionLog != null
                        && improvedDate < newSessionLog.getDate()){
                    newSessionLog.setDate(improvedDate);
                    IadtController.getDatabase().friendlyDao().update(newSessionLog);
                }

                sessionStartTimeImproved = true;
                Log.v(Iadt.TAG, "Improved session start time! "
                        + Humanizer.getElapsedTime(improvedDate, detectionDate));
            }
            else{
                Log.w(Iadt.TAG, "Unable to improve session start time");
            }
        }
    }
}

