package es.rafaco.devtools.view.overlay;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.logic.utils.AppUtils;
import es.rafaco.devtools.logic.utils.FriendlyLog;
import es.rafaco.devtools.view.activities.PermissionActivity;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.AnrDetailScreen;
import es.rafaco.devtools.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.devtools.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.devtools.view.overlay.screens.home.InspectScreen;
import es.rafaco.devtools.view.overlay.screens.home.HomeScreen;
import es.rafaco.devtools.view.overlay.screens.commands.CommandsScreen;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.home.RunScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;
import es.rafaco.devtools.view.overlay.screens.network.detail.NetworkDetailScreen;
import es.rafaco.devtools.view.overlay.screens.network.NetworkScreen;
import es.rafaco.devtools.view.overlay.screens.report.ReportScreen;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;
import es.rafaco.devtools.view.overlay.screens.storage.DatabaseScreen;
import es.rafaco.devtools.view.overlay.screens.storage.FileScreen;
import es.rafaco.devtools.view.overlay.screens.storage.FolderScreen;
import es.rafaco.devtools.view.overlay.screens.storage.SharedPrefsScreen;
import es.rafaco.devtools.view.overlay.screens.storage.StorageScreen;
import es.rafaco.devtools.view.overlay.screens.storage.TableScreen;


public class OverlayUIService extends Service {

    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    public static final String EXTRA_INTENT_PROPERTY = "EXTRA_INTENT_PROPERTY";
    private static final String EXTRA_INTENT_PARAM = "EXTRA_INTENT_PARAM";
    private static Boolean initialised = false;
    private String lastRequestParam;

    public enum IntentAction { PERMISSION_GRANTED, RESTART_APP, CLOSE_APP, EXCEPTION, REPORT, SCREEN, TOOL, MAIN, ICON,
        NAVIGATE_TO, NAVIGATE_BACK, HIDE, NAVIGATE_HOME
    }

    private OverlayLayersManager overlayLayersManager;
    private MainOverlayLayerManager mainOverlayLayerManager;

    public OverlayUIService() {
    }

    public static void performNavigation(Class<? extends OverlayScreen> target) {
        performNavigationStep(new NavigationStep(target, null));
    }

    public static void performNavigationStep(NavigationStep step) {
        if (step == null){
            return;
        }
        Intent intent = buildScreenIntentAction(step.getClassName(), step.getParam());
        DevTools.getAppContext().startService(intent);
    }

    public static Intent buildScreenIntentAction(Class<? extends OverlayScreen> screenClass, String param) {
        Intent intent = new Intent(DevTools.getAppContext(), OverlayUIService.class);
        intent.putExtra(OverlayUIService.EXTRA_INTENT_ACTION, IntentAction.NAVIGATE_TO);
        if (screenClass!=null){
            intent.putExtra(OverlayUIService.EXTRA_INTENT_PROPERTY, screenClass.getSimpleName());
        }
        if (!TextUtils.isEmpty(param)){
            intent.putExtra(OverlayUIService.EXTRA_INTENT_PARAM, param);
        }
        return intent;
    }
    
    public static Intent buildIntentAction(OverlayUIService.IntentAction action, String property) {
        Intent intent = new Intent(DevTools.getAppContext(), OverlayUIService.class);
        intent.putExtra(OverlayUIService.EXTRA_INTENT_ACTION, action);
        if (!TextUtils.isEmpty(property)){
            intent.putExtra(OverlayUIService.EXTRA_INTENT_PROPERTY, property);
        }
        return intent;
    }

    public static void runAction(OverlayUIService.IntentAction action, String property) {
        Intent intent = buildIntentAction(action, property);
        DevTools.getAppContext().startService(intent);
    }
    

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentAction action = (IntentAction)intent.getSerializableExtra(EXTRA_INTENT_ACTION);
        String property = intent.getStringExtra(EXTRA_INTENT_PROPERTY);
        //TODO: cleanUp
        lastRequestParam = intent.getStringExtra(EXTRA_INTENT_PARAM);
        if (action != null){
            processIntentAction(action, property);
        }else{
            Log.v(DevTools.TAG, "OverlayUIService - onStartCommand without action");
        }
        return DevTools.getConfig().overlayUiServiceSticky ? START_STICKY : START_NOT_STICKY;
    }

    private void processIntentAction(IntentAction action, String property) {
        Log.v(DevTools.TAG, "OverlayUIService - onStartCommand with action: " + action.toString());
        
        if (!isInitialised(action, property))
            return;

        //Restore previous request on permission granted
        if (action.equals(IntentAction.PERMISSION_GRANTED)) {
            //TODO: remove
        }

        if (action.equals(IntentAction.TOOL)){
            navigateTo(property.replace(" Tool", ""));
        }
        else if (action.equals(IntentAction.MAIN)) {
            if (mainOverlayLayerManager.getCurrentScreen() == null){
                navigateHome();
            }
            overlayLayersManager.setMainVisibility(true);
        }
        else if (action.equals(IntentAction.REPORT)){
            navigateTo(ReportScreen.class.getSimpleName());
        }
        else if (action.equals(IntentAction.SCREEN)){
            navigateTo(ReportScreen.class.getSimpleName());
        }

        else if (action.equals(IntentAction.NAVIGATE_TO)){
            String cleanName = property.replace(" Tool", "");
            navigateTo(cleanName, lastRequestParam);
        }
        else if (action.equals(IntentAction.NAVIGATE_BACK)){
            navigateBack();
        }
        else if (action.equals(IntentAction.NAVIGATE_HOME)){
            navigateHome();
        }
        else if (action.equals(IntentAction.HIDE) || action.equals(IntentAction.ICON)){
            hide();
        }
        else if (action.equals(IntentAction.CLOSE_APP)){
            killProcess();
        }
        else if (action.equals(IntentAction.RESTART_APP)){
            AppUtils.programRestart(getApplicationContext());
            killProcess();
        }
    }

    private void hide() {
        overlayLayersManager.setMainVisibility(false);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialised = false;
    }

    public static boolean isInitialize() {
        return initialised;
    }

    private boolean isInitialised(final IntentAction action, final String property) {
        if (initialised)
            return true;

        if (!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            //TODO: remove
            throw new UnsupportedOperationException("Alguien ha llamado a OverlayUIService desde fuera!!!");
        }
        else {
            init();
            initialised = true;
            Log.w(DevTools.TAG, "OverlayUIService - initialised");
            return true;
        }
    }

    private void init() {
        overlayLayersManager = new OverlayLayersManager(this);
        mainOverlayLayerManager = new MainOverlayLayerManager(this, overlayLayersManager.getMainLayer());

        //TODO: delegate to ToolManager or delete
        mainOverlayLayerManager.registerScreen(HomeScreen.class);
        mainOverlayLayerManager.registerScreen(InfoScreen.class);
        mainOverlayLayerManager.registerScreen(NetworkScreen.class);
        mainOverlayLayerManager.registerScreen(ErrorsScreen.class);
        mainOverlayLayerManager.registerScreen(FriendlyLogScreen.class);
        mainOverlayLayerManager.registerScreen(LogScreen.class);
        mainOverlayLayerManager.registerScreen(CommandsScreen.class);
        mainOverlayLayerManager.registerScreen(ScreensScreen.class);
        mainOverlayLayerManager.registerScreen(ReportScreen.class);
        mainOverlayLayerManager.registerScreen(CrashDetailScreen.class);
        mainOverlayLayerManager.registerScreen(AnrDetailScreen.class);
        mainOverlayLayerManager.registerScreen(NetworkDetailScreen.class);

        mainOverlayLayerManager.registerScreen(StorageScreen.class);
        mainOverlayLayerManager.registerScreen(DatabaseScreen.class);
        mainOverlayLayerManager.registerScreen(TableScreen.class);
        mainOverlayLayerManager.registerScreen(SharedPrefsScreen.class);
        mainOverlayLayerManager.registerScreen(FolderScreen.class);
        mainOverlayLayerManager.registerScreen(FileScreen.class);
        mainOverlayLayerManager.registerScreen(RunScreen.class);
        mainOverlayLayerManager.registerScreen(InspectScreen.class);

        //navigateTo("Home");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (overlayLayersManager != null){
            overlayLayersManager.onConfigurationChanged(newConfig);
        }

        if (mainOverlayLayerManager != null){
            mainOverlayLayerManager.onConfigurationChanged(newConfig);
        }
    }



    //region [ STOP ]

    @Override
    public void onTaskRemoved(Intent rootIntent){
        FriendlyLog.log("I", "App", "TaskRemoved", "App closed (task removed)");
        DevTools.getActivityLogManager().setLastActivityResumed("");
        Log.w(DevTools.TAG, "OverlayUIService - onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        Log.d(DevTools.TAG, "OverlayUIService - onDestroy");
        if (mainOverlayLayerManager != null) mainOverlayLayerManager.destroy();
        if (overlayLayersManager != null) overlayLayersManager.destroy();

        super.onDestroy();
    }

    private void killProcess(){
        Log.d(DevTools.TAG, "OverlayUIService - Stopping service");
        stopSelf();
        AppUtils.exit();
    }

    //region




    public void navigateHome() {
        mainOverlayLayerManager.goHome();
    }

    public void navigateTo(String name) {
        navigateTo(name, null);
    }

    public void navigateTo(String name, String param) {
        overlayLayersManager.setMainVisibility(true);
        mainOverlayLayerManager.goTo(name, param);
    }

    public void navigateBack() {
        mainOverlayLayerManager.goBack();
    }

}
