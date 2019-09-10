package es.rafaco.inappdevtools.library;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.Nullable;
//#else
import android.support.annotation.Nullable;
//#endif

import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.log.reader.LogcatReaderService;
import es.rafaco.inappdevtools.library.storage.prefs.utils.FirstStartUtil;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.integrations.CustomChuckInterceptor;
import es.rafaco.inappdevtools.library.logic.runnables.RunnablesManager;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Screen;
import es.rafaco.inappdevtools.library.storage.files.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.activities.ReportDialogActivity;
import es.rafaco.inappdevtools.library.view.activities.WelcomeDialogActivity;
import es.rafaco.inappdevtools.library.view.notifications.NotificationUIService;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatHelper;
import es.rafaco.inappdevtools.library.logic.reports.ReportHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenHelper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public final class IadtController extends ContentProvider {

    private static IadtController INSTANCE;
    private Context appContext;
    private ConfigManager configManager;
    private EventManager eventManager;
    private SourcesManager sourcesManager;
    private RunnablesManager runnablesManager;
    private boolean isPendingForegroundInit;

    public IadtController() {
        if (INSTANCE != null) {
            throw new RuntimeException();
        }
    }

    public static IadtController get() {
        if (INSTANCE == null) {
            IadtController iadtController = new IadtController();
            iadtController.onCreate();
        }
        return INSTANCE;
    }

    @Override
    public boolean onCreate() {
        INSTANCE = this;
        init(getContext());
        return false;
    }


    //region [ INITIALIZATION ]

    private void init(Context context) {
        appContext = context.getApplicationContext();

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
        configManager = new ConfigManager(appContext);

        if (!isEnabled()){
            Log.w(Iadt.TAG, "Iadt DISABLED by configuration");
            return false;
        }else{
            Log.d(Iadt.TAG, "Iadt ENABLED");
        }

        if (isDebug())
            Log.d(Iadt.TAG, "Initializing background services...");

        eventManager = new EventManager(appContext);
        runnablesManager = new RunnablesManager((appContext));

        if (isDebug()){
            ThreadUtils.runOnBack(new Runnable() {
                @Override
                public void run() {
                    DevToolsDatabase.getInstance().printOverview();
                }
            });
        }

        Intent intent = LogcatReaderService.getStartIntent(getContext(), "Started from IadtController");
        LogcatReaderService.enqueueWork(getContext(), intent);
        
        return true;
    }

    public void initForegroundIfPending(){
        if (isPendingForegroundInit){
            initForeground();
        }
    }

    private void initForeground(){
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

        if (getConfig().getBoolean(Config.OVERLAY_ENABLED)){
            if (PendingCrashUtil.isPending()){
                // IsPendingCrash, we open crash details at overlay
                Intent intent = OverlayUIService.buildScreenIntentAction(CrashDetailScreen.class, null);
                getAppContext().startService(intent);
                PendingCrashUtil.clearPending();
            }
            else {
                //Start OverlayUIService
                getAppContext().startService(new Intent(getAppContext(), OverlayUIService.class));
            }
        }

        if (getConfig().getBoolean(Config.INVOCATION_BY_NOTIFICATION)){
            // Start foreground notification service
            Intent intent = new Intent(getAppContext(), NotificationUIService.class);
            intent.setAction(NotificationUIService.ACTION_START_FOREGROUND_SERVICE);
            getAppContext().startService(intent);
        }
    }


    //endregion

    //region [ GETTERS ]

    public Context getAppContext() {
        return appContext;
    }

    public ConfigManager getConfig() {
        return configManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public RunnablesManager getRunnableManager() {
        return runnablesManager;
    }

    public SourcesManager getSourcesManager() {
        //TODO: Lazy initialisation is not working when permission needed
        if (sourcesManager == null){
            sourcesManager = new SourcesManager(getAppContext());
        }
        return sourcesManager;
    }

    public static DevToolsDatabase getDatabase() {
        return DevToolsDatabase.getInstance();
    }

    public boolean isEnabled() {
        return getConfig().getBoolean(Config.ENABLED);
    }

    public boolean isDebug() {
        return getConfig().getBoolean(Config.DEBUG);
    }


    public OkHttpClient getOkHttpClient() {
        if (!isEnabled()){
            OkHttpClient client = new OkHttpClient.Builder().build();
            return client;
        }

        //TODO: relocate an create a unique interceptor, and a method to return it
        CustomChuckInterceptor httpGrabberInterceptor = new CustomChuckInterceptor(getAppContext());
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

    private boolean checksBeforeShowOverlay() {
        if (!isEnabled()) return true;
        if (!AppUtils.isForegroundImportance(getAppContext())) return true;

        if (isPendingForegroundInit) {
            initForegroundIfPending();
            if (!isPendingForegroundInit)
                return true;
        }
        else if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
                WelcomeDialogActivity.open(WelcomeDialogActivity.IntentAction.OVERLAY,
                        new Runnable() {
                            @Override
                            public void run() {
                                showMain();
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                Iadt.showMessage(R.string.draw_other_app_permission_denied);
                            }
                        });
            }
            return true;
        }
        return false;
    }

    public void showToggle() {
        if (checksBeforeShowOverlay()) return;

        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.SHOW_TOGGLE, null);
        getAppContext().startService(intent);
    }
    public void showMain() {
        if (checksBeforeShowOverlay()) return;

        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.SHOW_MAIN, null);
        getAppContext().startService(intent);
    }

    public void showIcon() {
        if (checksBeforeShowOverlay()) return;

        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.SHOW_ICON, null);
        getAppContext().startService(intent);
    }

    public void hideAll() {
        //if (checksBeforeShowOverlay()) return;

        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.HIDE_ALL, null);
        getAppContext().startService(intent);
    }

    public void restoreAll() {
        //if (checksBeforeShowOverlay()) return;

        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.RESTORE_ALL, null);
        getAppContext().startService(intent);
    }

    public void takeScreenshot() {
        if (!isEnabled()) return;

        Screen screen = new ScreenHelper().takeAndSaveScreen();
        FriendlyLog.log("I", "Iadt", "Screenshot","Screenshot taken");

        if(getConfig().getBoolean(Config.OVERLAY_ENABLED) && OverlayUIService.isInitialize()){
            showIcon();
        }
        FileProviderUtils.openFileExternally(getAppContext(), screen.getPath());
    }

    public void startReportDialog() {
        Intent intent = new Intent(getAppContext(), ReportDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getAppContext().startActivity(intent);
    }

    public void sendReport(ReportHelper.ReportType type, final Object param) {
        switch (type){
            case CRASH:
                ThreadUtils.runOnBack(new Runnable() {
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
                ThreadUtils.runOnBack(new Runnable() {
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

    public String getCurrentOverlay() {
        return MainOverlayLayerManager.getCurrent();
    }

    //endregion

    //region [ RESTART AND FORCE CLOSE ]

    public void restartApp(boolean isCrash){
        FriendlyLog.log( "I", "Run", "Restart", "Restart programmed");
        AppUtils.programRestart(getAppContext(), isCrash);

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
        ActivityManager am = (ActivityManager)getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getAppContext().getPackageName());*/

        if (isDebug())
            Log.w(Iadt.TAG, "Stopping Notification Service");
        NotificationUIService.stop();

        if (isDebug())
            Log.w(Iadt.TAG, "Stopping OverlayUI Service");
        OverlayUIService.stop();

        if (isDebug())
            Log.w(Iadt.TAG, "Stopping LogcatReaderService");
        Intent intent = LogcatReaderService.getStopIntent(getContext());
        LogcatReaderService.enqueueWork(getContext(), intent);
    }

    //endregion

    //region [ LEGACY METHODS: from extending ContentProvider ]

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    //endregion
}
