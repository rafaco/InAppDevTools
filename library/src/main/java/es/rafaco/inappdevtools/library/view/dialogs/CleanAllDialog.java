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

import android.content.Context;
import android.content.DialogInterface;

import es.rafaco.inappdevtools.library.IadtController;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.view.ContextThemeWrapper;
//#else
import android.support.v7.app.AlertDialog;
//#endif

public class CleanAllDialog extends IadtDialogBuilder {

    public CleanAllDialog(Context context) {
        super(context);
    }

    @Override
    public AlertDialog build() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                .setTitle("Clean tools data?")
                .setMessage("You are going to delete all data locally collected by InAppDevTools.\n\nYour app will be restarted.")
                .setCancelable(true)
                .setPositiveButton("Clean All",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IadtController.get().performCleanAll();
                            }})
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        return alertDialog;
    }
}
