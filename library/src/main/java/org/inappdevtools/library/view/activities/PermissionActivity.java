/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.view.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.core.app.ActivityCompat;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
//#endif

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;

public class PermissionActivity extends AppCompatActivity {

    private static final int OVERLAY_REQUEST_CODE = 3000;
    private static final int STORAGE_REQUEST_CODE = 3001;
    public static final String EXTRA_INTENT_ACTION = "EXTRA_INTENT_ACTION";
    private static Runnable onGrantedCallback;
    private static Runnable onRevokeCallback;

    public enum IntentAction {OVERLAY, STORAGE}


    //region [ STATIC INITIALIZATION ]

    public static boolean check(IntentAction action) {
        if (action.equals(IntentAction.OVERLAY)) {
            return checkOverlayPermission();
        }else{
            return checkStandardPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && checkStandardPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    public static void request(IntentAction action, Runnable onSuccessCallback, Runnable onFailCallback) {
        if (onSuccessCallback!=null)
            onGrantedCallback = onSuccessCallback;

        if (onFailCallback!=null)
            onRevokeCallback = onFailCallback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            Context context = IadtController.get().getContext();
            Intent intent = PermissionActivity.buildIntent(action, context);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent, null);
        }else if (onSuccessCallback!=null){
            onSuccessCallback.run();
        }
    }

    private static Intent buildIntent(IntentAction action, Context context) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(EXTRA_INTENT_ACTION, action);
        return intent;
    }

    private static boolean checkStandardPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ContextCompat.checkSelfPermission(IadtController.get().getContext(),
                    permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private static boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return Settings.canDrawOverlays(IadtController.get().getContext());
        }
        return true;
    }

    //endregion


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
            Log.d(Iadt.TAG, "OverlayService - onStartCommand without action");
        }
    }


    //region [ OVERLAY PERMISSION ]

    private void requestOverlayPermission() {
        if (!check(IntentAction.OVERLAY)) {
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
            if (checkOverlayPermission()){
                onOverlayPermissionGranted();
            } else{
                onOverlayPermissionRevoked();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void onOverlayPermissionGranted() {
        if(onGrantedCallback != null){
            onGrantedCallback.run();
            onGrantedCallback = null;
        }
        finish();
    }

    private void onOverlayPermissionRevoked() {
        if(onRevokeCallback != null){
            onRevokeCallback.run();
            onRevokeCallback = null;
        }
        finish();
    }

    //endregion


    //region [ STORAGE PERMISSION ]

    private void requestStoragePermission() {
        if (!check(IntentAction.STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_REQUEST_CODE);
        } else {
            onStoragePermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onStoragePermissionGranted();
                } else {
                    Iadt.buildMessage("Storage permission denied, operation cancelled")
                            .isError().fire();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void onStoragePermissionGranted() {
        if(onGrantedCallback != null){
            onGrantedCallback.run();
            onGrantedCallback = null;
        }
        finish();
    }

    //endregion
}
