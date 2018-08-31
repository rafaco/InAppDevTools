package es.rafaco.devtools.view;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.User;
import es.rafaco.devtools.utils.AppUtils;
import es.rafaco.devtools.view.activities.PermissionActivity;
import es.rafaco.devtools.view.overlay.OverlayLayersManager;
import es.rafaco.devtools.view.overlay.OverlayScreenManager;
import es.rafaco.devtools.view.overlay.screens.home.HomeScreen;
import es.rafaco.devtools.view.overlay.screens.commands.CommandsScreen;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;
import es.rafaco.devtools.view.overlay.screens.report.ReportScreen;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;


public class OverlayUIService extends Service {

    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    public static final String EXTRA_INTENT_PROPERTY = "EXTRA_INTENT_PROPERTY";
    private static Boolean initialised = false;

    public enum IntentAction { PERMISSION_GRANTED, RESTART, CLOSE, EXCEPTION, REPORT, SCREEN, TOOL, MAIN, ICON }

    private OverlayLayersManager overlayLayersManager;
    private OverlayScreenManager overlayScreenManager;

    public OverlayUIService() {
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
            startTool(property.replace(" Tool", ""));
        }
        else if (action.equals(IntentAction.ICON)) {
            overlayLayersManager.setMainVisibility(false);
        }
        else if (action.equals(IntentAction.MAIN)) {
            if (overlayScreenManager.getCurrentScreen() == null){
                //TODO: load home if forced from parameter
                startTool(HomeScreen.class.getSimpleName());
            }
            overlayLayersManager.setMainVisibility(true);
        }
        else if (action.equals(IntentAction.REPORT)){
            startTool(ReportScreen.class.getSimpleName());
        }
        else if (action.equals(IntentAction.SCREEN)){
            startTool(ReportScreen.class.getSimpleName());
        }
        else if (action.equals(IntentAction.CLOSE)){
            killProcess();

        }else if (action.equals(IntentAction.RESTART)){
            AppUtils.programRestart(getApplicationContext());
            killProcess();
        }
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
            /*PermissionActivity.request(PermissionActivity.IntentAction.OVERLAY,
                    new Runnable(){
                        @Override
                        public void run() { processIntentAction(action, property);
                        }
                    }, null);
            return false;*/
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
        overlayScreenManager = new OverlayScreenManager(this, overlayLayersManager.getMainLayer());

        //TODO: delegate to ToolManager
        overlayScreenManager.registerScreen(HomeScreen.class);
        overlayScreenManager.registerScreen(InfoScreen.class);
        overlayScreenManager.registerScreen(ErrorsScreen.class);
        overlayScreenManager.registerScreen(LogScreen.class);
        overlayScreenManager.registerScreen(CommandsScreen.class);
        overlayScreenManager.registerScreen(ScreensScreen.class);
        overlayScreenManager.registerScreen(ReportScreen.class);

        //TODO: delegate to ToolManager
        List<String> toolsList = overlayScreenManager.getMainScreens();
        overlayLayersManager.initToolList(toolsList);

        //testUserDao();

        //TODO: replace by icon
        //startTool("Home");
        overlayLayersManager.setMainVisibility(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (overlayLayersManager != null){
            overlayLayersManager.onConfigurationChanged(newConfig);
        }
    }



    //region [ STOP ]

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.w(DevTools.TAG, "OverlayUIService - onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        Log.d(DevTools.TAG, "OverlayUIService - onDestroy");
        if (overlayScreenManager != null) overlayScreenManager.destroy();
        if (overlayLayersManager != null) overlayLayersManager.destroy();

        super.onDestroy();
    }

    private void killProcess(){
        Log.d(DevTools.TAG, "OverlayUIService - Stopping service");
        stopSelf();
        AppUtils.exit();
    }

    //region



    public void startTool(String name) {
        overlayScreenManager.loadScreen(name, null);
        overlayLayersManager.setMainVisibility(true);
        overlayLayersManager.selectTool(name);
    }

    //TODO: REMOVE
    private void testUserDao() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.setFirstName("Ajay");
                user.setLastName("Saini");
                user.setAge(25);

                DevToolsDatabase db = DevTools.getDatabase();
                Log.d(DevTools.TAG, "Database size is: " + db.userDao().countUsers());
                db.userDao().insertAll(user);
                Log.d(DevTools.TAG, "Database size is: " + db.userDao().countUsers());

                User stored = db.userDao().findByName("Ajay", "Saini");
                Log.d(DevTools.TAG, "Database age is: " + stored.getAge());
            }
        });
    }

    //TODO: REFACTOR
    public ViewGroup getMainLayerContainer() {
        return overlayLayersManager.getMainLayer().getScreenWrapper();
    }
}
