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
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.TooManyListenersException;

import es.rafaco.inappdevtools.library.logic.builds.BuildManager;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.dialogs.DialogManager;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.crash.ForcedRuntimeException;
import es.rafaco.inappdevtools.library.logic.external.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.log.reader.LogcatReaderService;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationManager;
import es.rafaco.inappdevtools.library.logic.navigation.OverlayHelper;
import es.rafaco.inappdevtools.library.logic.reports.ReportType;
import es.rafaco.inappdevtools.library.logic.session.ActivityTracker;
import es.rafaco.inappdevtools.library.logic.session.FragmentTracker;
import es.rafaco.inappdevtools.library.logic.session.SessionManager;
import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableManager;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.files.utils.ScreenshotUtils;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PrivacyConsentPrefs;
import es.rafaco.inappdevtools.library.view.dialogs.WelcomeDialogHelper;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.dialogs.CleanAllDialog;
import es.rafaco.inappdevtools.library.view.dialogs.DisableDialog;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.logic.reports.ReportSender;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.NewReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.view.ZoomScreen;

/**
 * This class is the main internal interface of InAppDevTools.
 * Host apps should try to avoid using this directly, Iadt is our public interface and their method
 * are always safe to call even with the library disabled or using our noop version.
 */
public final class IadtController {

    private static IadtController INSTANCE;

    private Context context;
    private ConfigManager configManager;
    private SessionManager sessionManager;
    private BuildManager buildManager;
    private EventManager eventManager;
    private SourcesManager sourcesManager;
    private RunnableManager runnableManager;
    private DialogManager dialogManager;
    private NavigationManager navigationManager;
    private OverlayHelper overlayHelper;
    public boolean isPendingInitFull;
    private ActivityTracker activityTracker;
    private FragmentTracker fragmentTracker;

    protected IadtController(Context context) {
        if (INSTANCE != null) {
            throw new RuntimeException("IadtController is already initialized");
        }
        INSTANCE = this;
        init(context);
    }

    public static IadtController get() {
        return INSTANCE;
    }


    //region [ TWO-STEP INITIALIZATION ]

    private void init(Context context) {
        this.context = context.getApplicationContext();

        IadtDatabase.checkVersion();
        boolean isEssentialUp = initEssential();
        if (isEssentialUp)
            isPendingInitFull = true;

        new WelcomeDialogHelper().showIfNeededThen(new Runnable() {
            @Override
            public void run() {
                initFullIfPending();
            }
        });
    }

    private boolean initEssential() {
        configManager = new ConfigManager(context);

        if (!isEnabled()){
            Log.w(Iadt.TAG, "Iadt DISABLED by configuration");
            return false;
        }
        if (isDebug()) ThreadUtils.printOverview("IadtController init");
        if (isDebug()) Log.d(Iadt.TAG, "IadtController init essential");

        activityTracker = new ActivityTracker(context);
        fragmentTracker = new FragmentTracker(context, activityTracker);
        buildManager = new BuildManager(context);
        sessionManager = new SessionManager(context);
        eventManager = new EventManager(context);
        runnableManager = new RunnableManager((context));
        dialogManager = new DialogManager((context));

        if (isDebug()){
            ThreadUtils.runOnBack("Iadt-InitBack", new Runnable() {
                @Override
                public void run() {
                    IadtDatabase.get().printOverview();
                }
            });
        }
        
        return true;
    }

    public void initFullIfPending(){
        if (isDebug()) Log.d(Iadt.TAG, "initFullIfPending");
        if (isPendingInitFull){
            if (shouldDelayInitFull()){
                if (isDebug()) Log.d(Iadt.TAG, "shouldDelayInitFull");
                return;
            }
            else{
                initFull();
            }
        }
        else if (getConfig().getBoolean(BuildConfigField.OVERLAY_ENABLED)
                && PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)
                && !OverlayService.isRunning()){
            if (isDebug()) Log.d(Iadt.TAG, "Restarting OverlayHelper");
            overlayHelper = new OverlayHelper(getContext());
        }
        else {
            if (isDebug()) Log.d(Iadt.TAG, "initFullIfPending skipped");
        }
    }

    private boolean shouldDelayInitFull() {
        return !AppUtils.isForegroundImportance(context) ||
                !PrivacyConsentPrefs.isAccepted() ||
                !PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY);
    }

    private void initFull(){
        if (isDebug()) Log.d(Iadt.TAG, "IadtController init full");
        initDelayedBackground();
        initForeground();
        isPendingInitFull = false;
    }

    public void initDelayedBackground() {
        if (isDebug())
            Log.d(Iadt.TAG, "IadtController initDelayedBackground");

        sourcesManager = new SourcesManager(getContext());
        sessionManager.storeDocuments();

        Intent intent = LogcatReaderService.getStartIntent(getContext(), "Started from IadtController");
        LogcatReaderService.enqueueWork(getContext(), intent);

        PandoraBridge.init();
    }

    private void initForeground(){
        if (isDebug())
            Log.d(Iadt.TAG, "IadtController initForeground");

        if (getConfig().getBoolean(BuildConfigField.OVERLAY_ENABLED)){
            navigationManager = new NavigationManager();
            overlayHelper = new OverlayHelper(context);
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

    public ActivityTracker getActivityTracker() {
        return activityTracker;
    }

    public FragmentTracker getFragmentTracker() {
        return fragmentTracker;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public BuildManager getBuildManager() {
        return buildManager;
    }

    public RunnableManager getRunnableManager() {
        return runnableManager;
    }

    public DialogManager getDialogManager() {
        return dialogManager;
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

    public static boolean isEnabled() {
        if (INSTANCE==null){
            return false;
        }
        boolean isEnabledForSdk = Build.VERSION.SDK_INT >= IadtLauncher.MIN_SDK_INT;
        if (!isEnabledForSdk)
            return false;
        return INSTANCE.getConfig().getBoolean(BuildConfigField.ENABLED);
    }

    public boolean isDebug() {
        return getConfig().getBoolean(BuildConfigField.DEBUG);
    }

    //endregion

    //region [ METHODS FOR REPORTING ]

    public void startReportWizard() {
        OverlayService.performNavigation(NewReportScreen.class, null);
    }

    public void startReportWizard(ReportType type, final Object param) {
        Report report = new Report();
        report.setReportType(type);
        if(type.equals(ReportType.SESSION)){
            report.setSessionId((Long) param);
        }
        else if(type.equals(ReportType.CRASH)){
            report.setCrashId((Long) param);
        }
        String params = NewReportScreen.buildParams(report);
        OverlayService.performNavigation(NewReportScreen.class, params);
    }

    public void sendReport(final Report report) {
        ThreadUtils.runOnBack("Iadt-SendReport",
                new Runnable() {
                    @Override
                    public void run() {
                        new ReportSender().send(report);
                    }
                });
    }

    public void newSession() {
        Iadt.buildMessage("Starting a new Session (restarting)").isInfo().fire();
        IadtController.get().restartApp(false);
    }


    public void takeScreenshot() {
        takeScreenshot(null);
    }

    public void takeScreenshot(final Runnable callback) {
        if (!isEnabled()) return;
        boolean isOverlayRunning = getConfig().getBoolean(BuildConfigField.OVERLAY_ENABLED)
                && OverlayService.isInitialize();

        if (!isOverlayRunning){
            Screenshot screenshot = ScreenshotUtils.takeAndSave(false);
            FriendlyLog.logScreenshot(screenshot.getUid());
            if (callback!=null) callback.run();
            return;
        }

        getOverlayHelper().hideAll();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Screenshot screenshot = ScreenshotUtils.takeAndSave(false);
                FriendlyLog.logScreenshot(screenshot.getUid());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getOverlayHelper().restoreAll();
                        if (callback!=null) callback.run();
                    }
                }, 200);
            }
        }, 200);
    }

    public void takeLibraryScreenshot(){
        //getOverlayHelper().showMain();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScreenshotUtils.takeAndSaveOverlay();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Screenshot screenshot = IadtDatabase.get().screenshotDao().getLast();
                        FriendlyLog.logScreenshot(screenshot.getUid());
                        OverlayService.performNavigation(ZoomScreen.class, screenshot.getUid()+"");
                    }
                }, 200);
            }
        }, 200);
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

        if (isDebug()) Log.v(Iadt.TAG, "Stopping OverlayUI Service");
        OverlayService.stop();

        if (isDebug()) Log.v(Iadt.TAG, "Stopping LogcatReaderService");
        Intent intent = LogcatReaderService.getStopIntent(getContext());
        LogcatReaderService.enqueueWork(getContext(), intent);

        if (isDebug()) Log.v(Iadt.TAG, "Stopping watchers");
        sessionManager.destroy();
        eventManager.destroy();
    }

    public void disable() {
        getDialogManager().load(new DisableDialog());
    }

    public void cleanAll() {
        getDialogManager().load(new CleanAllDialog()
                .setCancelable(true));
    }

    //endregion

    //region [ CRASH ]

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

    public boolean handleInternalException(String message, final Exception e) {
        FriendlyLog.logException(message, e);

        Iadt.getConfig().setBoolean(BuildConfigField.ENABLED, false);
        Log.w(Iadt.TAG, "LIBRARY DISABLED");

        if (!isDebug()){
            //TODO: Replace this line
            //TODO: FriendlyLog dont print at logcat when !isDebug
            Log.e(Iadt.TAG, "INTERNAL EXCEPTION: " + message + " -> " + e.getMessage() + "\n"
                    + Log.getStackTraceString(e));

            return false;
        }
        //TODO: prevent showing them on debug mode
        return false;
    }

    //endregion
}

