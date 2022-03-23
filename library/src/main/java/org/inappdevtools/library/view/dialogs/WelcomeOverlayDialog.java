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

package org.inappdevtools.library.view.dialogs;

import android.content.DialogInterface;

import org.inappdevtools.library.view.activities.PermissionActivity;
import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

public class WelcomeOverlayDialog extends IadtDialogBuilder {

    public WelcomeOverlayDialog() {
        super();
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {
        builder.setTitle(R.string.welcome_permission_title)
                .setMessage(R.string.welcome_permission_content)
                .setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestOverlayPermission();
                    }
                })
                .setNeutralButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onCancel();
                    }
                });
    }

    private void requestOverlayPermission() {
        PermissionActivity.request(PermissionActivity.IntentAction.OVERLAY,
                new Runnable() {
                    @Override
                    public void run() {
                        onPermissionGranted();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        onPermissionRevoked();
                    }
                });
    }


    public void onPermissionGranted() {
        IadtController.get().getOverlayHelper().showMain();
    }

    public void onPermissionRevoked() {
        Iadt.buildMessage(R.string.draw_other_app_permission_denied).isError().fire();
    }

    public void onCancel() {
        Iadt.buildMessage(R.string.draw_other_app_permission_denied).isError().fire();
    }
}
