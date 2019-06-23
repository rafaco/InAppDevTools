package es.rafaco.inappdevtools.library.view.overlay;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

//#ifdef MODERN
//@import androidx.annotation.Nullable;
//#else
import android.support.annotation.Nullable;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.AnrDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.HomeScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.commands.CommandsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.InspectViewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.MoreScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.RunScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.detail.NetworkDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreensScreen;

public class OverlayUIService extends Service {

    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    public static final String EXTRA_INTENT_PROPERTY = "EXTRA_INTENT_PROPERTY";
    private static final String EXTRA_INTENT_PARAM = "EXTRA_INTENT_PARAM";
    private static Boolean initialised = false;
    private String lastRequestParam;

    public enum IntentAction {
        PERMISSION_GRANTED,
        NAVIGATE_HOME,
        NAVIGATE_BACK,
        NAVIGATE_TO,
        SHOW,
        HIDE,
        REPORT,
        SCREEN,
        TOOL,
        RESTART_APP,
        CLOSE_APP,
    }

    private OverlayLayersManager overlayLayersManager;
    private MainOverlayLayerManager mainOverlayLayerManager;

    public OverlayUIService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        initialised = false;
        instance = this;
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
        try {
            //TODO: cleanUp
            lastRequestParam = intent.getStringExtra(EXTRA_INTENT_PARAM);
            if (action != null){
                processIntentAction(action, property);
            }else{
                Log.v(Iadt.TAG, "OverlayUIService - onStartCommand without action");
            }
        } catch (Exception e) {

            if(Iadt.isDebug()){
                throw e;
            }else{
                //Handle internal exceptions with low profile
                FriendlyLog.logException("OverlayUiService unable to start " + action + " " + property, e);
                e.printStackTrace();
                stopSelf();
            }
        }

        return Iadt.getConfig().getBoolean(Config.STICKY_SERVICE) ?
                START_STICKY : START_NOT_STICKY;
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

    //region [ INITIALIZE ]

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
            Log.w(Iadt.TAG, "OverlayUIService - initialised");
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
        mainOverlayLayerManager.registerScreen(RunScreen.class);
        mainOverlayLayerManager.registerScreen(MoreScreen.class);
        mainOverlayLayerManager.registerScreen(InspectViewScreen.class);
        mainOverlayLayerManager.registerScreen(SourcesScreen.class);
        mainOverlayLayerManager.registerScreen(SourceDetailScreen.class);
    }

    //endregion

    //region [ PROCESS INTENT ACTION ]

    private void processIntentAction(IntentAction action, String property) {
        Log.v(Iadt.TAG, "OverlayUIService - onStartCommand with action: " + action.toString());

        if (!isInitialised(action, property))
            return;

        //Restore previous request on permission granted
        if (action.equals(IntentAction.PERMISSION_GRANTED)) {
            //TODO: remove
        }

        if (action.equals(IntentAction.NAVIGATE_HOME)){
            navigateHome();
        }
        else if (action.equals(IntentAction.NAVIGATE_BACK)){
            navigateBack();
        }
        else if (action.equals(IntentAction.NAVIGATE_TO)){
            String cleanName = property.replace(" Tool", "");
            navigateTo(cleanName, lastRequestParam);
        }
        else if (action.equals(IntentAction.HIDE)){
            hide();
        }
        else if (action.equals(IntentAction.SHOW)) {
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
        else if (action.equals(IntentAction.CLOSE_APP)){
            IadtController.get().forceCloseApp(false);
        }
        else if (action.equals(IntentAction.RESTART_APP)){
            IadtController.get().restartApp(false);
        }
    }

    //endregion

    //region [ INTERNAL NAVIGATION ]

    private void hide() {
        overlayLayersManager.setMainVisibility(false);
    }

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

    //endregion

    //region [ STOP ]

    //TODO: [LOW:Arch] Replace by bounded service
    private static OverlayUIService instance;

    public static void stop(){
        if (instance != null) instance.stopService();
    }

    private void stopService() {
        Log.d(Iadt.TAG, "Stopping OverlayUIService");
        stopSelf();
        instance = null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        FriendlyLog.log("I", "App", "TaskRemoved", "App closed (task removed)");
        Log.w(Iadt.TAG, "OverlayUIService - onTaskRemoved");

        ActivityEventDetector activityWatcher = (ActivityEventDetector) Iadt.getEventDetector(ActivityEventDetector.class);
        activityWatcher.setLastActivityResumed("");
    }

    @Override
    public void onDestroy() {
        Log.d(Iadt.TAG, "OverlayUIService - onDestroy");
        if (mainOverlayLayerManager != null) mainOverlayLayerManager.destroy();
        if (overlayLayersManager != null) overlayLayersManager.destroy();
        instance = null;

        super.onDestroy();
    }

    //endregion

    //region [ STATIC ACCESSORS ]

    public static void performNavigation(Class<? extends OverlayScreen> target) {
        performNavigationStep(new NavigationStep(target, null));
    }

    public static void performNavigation(Class<? extends OverlayScreen> target, String param) {
        performNavigationStep(new NavigationStep(target, param));
    }

    public static void performNavigationStep(NavigationStep step) {
        if (step == null){
            return;
        }
        Intent intent = buildScreenIntentAction(step.getClassName(), step.getParam());
        Iadt.getAppContext().startService(intent);
    }

    public static Intent buildScreenIntentAction(Class<? extends OverlayScreen> screenClass, String param) {
        Intent intent = new Intent(Iadt.getAppContext(), OverlayUIService.class);
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
        Intent intent = new Intent(Iadt.getAppContext(), OverlayUIService.class);
        intent.putExtra(OverlayUIService.EXTRA_INTENT_ACTION, action);
        if (!TextUtils.isEmpty(property)){
            intent.putExtra(OverlayUIService.EXTRA_INTENT_PROPERTY, property);
        }
        return intent;
    }

    public static void runAction(OverlayUIService.IntentAction action, String property) {
        Intent intent = buildIntentAction(action, property);
        Iadt.getAppContext().startService(intent);
    }

    //endregion
}
