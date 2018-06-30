package es.rafaco.devtools.view;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.User;
import es.rafaco.devtools.view.overlay.OverlayToolsManager;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.utils.AppUtils;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayer;
import es.rafaco.devtools.view.overlay.layers.OverlayLayer;
import es.rafaco.devtools.view.overlay.OverlayLayersManager;


public class OverlayUIService extends Service {

    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    public static final String EXTRA_INTENT_PROPERTY = "EXTRA_INTENT_PROPERTY";
    private boolean initialised;

    public enum IntentAction { PERMISSION_GRANTED, RESTART, CLOSE, EXCEPTION, REPORT, SCREEN, TOOL, MAIN, ICON }

    private OverlayLayersManager overlayLayersManager;
    private OverlayToolsManager overlayToolsManager;

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

        boolean canContinue = isInitialised();
        if (!canContinue)
            return;

        if (action.equals(IntentAction.TOOL)){
            startTool(property.replace(" Tool", ""));
        }
        else if (action.equals(IntentAction.ICON)) {
            overlayLayersManager.setMainVisibility(false);
        }
        else if (action.equals(IntentAction.MAIN)) {
            if (overlayToolsManager.getCurrent() == null){
                //TODO: load home if forced from parameter
                startTool("Home");
            }
            overlayLayersManager.setMainVisibility(true);
        }
        else if (action.equals(IntentAction.PERMISSION_GRANTED)) {
            isInitialised();
        }
        else if (action.equals(IntentAction.REPORT)){
            startTool("Report");
        }
        else if (action.equals(IntentAction.SCREEN)){
            startTool("Report");
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

    private boolean isInitialised() {
        if (initialised)
            return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //Start a flash activity to request required permissions
            Intent intent = PermissionActivity.buildIntent(PermissionActivity.IntentAction.OVERLAY, getApplicationContext());
            startActivity(intent, null);
            return false;
        } else {
            init();
            initialised = true;
            Log.w(DevTools.TAG, "OverlayUIService - initialised");
            return true;
        }
    }

    private void init() {
        overlayLayersManager = new OverlayLayersManager(this);
        overlayToolsManager = new OverlayToolsManager(this);

        ArrayList<String> toolsList = overlayToolsManager.getToolList();
        overlayLayersManager.initToolList(toolsList);

        //testUserDao();

        //TODO: replace by icon
        //startTool("Home");
        overlayLayersManager.setMainVisibility(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        overlayLayersManager.onConfigurationChanged(newConfig);
    }



    //region [ STOP ]

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.w(DevTools.TAG, "OverlayUIService - onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        Log.d(DevTools.TAG, "OverlayUIService - onDestroy");
        overlayToolsManager.destroy();
        overlayLayersManager.destroy();

        super.onDestroy();
    }

    private void killProcess(){
        Log.d(DevTools.TAG, "OverlayUIService - Stopping service");
        stopSelf();
        AppUtils.exit();
    }

    //region



    public void startTool(String title) {
        overlayToolsManager.selectTool(title);
        overlayLayersManager.setMainVisibility(true);
        overlayLayersManager.selectTool(title);
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
        return ((MainOverlayLayer) overlayLayersManager.getWidget(OverlayLayer.Type.MAIN)).getToolContainer();
    }
}
