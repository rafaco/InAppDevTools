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
import es.rafaco.devtools.view.OverlayUIService;
import es.rafaco.devtools.R;

public class PermissionActivity extends AppCompatActivity {

    private static final int OVERLAY_REQUEST_CODE = 1222;
    private static final int STORAGE_REQUEST_CODE = 1333;
    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";

    public enum IntentAction {OVERLAY, STORAGE}

    public static Intent buildIntent(IntentAction action, Context context) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(EXTRA_INTENT_ACTION, action);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentAction action = (IntentAction) getIntent().getSerializableExtra(EXTRA_INTENT_ACTION);
        if (action != null) {
            if (action.equals(IntentAction.OVERLAY)) {
                requestOverlayPermission();
            } else if (action.equals(IntentAction.STORAGE)) {
                requestStoragePermission();
            }
        } else {
            Log.d(DevTools.TAG, "OverlayUIService - onStartCommand without action");
        }

    }

    private void requestStoragePermission() {
        if (!checkPermission(IntentAction.STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_REQUEST_CODE);
        } else {
            onStoragePermissionGranted();
        }
    }

    public void requestOverlayPermission() {
        if (!checkPermission(IntentAction.OVERLAY)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_REQUEST_CODE);
        } else {
            onOverlayPermissionGranted();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_REQUEST_CODE) {
            if (checkOverlayPermission())
                onOverlayPermissionGranted();
            else
                DevTools.showMessage(R.string.draw_other_app_permission_denied);

        } else if (requestCode == STORAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
                onStoragePermissionGranted();
            else
                DevTools.showMessage("Storage permission denied");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void onOverlayPermissionGranted() {
        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.PERMISSION_GRANTED, "OverlayLayer");
        startService(intent);
        finish();
    }

    private void onStoragePermissionGranted() {
        Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.TOOL, "Screen");
        startService(intent);
        finish();
    }


    public static boolean checkPermission(IntentAction action) {
        if (action== IntentAction.OVERLAY) {
            return checkOverlayPermission();
        }else{
            //TODO: make dynamic for future IntentActions
            //TODO: String[] permissions = getPermissionsForAction
            //TODO: for each permission:  checkStandardPermission
            return checkStandardPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && checkStandardPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    public static boolean checkStandardPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ContextCompat.checkSelfPermission(DevTools.getAppContext(),
                    permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return Settings.canDrawOverlays(DevTools.getAppContext());
        }
        return true;
    }


    public static boolean isNeededWithAutoStart(Context context, IntentAction action) {
        if (!checkPermission(action)){
            //TODO: callback when granted
            DevTools.showMessage("Permission needed, try again when granted.");
            Intent intent = PermissionActivity.buildIntent(action, context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                context.startActivity(intent, null);
            return false;
        }
        //Permission granted or no dynamic permission needed
        return true;
    }
}
