package es.rafaco.devtools.logic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.DevToolsService;
import es.rafaco.devtools.R;

public class PermissionActivity extends AppCompatActivity {

    private static final int OVERLAY_REQUEST_CODE = 1222;
    private static final int STORAGE_REQUEST_CODE = 1333;
    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";

    public enum IntentAction { OVERLAY, STORAGE }

    public static Intent buildIntent(IntentAction action, Context context){
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(EXTRA_INTENT_ACTION, action);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentAction action = (IntentAction)getIntent().getSerializableExtra(EXTRA_INTENT_ACTION);
        if (action != null){
            if(action.equals(IntentAction.OVERLAY)){
                requestOverlayPermission();
            }else if(action.equals(IntentAction.STORAGE)){
                requestStoragePermission();
            }
        }else{
            Log.d(DevTools.TAG, "DevToolsService - onStartCommand without action");
        }

    }

    private void requestStoragePermission() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        ||(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){

            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_REQUEST_CODE);
        }else{
            onStoragePermissionGranted();
        }
    }

    public void requestOverlayPermission() {
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_REQUEST_CODE);
        } else {
            //If permission is granted start floating widget service
            onOverlayPermissionGranted();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_REQUEST_CODE) {
            //Check if the permission is granted or not.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this))
                //If permission granted start floating widget service
                onOverlayPermissionGranted();
            else
                //Permission is not available then display toast
                DevTools.showMessage(R.string.draw_other_app_permission_denied);

        }else if (requestCode == STORAGE_REQUEST_CODE) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK)
                //If permission granted start floating widget service
                onStoragePermissionGranted();
            else
                //Permission is not available then display toast
                DevTools.showMessage("Storage permission denied");
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*  Start Floating widget service and finish current activity */
    private void onOverlayPermissionGranted() {
        startService(new Intent(PermissionActivity.this, DevToolsService.class));
        finish();
    }

    private void onStoragePermissionGranted() {
        Intent intent = DevToolsService.buildIntentAction(DevToolsService.IntentAction.REPORT, null);
        startService(intent);
        finish();
    }
}
