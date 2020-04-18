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

package es.rafaco.inappdevtools.library.logic.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.utils.ClassHelper;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.dialogs.IadtDialogBuilder;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;

public class DialogManager {

    Context context;
    Class<? extends IadtDialogBuilder> currentBuilderClass;
    AlertDialog currentDialog;
    String currentActivityHash;
    private boolean isManagerDismiss = false;

    public DialogManager(Context context) {
        this.context = context;

        init();
    }

    private void init() {
        IadtController.get().getEventManager().subscribe(Event.ACTIVITY_OPEN, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                onActivityOpen((Activity)param);
            }
        });

        IadtController.get().getEventManager().subscribe(Event.ACTIVITY_ON_STOP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                onActivityDestroy((Activity)param);
            }
        });
    }

    private void onActivityOpen(Activity activity) {
        Log.d("DIALOGS", "Activity Open: " + activity.getClass().getSimpleName());
        if (isLoaded() && !currentActivityHash.equals(activity.toString())){
            dismiss();
            build(currentBuilderClass);
            show();
        }
    }

    private void onActivityDestroy(Activity activity) {
        if (isLoaded() && currentActivityHash.equals(activity.toString())){
            Log.d("DIALOGS", "Current activity destroy: " + activity.getClass().getSimpleName());
            isManagerDismiss = true;
            dismiss();
        }
    }



    public void load(Class<? extends IadtDialogBuilder> dialogClass){
        dismiss();
        build(dialogClass);
        show();
    }

    private boolean isLoaded() {
        return currentBuilderClass != null;
    }

    private void cleanLoaded() {
        currentDialog = null;
        currentBuilderClass = null;
        isManagerDismiss = false;
    }

    private void dismiss() {
        if (currentDialog!=null){// && currentDialog.isShowing()){
            Log.d("DIALOGS", "Dismissing dialog: " + currentBuilderClass.getSimpleName());
            isManagerDismiss = true;
            currentDialog.dismiss();
        }
    }

    private void build(Class<? extends IadtDialogBuilder> dialogClass) {
        Log.d("DIALOGS", "Creating dialog");
        Activity currentActivity = getCurrentActivity();
        IadtDialogBuilder newDialogBuilder = new ClassHelper<IadtDialogBuilder>()
                .createClass(dialogClass, Context.class, currentActivity);

        currentActivityHash = currentActivity.toString();
        currentBuilderClass = dialogClass;
        currentDialog = newDialogBuilder.build();
        currentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isManagerDismiss) {
                    isManagerDismiss = false;
                }
                else{
                    //Dismissed by user
                    cleanLoaded();
                }
            }
        });

        if (PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)){
            currentDialog.getWindow().setType(Layer.getLayoutType());
        }
        //Else use default layout type for dialogs

        currentDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
    }

    private void show() {
        Log.d("DIALOGS", "Showing dialog");
        currentDialog.show();
    }

    private Activity getCurrentActivity() {
        return IadtController.get().getEventManager().getActivityWatcher().getCurrentActivity();
    }
}
