package es.rafaco.devtools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.logic.tools.CommandsTool;
import es.rafaco.devtools.logic.tools.ErrorsTool;
import es.rafaco.devtools.logic.tools.HomeTool;
import es.rafaco.devtools.logic.tools.InfoTool;
import es.rafaco.devtools.logic.tools.LogTool;
import es.rafaco.devtools.logic.tools.ReportTool;
import es.rafaco.devtools.logic.tools.ScreenTool;
import es.rafaco.devtools.logic.tools.ToolManager;
import es.rafaco.devtools.utils.FirstStartUtil;
import es.rafaco.devtools.view.activities.PermissionActivity;
import es.rafaco.devtools.logic.activityLog.ActivityLogManager;
import es.rafaco.devtools.logic.anr.AnrLogger;
import es.rafaco.devtools.logic.crash.CrashHandler;
import es.rafaco.devtools.logic.crash.PendingCrashUtil;
import es.rafaco.devtools.utils.FileUtils;
import es.rafaco.devtools.view.dialog.CrashDialogActivity;
import es.rafaco.devtools.view.dialog.ReportDialogActivity;
import es.rafaco.devtools.view.dialog.WelcomeDialogActivity;
import es.rafaco.devtools.view.overlay.screens.log.LogHelper;
import es.rafaco.devtools.view.overlay.screens.report.ReportHelper;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreenHelper;
import es.rafaco.devtools.utils.AppUtils;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.NotificationUIService;
import es.rafaco.devtools.view.OverlayUIService;

public class DevTools {

    public static final String TAG = "DevTools";

    private static Context appContext;
    private static DevToolsConfig config;
    private static ToolManager toolManager;
    private static ActivityLogManager activityLogManager;
    private static AnrLogger anrLogger;
    public static int readerCounter = 0;


    //region [ PUBLIC INITIALIZATION ]

    public static void install(Context context) {
        install(context, DevToolsConfig.newBuilder().build());
    }

    public static void install(Context context, DevToolsConfig config) {
        if (config == null || !config.enabled){
            Log.w(DevTools.TAG, "DevTools initialization skipped.");
            return;
        }

        Log.d(DevTools.TAG, "Initializing DevTools...");
        appContext = context.getApplicationContext();
        DevTools.config = config;

        toolManager = new ToolManager(appContext);

        //TODO: adapt with config Profiles
        toolManager.registerTool(HomeTool.class);
        toolManager.registerTool(InfoTool.class);
        toolManager.registerTool(ErrorsTool.class);
        toolManager.registerTool(LogTool.class);
        toolManager.registerTool(CommandsTool.class);
        toolManager.registerTool(ScreenTool.class);
        toolManager.registerTool(ReportTool.class);


        if (config.crashHandlerEnabled) startCrashHandler(context);
        if (config.anrLoggerEnabled) startAnrLogger();
        if (config.strictModeEnabled) startStrictMode();
        if (config.activityLoggerEnabled) startActivityLogger(context);
        if (config.notificationUiEnabled) startForegroundService(context);
        if (config.overlayUiEnabled) startUiService(context);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                getDatabase().printOverview();
            }
        });
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

    //endregion

    //region [ PUBLIC ACTIONS ]

    public static void showMessage(int stringId) {
        showMessage(getAppContext().getResources().getString(stringId));
    }

    private static void showError(final String text) {
        Log.e(DevTools.TAG, "ERROR: " + text);
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), "ERROR: " + text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showMessage(final String text) {
        Log.i(DevTools.TAG, "INFO: " + text);
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), "INFO: " + text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void takeScreenshot() {

        if (!PermissionActivity.check(PermissionActivity.IntentAction.STORAGE)){
            PermissionActivity.request(PermissionActivity.IntentAction.STORAGE,
                    new Runnable(){
                        @Override
                        public void run() {
                            takeScreenshot();
                        }
                    }, null);
            return;
        }

        Screen screen = new ScreenHelper().takeAndSaveScreen();

        if(config.overlayUiEnabled && OverlayUIService.isInitialize()){
            Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.ICON, null);
            getAppContext().startService(intent);
        }
        FileUtils.openFileExternally(getAppContext(), screen.getPath());
    }

    public static void sendReport(final ReportHelper.ReportType type, final Object param) {

        if (!PermissionActivity.check(PermissionActivity.IntentAction.STORAGE)){
            PermissionActivity.request(PermissionActivity.IntentAction.STORAGE,
                    new Runnable(){
                        @Override
                        public void run() {
                            sendReport(type, param);
                        }
                    }, null);
            return;
        }

        switch (type){
            case CRASH:
                ThreadUtils.runOnBackThread(new Runnable() {
                    @Override
                    public void run() {
                        long crashId = (long)param;
                        Crash crash;
                        if (crashId<0){
                            crash = getDatabase().crashDao().getLast();
                        }else{
                            crash = getDatabase().crashDao().findById(crashId);
                        }
                        if (crash == null){
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
        getAppContext().startActivity(intent);
    }

    public static void cleanSession() {
        LogHelper.clearLogcatBuffer();
    }

    public static void openTools(boolean atHome) {

        if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            PermissionActivity.request(PermissionActivity.IntentAction.OVERLAY,
                    new Runnable(){
                        @Override
                        public void run() {
                            openTools(false);
                        }
                    }, null);
            return;
        }

        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.MAIN, null);
        getAppContext().startService(intent);
    }

    //endregion
}
