package es.rafaco.inappdevtools.library;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;

//#ifdef MODERN
//@import androidx.annotation.NonNull; 
//#else
import android.support.annotation.NonNull;
//#endif

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.detectors.GestureEventDetector;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.integrations.CustomChuckInterceptor;
import es.rafaco.inappdevtools.library.logic.initialization.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.initialization.FirstStartUtil;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
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
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenHelper;
import es.rafaco.inappdevtools.library.logic.integrations.CustomToast;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class DevTools {

    public static final String TAG = "InAppDevTools";

    private static Context appContext;
    private static DevToolsConfig config;
    private static EventManager eventManager;
    private static SourcesManager sourcesManager;

    public static int readerCounter = 0;
    private static Runnable onForceCloseRunnable;
    private static boolean isPendingForegroundInit;


    //region [ PUBLIC INITIALIZATION ]

    public static void install(Context context) {
        install(context, DevToolsConfig.newBuilder().build());
    }

    public static void install(Context context, DevToolsConfig config) {

        if (config == null || !config.enabled){
            android.util.Log.w(DevTools.TAG, "DevTools initialization skipped");
            return;
        }

        if (DevTools.config != null){
            android.util.Log.w(DevTools.TAG, "DevTools already initialize");
            return;
        }

        DevTools.config = config;
        appContext = context.getApplicationContext();
        FriendlyLog.log(new Date().getTime(), "D", "DevTools", "Init", "DevTools started");

        initBackground();

        if (!AppUtils.isForegroundImportance(context)){
            isPendingForegroundInit = true;
        }else{
            initForeground(context);
        }
    }

    private static void initBackground() {
        android.util.Log.d(DevTools.TAG, "Initializing background services...");
        eventManager = new EventManager(appContext);

        //Lazy initialized
        //sourcesManager = new SourcesManager(appContext);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                getDatabase().printOverview();
            }
        });
        android.util.Log.i(DevTools.TAG, "DevTools background initialized");
    }

    public static void initForegroundIfPending(){
        if (isPendingForegroundInit){
            initForeground(DevTools.getAppContext());
        }
    }
    private static void initForeground(Context context){
        android.util.Log.d(DevTools.TAG, "Initializing foreground services...");

        if (PendingCrashUtil.isPending()){
            Intent intent = OverlayUIService.buildScreenIntentAction(CrashDetailScreen.class, null);
            DevTools.getAppContext().startService(intent);
            PendingCrashUtil.clearPending();
        }
        else if (FirstStartUtil.isFirstStart()){
            Intent intent = new Intent(getAppContext(), WelcomeDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getAppContext().startActivity(intent);
            FirstStartUtil.saveFirstStart();
        }
        else{
            if (config.overlayUiEnabled)
            startOverlayService(context);
        }

        if (config.notificationUiEnabled)
            startForegroundService(context);
    }

    //endregion

    //region [ SERVICES INITIALIZATION ]

    private static void startForegroundService(Context context) {
        Intent intent = new Intent(context, NotificationUIService.class);
        intent.setAction(NotificationUIService.ACTION_START_FOREGROUND_SERVICE);
        context.startService(intent);
    }

    private static void startOverlayService(Context context) {
        context.startService(new Intent(context, OverlayUIService.class));
    }

    //endregion

    //region [ PUBLIC ACCESSORS ]

    public static Context getAppContext() {
        return appContext;
    }

    public static DevToolsConfig getConfig() {
        return config;
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static EventDetector getEventDetector(Class<? extends EventDetector> className) {
        return eventManager.getEventDetectorsManager().get(className);
    }

    public static SourcesManager getSourcesManager() {
        //TODO: Delayed initialisation (not working if need permission)
        if (sourcesManager == null){
            sourcesManager = new SourcesManager(getAppContext());
        }
        return sourcesManager;
    }

    public static DevToolsDatabase getDatabase() {
        return DevToolsDatabase.getInstance();
    }

    //TODO:
    public static GestureDetector getGestureDetector() {
        GestureEventDetector watcher = (GestureEventDetector) getEventDetector(GestureEventDetector.class);

        if (watcher==null) return null;
        return watcher.getDetector();
    }

    @NonNull
    public static OkHttpClient getOkHttpClient() {
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

    //region [ PUBLIC ACTIONS ]

    public static void showMessage(int stringId) {
        showMessage(getAppContext().getResources().getString(stringId));
    }

    public static void showMessage(final String text) {
        CustomToast.show(getAppContext(), text, CustomToast.TYPE_INFO);
        FriendlyLog.log("I", "Message", "Info", text);
    }

    private static void showWarning(final String text) {
        CustomToast.show(getAppContext(), text, CustomToast.TYPE_WARNING);
        FriendlyLog.log("W", "Message", "Warning", text);
    }

    private static void showError(final String text) {
        CustomToast.show(getAppContext(), text, CustomToast.TYPE_ERROR);
        FriendlyLog.log("E", "Message", "Error", text);
    }



    public static void takeScreenshot() {
        Screen screen = new ScreenHelper().takeAndSaveScreen();

        FriendlyLog.log("I", "DevTools", "Screenshot","Screenshot taken");

        if(config.overlayUiEnabled && OverlayUIService.isInitialize()){
            Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.ICON, null);
            getAppContext().startService(intent);
        }
        FileProviderUtils.openFileExternally(getAppContext(), screen.getPath());
    }

    public static void sendReport(final ReportHelper.ReportType type, final Object param) {

        switch (type){
            case CRASH:
                ThreadUtils.runOnBackThread(new Runnable() {
                    @Override
                    public void run() {
                        Crash crash;
                        if (param == null) {
                            crash = getDatabase().crashDao().getLast();
                        } else {
                            crash = getDatabase().crashDao().findById((long) param);
                        }
                        if (crash == null) {
                            showError("Unable to found it");
                            return;
                        }
                        new ReportHelper().start(ReportHelper.ReportType.CRASH, crash);
                    }
                });
                break;

            case SESSION:
                ThreadUtils.runOnBackThread(new Runnable() {
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

    public static void startReportDialog() {
        Intent intent = new Intent(getAppContext(), ReportDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getAppContext().startActivity(intent);
    }

    public static void cleanSession() {
        LogHelper.clearLogcatBuffer();
    }

    public static void openTools(boolean atHome) {

        if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            PermissionActivity.request(PermissionActivity.IntentAction.OVERLAY,
                    new Runnable() {
                        @Override
                        public void run() {
                            openTools(false);
                        }
                    }, null);
            return;
        }

        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.SHOW, null);
        getAppContext().startService(intent);
    }

    //endregion

    public static void breakpoint(Object caller){
        //String objectToString = ToStringBuilder.reflectionToString(caller, ToStringStyle.MULTI_LINE_STYLE);
        //String result2 = new GsonBuilder().setPrettyPrinting().create().toJson(caller);
        String message = "Breakpoint from " + caller.getClass().getSimpleName(); // + ": " + objectToString;
        CustomToast.show(getAppContext(), message, CustomToast.TYPE_INFO);
        FriendlyLog.log("D", "Debug", "Breakpoint", message);
    }


    //region [ RUNNABLES ]

    private static List<RunnableConfig> customRunnables;

    public static void addCustomRunnable(RunnableConfig config){
        if (customRunnables == null)
            customRunnables = new ArrayList<>();
        customRunnables.add(config);
    }

    public static void removeCustomRunnable(RunnableConfig target){
        if (customRunnables !=null && customRunnables.contains(target))
            customRunnables.remove(target);
    }

    public static List<RunnableConfig> getCustomRunnables(){
        if (customRunnables == null)
            customRunnables = new ArrayList<>();
        return customRunnables;
    }

    //endregion

    //region [ RESTART AND FORCE CLOSE ]

    public static void restartApp(boolean isCrash){
        //FriendlyLog.log( "I", "Run", "Restart", "Restart");
        AppUtils.programRestart(getAppContext(), isCrash);

        forceCloseApp(isCrash);
    }

    public static void forceCloseApp(boolean isCrash){
        FriendlyLog.log( "I", "Run", "ForceClose", "Force Close");

        if (!isCrash)
            beforeClose(); //on crash is performed by CrashHandler

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                AppUtils.exit();
            }
        }, 100);
    }

    public static void beforeClose(){

        if(onForceCloseRunnable != null)
            onForceCloseRunnable.run();

        android.util.Log.w(DevTools.TAG, "Stopping watchers");
        eventManager.destroy();

        /*<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
        ActivityManager am = (ActivityManager)getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getAppContext().getPackageName());*/

        android.util.Log.w(DevTools.TAG, "Stopping Foreground");
        NotificationUIService.stop();

        android.util.Log.w(DevTools.TAG, "Stopping Overlay");
        OverlayUIService.stop();
    }

    public static void addOnForceCloseRunnnable(Runnable onForceClose){
        onForceCloseRunnable = onForceClose;
    }

    public static Runnable getOnForceCloseRunnnable(){
        return onForceCloseRunnable;
    }

    public static boolean isDebug() {
        //TODO: sinc with config
        return false;
    }

    public static class Log {

        public static void v(String msg) {
            DevTools.showMessage(msg);
        }
    }
}
