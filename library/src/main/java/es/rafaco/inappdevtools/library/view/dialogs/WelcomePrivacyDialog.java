/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.dialogs;

import android.content.DialogInterface;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.prefs.utils.PrivacyConsentPrefs;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

public class WelcomePrivacyDialog extends IadtDialogBuilder {

    public WelcomePrivacyDialog() {
        super();
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {
        builder
                .setTitle(R.string.welcome_privacy_title)
                .setMessage(R.string.welcome_privacy_content)
                .setPositiveButton(R.string.button_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        PrivacyConsentPrefs.saveAccepted();
                        onAccepted();
                    }
                })
                .setNeutralButton(R.string.button_disable, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onDisable();
                    }
                });
    }

    public void onAccepted() {

    }

    public void onDisable() {
        getManager().load(new DisableDialog());
    }
}
