package es.rafaco.inappdevtools.library;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.watcher.WatcherManager;
import es.rafaco.inappdevtools.library.logic.watcher.activityLog.ActivityLogManager;
import es.rafaco.inappdevtools.library.logic.watcher.activityLog.CustomChuckInterceptor;
import es.rafaco.inappdevtools.library.logic.initialization.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.initialization.FirstStartUtil;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Screen;
import es.rafaco.inappdevtools.library.storage.files.FileProviderUtils;
import es.rafaco.inappdevtools.library.tools.ToolManager;
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

    public static final String TAG = "DevTools";

    private static Context appContext;
    private static DevToolsConfig config;
    private static ToolManager toolManager;
    private static WatcherManager watcherManager;
    private static SourcesManager sourcesManager;

    public static int readerCounter = 0;
    private static Runnable onForceCloseRunnable;


    //region [ PUBLIC INITIALIZATION ]

    public static void install(Context context) {
        install(context, DevToolsConfig.newBuilder().build());
    }

    public static void install(Context context, DevToolsConfig config) {

        if (config == null || !config.enabled){
            Log.w(DevTools.TAG, "DevTools initialization skipped");
            return;
        }

        if (DevTools.config != null){
            Log.w(DevTools.TAG, "DevTools already initialize");
            return;
        }

        Log.d(DevTools.TAG, "Initializing DevTools...");
        DevTools.config = config;

        appContext = context.getApplicationContext();
        toolManager = new ToolManager(appContext);
        watcherManager = new WatcherManager(appContext);
        watcherManager.init(config);

        //sourcesManager = new SourcesManager(appContext);

        if (config.notificationUiEnabled) startForegroundService(context);
        if (config.overlayUiEnabled) startUiService(context);

        ThreadUtils.runOnBackThread(() -> getDatabase().printOverview());
        Log.i(DevTools.TAG, "DevTools initialized");


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
    }

    //endregion

    //region [ PRIVATE INITIALIZATION ]

    private static void startForegroundService(Context context) {
        Intent intent = new Intent(context, NotificationUIService.class);
        intent.setAction(NotificationUIService.ACTION_START_FOREGROUND_SERVICE);
        context.startService(intent);
    }

    private static void startUiService(Context context) {
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
    public static ToolManager getToolManager() {
        return toolManager;
    }
    public static WatcherManager getWatcherManager() {
        return watcherManager;
    }
    public static SourcesManager getSourcesManager() {
        //TODO: Delayed initialisation (not working if need permission)
        if (sourcesManager == null){
            sourcesManager = new SourcesManager(getAppContext());
        }
        return sourcesManager;
    }
    public static ActivityLogManager getActivityLogManager() {
        return watcherManager.getActivityLogManager();
    }

    public static DevToolsDatabase getDatabase() {
        return DevToolsDatabase.getInstance();
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

        if (!PermissionActivity.check(PermissionActivity.IntentAction.STORAGE)){
            PermissionActivity.request(PermissionActivity.IntentAction.STORAGE,
                    () -> takeScreenshot(), null);
            return;
        }

        Screen screen = new ScreenHelper().takeAndSaveScreen();

        FriendlyLog.log("I", "DevTools", "Screenshot","Screenshot taken");

        if(config.overlayUiEnabled && OverlayUIService.isInitialize()){
            Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.ICON, null);
            getAppContext().startService(intent);
        }
        FileProviderUtils.openFileExternally(getAppContext(), screen.getPath());
    }

    public static void sendReport(final ReportHelper.ReportType type, final Object param) {

        if (!PermissionActivity.check(PermissionActivity.IntentAction.STORAGE)){
            PermissionActivity.request(PermissionActivity.IntentAction.STORAGE,
                    () -> sendReport(type, param), null);
            return;
        }

        switch (type){
            case CRASH:
                ThreadUtils.runOnBackThread(() -> {
                    Crash crash;
                    if (param == null){
                        crash = getDatabase().crashDao().getLast();
                    }else{
                        crash = getDatabase().crashDao().findById((long)param);
                    }
                    if (crash == null){
                        showError("Unable to found it");
                        return;
                    }
                    new ReportHelper().start(ReportHelper.ReportType.CRASH, crash);
                });
                break;

            case SESSION:
                ThreadUtils.runOnBackThread(() -> {
                    //ArrayList<Uri> files = (ArrayList<Uri>)params;
                    //TODO: Session report
                    new ReportHelper().start(ReportHelper.ReportType.SESSION, param);
                });
                break;
        }
    }

    public static void startReportDialog() {
        Intent intent = new Intent(getAppContext(), ReportDialogActivity.class);
        getAppContext().startActivity(intent);
    }

    public static void cleanSession() {
        LogHelper.clearLogcatBuffer();
    }

    public static void openTools(boolean atHome) {

        if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            PermissionActivity.request(PermissionActivity.IntentAction.OVERLAY,
                    () -> openTools(false), null);
            return;
        }

        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.MAIN, null);
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

    public static void logCreatedInitProvider(Context context) {
        appContext = context.getApplicationContext();
        if (PendingCrashUtil.isPending())
            FriendlyLog.log(new Date().getTime(), "I", "App", "Restarted", "App restarted after a crash");
        else if (FirstStartUtil.isFirstStart())
            FriendlyLog.log(new Date().getTime(), "I", "App", "FirstStartup", "App started for first time");
        else
            FriendlyLog.log(new Date().getTime(), "I", "App", "Startup", "App started");
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

        ThreadUtils.runOnBackThread(() -> AppUtils.exit(), 100);
    }

    public static void beforeClose(){

        if(onForceCloseRunnable != null)
            onForceCloseRunnable.run();

        Log.w(DevTools.TAG, "Stopping watchers");
        watcherManager.destroy();

        /*<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
        ActivityManager am = (ActivityManager)getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getAppContext().getPackageName());*/

        Log.w(DevTools.TAG, "Stopping Foreground");
        NotificationUIService.close();

        Log.w(DevTools.TAG, "Stopping Overlay");
        OverlayUIService.close();
    }

    public static void addOnForceCloseRunnnable(Runnable onForceClose){
        onForceCloseRunnable = onForceClose;
    }

    public static Runnable getOnForceCloseRunnnable(){
        return onForceCloseRunnable;
    }

    //endregion
}
