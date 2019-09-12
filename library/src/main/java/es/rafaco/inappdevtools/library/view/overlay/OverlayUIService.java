package es.rafaco.inappdevtools.library.view.overlay;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.annotation.Nullable;
//#else
import android.support.annotation.Nullable;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.AnrDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.ConfigScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.AnalysisScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.HomeScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.ConsoleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.InspectViewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.MoreScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.RunScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.detail.NetworkDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreensScreen;

public class OverlayUIService extends Service {

    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    public static final String EXTRA_INTENT_TARGET = "EXTRA_INTENT_TARGET";
    private static final String EXTRA_INTENT_PARAMS = "EXTRA_INTENT_PARAMS";
    private static Boolean initialised = false;

    public enum IntentAction {
        PERMISSION_GRANTED,
        NAVIGATE_HOME,
        NAVIGATE_BACK,
        NAVIGATE_TO,
        SHOW_MAIN,
        SHOW_ICON,
        SHOW_TOGGLE,
        HIDE_ALL,
        RESTORE_ALL,
        REPORT,
        SCREEN,
        TOOL,
        RESTART_APP,
        CLOSE_APP,;
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
        String target = intent.getStringExtra(EXTRA_INTENT_TARGET);
        String params = intent.getStringExtra(EXTRA_INTENT_PARAMS);
        try {
            if (action != null){
                processIntentAction(action, target, params);
            }else{
                isInitialised();
                if (IadtController.get().isDebug())
                    Log.d(Iadt.TAG, "OverlayUIService - onStartCommand without action");
            }
        } catch (Exception e) {

            if (IadtController.get().isDebug()){
                throw e;
            }else{
                FriendlyLog.logException("OverlayUiService unable to start "
                        + action + " " + target + " " + params,  e);
                stopSelf();
            }
        }

        return START_NOT_STICKY;
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

    private boolean isInitialised() {
        if (initialised)
            return true;

        if (false){ //!PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            //TODO: remove
            throw new UnsupportedOperationException("Alguien ha llamado a OverlayUIService desde fuera!!!");
        }
        else {
            init();
            return true;
        }
    }

    private void init() {
        overlayLayersManager = new OverlayLayersManager(this);
        mainOverlayLayerManager = new MainOverlayLayerManager(this, overlayLayersManager.getMainLayer());

        initialised = true;
        if (IadtController.get().isDebug())
            Log.d(Iadt.TAG, "OverlayUIService - initialised");

        onInit();
    }

    private void onInit() {
        //Load home screen (ready to show and for back navigation
        navigateHome();

        if (PendingCrashUtil.isPending()){
            navigateTo(CrashDetailScreen.class.getSimpleName(), null);
            PendingCrashUtil.clearPending();
            overlayLayersManager.toggleVisibility(true);
            showMain();
        }
        else{
            overlayLayersManager.toggleVisibility(true);
            showIcon();
        }
    }

    //endregion

    //region [ PROCESS INTENT ACTION ]

    private void processIntentAction(IntentAction action, String target, String params) {
        if (!isInitialised())
            return;

        if (action.equals(IntentAction.NAVIGATE_HOME)){
            navigateHome();
        }
        else if (action.equals(IntentAction.NAVIGATE_BACK)){
            navigateBack();
        }
        else if (action.equals(IntentAction.NAVIGATE_TO)){
            String cleanName = target.replace(" Tool", "");
            navigateTo(cleanName, params);
        }
        else if (action.equals(IntentAction.SHOW_MAIN)) {
            showMain();
        }
        else if (action.equals(IntentAction.SHOW_ICON)){
            showIcon();
        }
        else if (action.equals(IntentAction.SHOW_TOGGLE)) {
            showToggle();
        }
        else if (action.equals(IntentAction.HIDE_ALL)){
            hideAll();
        }
        else if (action.equals(IntentAction.RESTORE_ALL)){
            restoreAll();
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

    private void showToggle() {
        overlayLayersManager.toggleMainIconVisibility(null);
    }

    private void showMain() {
        overlayLayersManager.toggleMainIconVisibility(true);
    }

    private void showIcon() {
        overlayLayersManager.toggleMainIconVisibility(false);
        if (Iadt.isDebug()){
            IadtController.get().getEventManager().fire(Event.OVERLAY_HIDDEN, null);
        }
    }

    private void hideAll() {
        overlayLayersManager.toggleVisibility(false);
    }

    private void restoreAll() {
        overlayLayersManager.toggleVisibility(true);
    }

    public void navigateHome() {
        mainOverlayLayerManager.goHome();
    }

    public void navigateTo(String name) {
        navigateTo(name, null);
    }

    public void navigateTo(String name, String param) {
        overlayLayersManager.toggleMainIconVisibility(true);
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
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "Stopping OverlayUIService");
        stopSelf();
        instance = null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        //TODO: relocate to eventmanager
        FriendlyLog.log("I", "App", "TaskRemoved", "App closed (task removed)");
        ActivityEventDetector activityWatcher = (ActivityEventDetector) Iadt.getEventDetector(ActivityEventDetector.class);
        activityWatcher.setLastActivityResumed("");
    }

    @Override
    public void onDestroy() {
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "OverlayUIService - onDestroy");
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

    public static Intent buildScreenIntentAction(Class<? extends OverlayScreen> screenClass, String params) {
        Intent intent = new Intent(Iadt.getAppContext(), OverlayUIService.class);
        intent.putExtra(OverlayUIService.EXTRA_INTENT_ACTION, IntentAction.NAVIGATE_TO);
        if (screenClass!=null){
            intent.putExtra(OverlayUIService.EXTRA_INTENT_TARGET, screenClass.getSimpleName());
        }
        if (!TextUtils.isEmpty(params)){
            intent.putExtra(OverlayUIService.EXTRA_INTENT_PARAMS, params);
        }
        return intent;
    }
    
    public static Intent buildIntentAction(OverlayUIService.IntentAction action, String property) {
        Intent intent = new Intent(Iadt.getAppContext(), OverlayUIService.class);
        intent.putExtra(OverlayUIService.EXTRA_INTENT_ACTION, action);
        if (!TextUtils.isEmpty(property)){
            intent.putExtra(OverlayUIService.EXTRA_INTENT_TARGET, property);
        }
        return intent;
    }

    public static void runAction(OverlayUIService.IntentAction action, String property) {
        Intent intent = buildIntentAction(action, property);
        Iadt.getAppContext().startService(intent);
    }

    //endregion
}
