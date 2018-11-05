package es.rafaco.devtools;

import android.app.ActivityManager;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.rafaco.devtools.logic.activityLog.ActivityLogManager;
import es.rafaco.devtools.logic.activityLog.CustomChuckInterceptor;
import es.rafaco.devtools.logic.activityLog.ProcessLifecycleCallbacks;
import es.rafaco.devtools.logic.anr.AnrLogger;
import es.rafaco.devtools.logic.crash.CrashHandler;
import es.rafaco.devtools.logic.crash.PendingCrashUtil;
import es.rafaco.devtools.logic.shake.ShakeDetector;
import es.rafaco.devtools.logic.utils.AppUtils;
import es.rafaco.devtools.logic.utils.FirstStartUtil;
import es.rafaco.devtools.logic.utils.FriendlyLog;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.storage.db.DevToolsDatabase;
import es.rafaco.devtools.storage.db.entities.Crash;
import es.rafaco.devtools.storage.db.entities.Screen;
import es.rafaco.devtools.storage.files.FileProviderUtils;
import es.rafaco.devtools.tools.CommandsTool;
import es.rafaco.devtools.tools.ErrorsTool;
import es.rafaco.devtools.tools.FriendlyLogTool;
import es.rafaco.devtools.tools.HomeTool;
import es.rafaco.devtools.tools.InfoTool;
import es.rafaco.devtools.tools.LogTool;
import es.rafaco.devtools.tools.NetworkTool;
import es.rafaco.devtools.tools.ReportTool;
import es.rafaco.devtools.tools.ScreenTool;
import es.rafaco.devtools.tools.StorageTool;
import es.rafaco.devtools.tools.ToolManager;
import es.rafaco.devtools.view.activities.PermissionActivity;
import es.rafaco.devtools.view.dialogs.CrashDialogActivity;
import es.rafaco.devtools.view.dialogs.ReportDialogActivity;
import es.rafaco.devtools.view.dialogs.WelcomeDialogActivity;
import es.rafaco.devtools.view.notifications.NotificationUIService;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.screens.home.RunnableConfig;
import es.rafaco.devtools.view.overlay.screens.log.LogHelper;
import es.rafaco.devtools.view.overlay.screens.report.ReportHelper;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreenHelper;
import es.rafaco.devtools.view.utils.CustomToast;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class DevTools {

    public static final String TAG = "DevTools";

    private static Context appContext;
    private static DevToolsConfig config;
    private static ToolManager toolManager;
    private static ActivityLogManager activityLogManager;
    private static AnrLogger anrLogger;
    public static int readerCounter = 0;
    private static ShakeDetector shakeDetector;
    private static Runnable onForceCloseRunnable;

    //region [ PUBLIC INITIALIZATION ]

    public static void install(Context context) {
        install(context, DevToolsConfig.newBuilder().build());
    }

    public static void install(Context context, DevToolsConfig config) {

        if (config == null || !config.enabled){
            Log.w(DevTools.TAG, "DevTools initialization skipped.");
            return;
        }

        if (DevTools.config != null){
            Log.w(DevTools.TAG, "DevTools already initialize.");
            return;
        }

        Log.d(DevTools.TAG, "Initializing DevTools...");
        appContext = context.getApplicationContext();
        DevTools.config = config;

        toolManager = new ToolManager(appContext);
        //TODO: remove this!
        toolManager.registerTool(HomeTool.class);
        toolManager.registerTool(InfoTool.class);
        toolManager.registerTool(FriendlyLogTool.class);
        toolManager.registerTool(LogTool.class);
        toolManager.registerTool(CommandsTool.class);
        toolManager.registerTool(NetworkTool.class);
        toolManager.registerTool(StorageTool.class);
        toolManager.registerTool(ErrorsTool.class);
        toolManager.registerTool(ScreenTool.class);
        toolManager.registerTool(ReportTool.class);

        if (config.crashHandlerEnabled) startCrashHandler(context);
        if (config.anrLoggerEnabled) startAnrLogger();
        if (config.strictModeEnabled) startStrictMode();
        if (config.activityLoggerEnabled) startActivityLogger(context);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ProcessLifecycleCallbacks());

        //if (config.invocationByShake)
        startShakeDetector(context);
        if (config.notificationUiEnabled) startForegroundService(context);
        if (config.overlayUiEnabled) startUiService(context);

        ThreadUtils.runOnBackThread(() -> getDatabase().printOverview());
        Log.i(DevTools.TAG, "DevTools initialized");


        if (PendingCrashUtil.isPending()){
            Intent intent = new Intent(getAppContext(), CrashDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getAppContext().startActivity(intent);
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

    private static void startCrashHandler(Context context) {
        Thread.UncaughtExceptionHandler currentHanler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHanler != null || !currentHanler.getClass().isInstance(CrashHandler.class)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(context.getApplicationContext(), context, currentHanler));
            Log.d(DevTools.TAG, "Exception handler added");
        }else{
            Log.d(DevTools.TAG, "Exception handler already attach on thread");
        }
    }

    private static void startStrictMode() {
        if (config.strictModeEnabled && BuildConfig.DEBUG) {
            AppUtils.startStrictMode();
        }
    }

    private static void startShakeDetector(Context context) {
        shakeDetector = new ShakeDetector(getAppContext(),
                () -> openTools(false));
    }

    private static void startActivityLogger(Context context) {
        activityLogManager = new ActivityLogManager(context);
    }

    private static void startAnrLogger(){
        anrLogger = new AnrLogger();
    }

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
    public static ActivityLogManager getActivityLogManager() {
        return activityLogManager;
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

    public static ShakeDetector getShakeDetector() {
        return shakeDetector;
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

    public static void restartApp(){
        //FriendlyLog.log( "I", "Run", "Restart", "Restart");
        AppUtils.programRestart(getAppContext());
        forceCloseApp();
    }

    public static void forceCloseApp(){
        FriendlyLog.log( "I", "Run", "ForceClose", "Force Close");
        if(onForceCloseRunnable != null)
            onForceCloseRunnable.run();

        forceClose();
        ThreadUtils.runOnUiThread(() -> AppUtils.exit(), 1000);
    }

    public static void forceClose(){
        /*<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
        ActivityManager am = (ActivityManager)getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(getAppContext().getPackageName());*/

        Intent closeForegroundIntent = new Intent(getAppContext(), NotificationUIService.class);
        closeForegroundIntent.setAction(NotificationUIService.ACTION_STOP_FOREGROUND_SERVICE);
        getAppContext().startService(closeForegroundIntent);

        Intent closeOverlayIntent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.FORCE_CLOSE, null);
        getAppContext().startService(closeOverlayIntent);
    }

    public static void addOnForceCloseRunnnable(Runnable onForceClose){
        onForceCloseRunnable = onForceClose;
    }

    public static Runnable getOnForceCloseRunnnable(){
        return onForceCloseRunnable;
    }
    //endregion
}
