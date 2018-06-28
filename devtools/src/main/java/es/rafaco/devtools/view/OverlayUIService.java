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
import es.rafaco.devtools.view.overlay.tools.ToolsManager;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.utils.AppUtils;
import es.rafaco.devtools.view.overlay.widgets.FullWidget;
import es.rafaco.devtools.view.overlay.widgets.Widget;
import es.rafaco.devtools.view.overlay.widgets.WidgetsManager;


public class
OverlayUIService extends Service {

    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    public static final String EXTRA_INTENT_PROPERTY = "EXTRA_INTENT_PROPERTY";
    public enum IntentAction { PERMISSION_GRANTED, RESTART, CLOSE, EXCEPTION, REPORT, SCREEN, TOOL, FULL, ICON }

    private WidgetsManager widgetsManager;
    private ToolsManager toolsManager;

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
        if (action.equals(IntentAction.TOOL)){
            startTool(property.replace(" Tool", ""));
        }
        else if (action.equals(IntentAction.ICON)) {
            widgetsManager.toogleFullMode(false);
        }
        else if (action.equals(IntentAction.FULL)) {
            if (toolsManager.getCurrent() == null){
                startTool("Home");
            }
            widgetsManager.toogleFullMode(true);
        }
        else if (action.equals(IntentAction.PERMISSION_GRANTED)) {
            initOrRequestPermission();
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

    @Override
    public void onCreate() {
        super.onCreate();

        initOrRequestPermission();
    }

    private void initOrRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //Start a flash activity to request required permissions
            Intent intent = PermissionActivity.buildIntent(PermissionActivity.IntentAction.OVERLAY, getApplicationContext());
            startActivity(intent, null);
        } else {
            init();
        }
    }

    private void init() {
        widgetsManager = new WidgetsManager(this);
        toolsManager = new ToolsManager(this);

        ArrayList<String> toolsList = toolsManager.getToolList();
        ((FullWidget)widgetsManager.getWidget(Widget.Type.FULL)).initToolSelector(toolsList);

        testUserDao();

        //TODO: replace by icon
        //startTool("Home");
        widgetsManager.toogleFullMode(false);
        DevTools.showMessage("DevTools is watching your back");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        widgetsManager.onConfigurationChanged(newConfig);
    }



    //region [ STOP ]

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.e("DevTools", "onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        Log.e("DevTools", "onDestroy");
        toolsManager.destroy();
        widgetsManager.destroy();

        super.onDestroy();
    }

    private void killProcess(){
        Log.d("DevTools", "Stopping service");
        stopSelf();
        AppUtils.exit();
    }

    //region



    public void startTool(String title) {
        toolsManager.selectTool(title);
        widgetsManager.toogleFullMode(true);
        widgetsManager.selectTool(title);
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
    public ViewGroup getToolContainer() {
        return ((FullWidget)widgetsManager.getWidget(Widget.Type.FULL)).getToolContainer();
    }
}
