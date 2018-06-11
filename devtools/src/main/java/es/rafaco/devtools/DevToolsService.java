package es.rafaco.devtools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.widgets.FullWidget;
import es.rafaco.devtools.widgets.Widget;
import es.rafaco.devtools.widgets.WidgetsManager;


public class DevToolsService extends Service {

    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    public static final String EXTRA_INTENT_PROPERTY = "EXTRA_INTENT_PROPERTY";
    public enum IntentAction { RESTART, CLOSE, EXCEPTION, REPORT, TOOL, ICON }

    private WidgetsManager widgetsManager;
    private ToolsManager toolsManager;

    public DevToolsService() {
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
            Log.d(DevTools.TAG, "DevToolsService - onStartCommand without action");
        }
        return DevTools.SERVICE_STICKY ? START_STICKY : START_NOT_STICKY;
    }

    private void processIntentAction(IntentAction action, String property) {
        Log.d(DevTools.TAG, "DevToolsService - onStartCommand with action: " + IntentAction.RESTART.toString());
        if (action.equals(IntentAction.TOOL)){
            startTool(property);
        }
        else if (action.equals(IntentAction.ICON)) {
            widgetsManager.toogleFullMode(true);
        }
        else if (action.equals(IntentAction.REPORT)){
            startTool("Report");
        }
        else if (action.equals(IntentAction.CLOSE)){
            killProcess();

        }else if (action.equals(IntentAction.RESTART)){
            programAppRestart();
            killProcess();
        }
    }

    public static Intent buildIntentAction(DevToolsService.IntentAction action, String property) {
        Intent intent = new Intent(DevTools.getAppContext(), DevToolsService.class);
        intent.putExtra(DevToolsService.EXTRA_INTENT_ACTION, action);
        if (!TextUtils.isEmpty(property)){
            intent.putExtra(DevToolsService.EXTRA_INTENT_PROPERTY, property);
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

        startTool("Home");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        widgetsManager.onConfigurationChanged(newConfig);
    }

    public void startTool(String title) {
        toolsManager.selectTool(title);
        widgetsManager.toogleFullMode(true);
        widgetsManager.selectTool(title);
    }

    public ViewGroup getToolContainer() {
        return ((FullWidget)widgetsManager.getWidget(Widget.Type.FULL)).getToolContainer();
    }

    @Override
    public void onDestroy() {

        toolsManager.destroy();
        widgetsManager.destroy();

        super.onDestroy();
    }


    //region [ TOOLS PLAYGROUND ]

    private String m_Text = "";


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            //View v1 = getWindow().getDecorView().getRootView();
            ViewGroup v1 = (ViewGroup) ((ViewGroup) widgetsManager.getView(Widget.Type.FULL).findViewById(android.R.id.content)).getChildAt(0);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    //endregion

    private void programAppRestart() {
        Log.e("DevTools", "Programming restart...");
        PackageManager pm = getApplicationContext().getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());

        intent.putExtra("crash", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext().getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
    }

    private void killProcess(){
        Log.d("DevTools", "Stopping service");
        stopSelf();
        //Log.e("DevTools", "Killing process...");
        //android.os.Process.killProcess(android.os.Process.myPid());
        Log.d("DevTools", "Killing application");
        System.exit(10);
    }
}
