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
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;

public class OverlayService extends Service {

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

    private LayerManager layerManager;
    private ScreenManager screenManager;

    public OverlayService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        initialised = false;
        instance = this;
        ThreadUtils.printOverview("OverlayService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (layerManager != null){
            layerManager.onConfigurationChanged(newConfig);
        }

        if (screenManager != null){
            screenManager.onConfigurationChanged(newConfig);
        }
    }

    //region [ STATIC ACCESSORS ]

    public static void performNavigation(Class<? extends Screen> target) {
        performNavigationStep(new NavigationStep(target, null));
    }

    public static void performNavigation(Class<? extends Screen> target, String param) {
        performNavigationStep(new NavigationStep(target, param));
    }

    public static void performNavigationStep(NavigationStep step) {
        if (step == null){
            return;
        }
        Intent intent = buildNavigationIntent(step.getClassName(), step.getParams());
        IadtController.get().getContext().startService(intent);
    }

    private static Intent buildNavigationIntent(Class<? extends Screen> screenClass, String params) {
        Intent intent = new Intent(IadtController.get().getContext(), OverlayService.class);
        intent.putExtra(OverlayService.EXTRA_INTENT_ACTION, IntentAction.NAVIGATE_TO);
        if (screenClass!=null){
            intent.putExtra(OverlayService.EXTRA_INTENT_TARGET, screenClass.getSimpleName());
        }
        if (!TextUtils.isEmpty(params)){
            intent.putExtra(OverlayService.EXTRA_INTENT_PARAMS, params);
        }
        return intent;
    }

    public static void performAction(OverlayService.IntentAction action) {
        performAction(action, null);
    }

    public static void performAction(OverlayService.IntentAction action, String property) {
        Intent intent = buildActionIntent(action, property);
        IadtController.get().getContext().startService(intent);
    }

    public static Intent buildActionIntent(OverlayService.IntentAction action, String property) {
        Intent intent = new Intent(IadtController.get().getContext(), OverlayService.class);
        intent.putExtra(OverlayService.EXTRA_INTENT_ACTION, action);
        if (!TextUtils.isEmpty(property)){
            intent.putExtra(OverlayService.EXTRA_INTENT_TARGET, property);
        }
        return intent;
    }

    //endregion

    //region [ INIT ]

    public static boolean isInitialize() {
        return initialised;
    }

    private void init() {
        if (IadtController.get().isDebug())
            Log.d(Iadt.TAG, "OverlayService - init()");

        layerManager = new LayerManager(this);
        screenManager = new ScreenManager(this, layerManager.getMainLayer());
        initialised = true;

        if (IadtController.get().isDebug())
            Log.d(Iadt.TAG, "OverlayService - initialised");

        onInit();
    }

    private void onInit() {
        if (PendingCrashUtil.isPending()){
            navigateTo(CrashDetailScreen.class.getSimpleName(), null);
            PendingCrashUtil.clearPending();
            layerManager.toggleAllLayerVisibility(true);
            showMain();
        }
        else{
            //Show icon
            layerManager.toggleAllLayerVisibility(true);
            layerManager.toggleMainLayerVisibility(false);
        }
    }

    //endregion

    //region [ ACTION CONTROLLER ]

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentAction action = (IntentAction)intent.getSerializableExtra(EXTRA_INTENT_ACTION);
        String target = intent.getStringExtra(EXTRA_INTENT_TARGET);
        String params = intent.getStringExtra(EXTRA_INTENT_PARAMS);

        try {
            if (action != null){
                processIntentAction(action, target, params);
            }else{
                init();
            }
        }
        catch (Exception e) {

            //IMPORTANT: This catch picks most of internal exceptions
            if (IadtController.get().isDebug()){
                throw e;
            }
            else{
                FriendlyLog.logException("OverlayUiService unable to start "
                        + action + " " + target + " " + params,  e);
                stopSelf();
            }
        }

        return START_NOT_STICKY;
    }

    private void processIntentAction(IntentAction action, String target, String params) {
        if (!isInitialize()) init();

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
        if (screenManager.getCurrentScreen() == null){
            screenManager.goHome();
        }

        layerManager.toggleMainLayerVisibility(null);
    }

    private void showMain() {
        if (screenManager.getCurrentScreen() == null){
            screenManager.goHome();
        }

        layerManager.toggleMainLayerVisibility(true);
    }

    private void showIcon() {
        layerManager.toggleMainLayerVisibility(false);
        if (Iadt.isDebug()){
            IadtController.get().getEventManager().fire(Event.OVERLAY_HIDDEN, null);
        }
    }

    private void hideAll() {
        layerManager.toggleAllLayerVisibility(false);
    }

    private void restoreAll() {
        layerManager.toggleAllLayerVisibility(true);
    }

    public void navigateHome() {
        screenManager.goHome();
    }

    public void navigateTo(String name) {
        navigateTo(name, null);
    }

    public void navigateTo(String name, String param) {
        layerManager.toggleMainLayerVisibility(true);
        screenManager.goTo(name, param);
    }

    public void navigateBack() {
        screenManager.goBack();
    }

    //endregion

    //region [ STOP ]

    //TODO: [LOW:Arch] Replace by bounded service
    private static OverlayService instance;

    public static void stop(){
        if (instance != null) instance.stopService();
    }

    private void stopService() {
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "Stopping OverlayService");
        stopSelf();
        instance = null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        //TODO: relocate to eventmanager
        FriendlyLog.log("I", "App", "TaskRemoved", "App closed (task removed)");
        ActivityEventDetector activityWatcher = (ActivityEventDetector) IadtController.get().getEventManager()
                .getEventDetectorsManager().get(ActivityEventDetector.class);
        activityWatcher.setLastActivityResumed("");
    }

    @Override
    public void onDestroy() {
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "OverlayService - onDestroy");
        if (screenManager != null) screenManager.destroy();
        if (layerManager != null) layerManager.destroy();
        instance = null;

        super.onDestroy();
    }

    //endregion

}
