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

import org.inappdevtools.library.R;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

public abstract class WelcomeSuccessDialog extends IadtDialogBuilder {

    public WelcomeSuccessDialog() {
        super();
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {
        builder.setIcon(R.drawable.ic_check_circle_green_24dp)
                .setTitle(R.string.welcome_enabled_title)
                .setMessage(R.string.welcome_enabled_content)
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }
}
