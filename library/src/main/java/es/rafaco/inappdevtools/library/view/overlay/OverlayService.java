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
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PendingCrashUtil;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;

public class OverlayService extends Service {

    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    public static final String EXTRA_INTENT_TARGET = "EXTRA_INTENT_TARGET";
    private static final String EXTRA_INTENT_PARAMS = "EXTRA_INTENT_PARAMS";
    private static Boolean initialised = false;

    private OverlayManager overlayManager;

    public enum IntentAction {
        NAVIGATE_HOME,
        NAVIGATE_BACK,
        NAVIGATE_TO,
        SHOW_MAIN,
        SHOW_ICON,
        SHOW_TOGGLE,
    }

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
        if (overlayManager != null){
            overlayManager.onConfigurationChanged(newConfig);
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

        overlayManager = new OverlayManager(this);
        initialised = true;

        onInit();
    }

    private void onInit() {
        if (PendingCrashUtil.isPending()){
            overlayManager.navigateTo(CrashDetailScreen.class.getSimpleName(), null);
            PendingCrashUtil.clearPending();
            //TODO: check if needed
            //overlayManager.showMain();
        }
        else{
            overlayManager.showIcon();
        }
    }

    //endregion

    //region [ ACTIONS ]

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

            //IMPORTANT: This line can picks most of us internal exceptions
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
            overlayManager.navigateHome();
        }
        else if (action.equals(IntentAction.NAVIGATE_BACK)){
            overlayManager.navigateBack();
        }
        else if (action.equals(IntentAction.NAVIGATE_TO)){
            overlayManager.navigateTo(target, params);
        }
        else if (action.equals(IntentAction.SHOW_MAIN)) {
            overlayManager.showMain();
        }
        else if (action.equals(IntentAction.SHOW_ICON)){
            overlayManager.showIcon();
        }
        else if (action.equals(IntentAction.SHOW_TOGGLE)) {
            overlayManager.showToggle();
        }
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

        //TODO: ???
        activityWatcher.setLastActivityResumed("");
    }

    @Override
    public void onDestroy() {
        if (IadtController.get().isDebug())
            Log.v(Iadt.TAG, "OverlayService - onDestroy");
        if (overlayManager != null) overlayManager.destroy();
        instance = null;

        super.onDestroy();
    }

    //endregion

}
