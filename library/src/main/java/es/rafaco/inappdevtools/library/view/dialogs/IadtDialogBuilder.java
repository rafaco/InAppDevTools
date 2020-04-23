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
import android.content.ContextWrapper;
import android.content.DialogInterface;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.view.ContextThemeWrapper;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
//#endif

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.dialogs.DialogManager;

public abstract class IadtDialogBuilder {

    ContextWrapper context;
    private boolean isCancelable;

    public IadtDialogBuilder() {

    }

    public AlertDialog createDialog(Context baseContext){
        setContext(baseContext);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        initBuilder(builder);
        onBuilderCreated(builder);

        AlertDialog dialog = builder.create();
        initDialog(dialog);
        onDialogCreated(dialog);

        return dialog;
    }

    private void initBuilder(AlertDialog.Builder builder) {
        builder.setCancelable(isCancelable);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onDismissWrapper(dialog);
            }
        });
    }

    public abstract void onBuilderCreated(AlertDialog.Builder builder);

    private void initDialog(AlertDialog dialog) {
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
    }

    public void onDialogCreated(AlertDialog dialog) {
        //Intentionally empty, optional to override
    }

    private void setContext(Context baseContext){
        context = new ContextThemeWrapper(baseContext, R.style.LibTheme_Dialog);
    }

    protected Context getContext(){
        return context;
    }

    protected DialogManager getManager(){
        return IadtController.get().getDialogManager();
    }


    public IadtDialogBuilder setCancelable (boolean isCancelable){
        this.isCancelable = isCancelable;
        return this;
    }

    public void onDismissWrapper(DialogInterface dialog) {
        onDismiss(dialog);
        getManager().onDismiss(dialog);
    }

    public void onDismiss(DialogInterface dialog) {
        //Intentionally empty, optional to override
    }
}
