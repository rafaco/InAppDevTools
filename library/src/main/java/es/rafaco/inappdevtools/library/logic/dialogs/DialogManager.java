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
import android.view.View;
//#endif

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.dialogs.IadtDialogBuilder;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;


/**
 * DialogManager.java
 *  Build and show dialogs with custom styles, defined by a IadtDialogBuilder
 *  This dialogs are persistent across activities using 2 mechanics depending on canDrawOverlay:
 *  - Initially (!canDrawOverlay) they are recreated by using activity change listeners
 *  - When canDrawOverlay, they are just created as overlay persist
 */
public class DialogManager {

    Context context;
    IadtDialogBuilder builder;
    AlertDialog currentDialog;
    String currentActivityHash;
    private boolean isManagerDismiss = false;

    private Boolean isOverlayMode;
    private EventManager.Listener activityOpenListener;
    private EventManager.Listener activityStopListener;
    private EventManager.Listener onForegroundListener;
    private EventManager.Listener onBackgroundListener;

    public DialogManager(Context context) {
        this.context = context;
    }

    public void load(IadtDialogBuilder builder){
        updateMode();
        dismiss();

        this.builder = builder;

        if (!canDrawOverlay()){
            Activity currentActivity = getCurrentActivity();
            if (currentActivity==null){
                return; // Will build when activity available (by listener)
            }
            currentActivityHash = currentActivity.toString();
            build(builder, currentActivity);
        }
        else{
            currentActivityHash = null;
            build(builder, IadtController.get().getContext());
        }

        if (IadtController.get().isDebug()){
            FriendlyLog.log("D", "Iadt", "Navigation",
                    "Dialog navigation to " + builder.getClass().getSimpleName());
        }
        show();
    }

    private boolean isLoaded() {
        return builder != null;
    }

    private void cleanLoaded() {
        builder = null;
        currentDialog = null;
        currentActivityHash = null;
        isManagerDismiss = false;
    }

    private boolean canDrawOverlay() {
        return PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY);
    }

    private Activity getCurrentActivity() {
        return IadtController.get().getEventManager().getActivityWatcher().getCurrentActivity();
    }

    //region [ DIALOG ]

    private void dismiss() {
        if (currentDialog!=null){// && currentDialog.isShowing()){
            isManagerDismiss = true;
            currentDialog.dismiss();
            currentActivityHash = null;
        }
    }

    private void build(IadtDialogBuilder builder, Context context) {
        currentDialog = builder.createDialog(context);
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

        if (canDrawOverlay()){
            currentDialog.getWindow().setType(Layer.getLayoutType());
        }
        //Else use default layout type for dialogs
    }

    private void show() {
        Log.d("DIALOGS", "Showing dialog");
        currentDialog.show();
    }

    public void onPause(){
        if(isLoaded() && isOverlayMode){
            currentDialog.getWindow().getDecorView().setVisibility(View.GONE);
        }
    }

    public void onResume(){
        if(isLoaded() && isOverlayMode){
            currentDialog.getWindow().getDecorView().setVisibility(View.VISIBLE);
        }
    }

    private void onActivityOpen(Activity activity) {
        if (isLoaded() && !isOverlayMode
                && (currentActivityHash == null || !currentActivityHash.equals(activity.toString()))){

            dismiss();
            currentActivityHash = activity.toString();
            build(builder, activity);
            if (IadtController.get().isDebug()){
                FriendlyLog.log("D", "Iadt", "Navigation",
                        "Dialog restored " + builder.getClass().getSimpleName());
            }
            show();
        }
    }

    private void onActivityDestroy(Activity activity) {
        if (isLoaded() && !isOverlayMode
                && currentActivityHash!=null && currentActivityHash.equals(activity.toString())){
            if (IadtController.get().isDebug()){
                FriendlyLog.log("D", "Iadt", "Navigation",
                        "Dialog destroyed " + builder.getClass().getSimpleName());
            }
            isManagerDismiss = true;
            dismiss();
        }
    }

    //endregion

    //region [ LISTENERS ]

    private void updateMode() {
        if (isOverlayMode == getOverlayMode()){
           return;
        }

        if (!getOverlayMode()){
            registerNonOverlayListener();
            unregisterOverlayListeners();
        }
        else{
            registerOverlayListeners();
            unregisterNonOverlayListener();
        }
        isOverlayMode = getOverlayMode();
    }

    private Boolean getOverlayMode(){
        return canDrawOverlay();
    }

    private EventManager getEventManager() {
        return IadtController.get().getEventManager();
    }

    private void registerNonOverlayListener() {
        activityOpenListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                onActivityOpen((Activity) param);
            }
        };
        getEventManager().subscribe(Event.ACTIVITY_ON_RESUME, activityOpenListener);

        activityStopListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                onActivityDestroy((Activity)param);
            }
        };
        getEventManager().subscribe(Event.ACTIVITY_ON_STOP, activityStopListener);
    }
    
    private void unregisterNonOverlayListener() {
        if (activityOpenListener!=null) {
            getEventManager().unsubscribe(Event.ACTIVITY_ON_RESUME, activityOpenListener);
            activityOpenListener = null;
        }
        if (activityStopListener!=null) {
            getEventManager().unsubscribe(Event.ACTIVITY_ON_STOP, activityStopListener);
            activityStopListener = null;
        }
    }

    public void registerOverlayListeners() {
        onForegroundListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                onResume();
            }
        };
        onBackgroundListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                onPause();
            }
        };
        getEventManager().subscribe(Event.FOREGROUND_CHANGE_ENTER, onForegroundListener);
        getEventManager().subscribe(Event.FOREGROUND_CHANGE_EXIT, onBackgroundListener);
    }

    public void unregisterOverlayListeners() {
        if (onForegroundListener!=null){
            getEventManager().unsubscribe(Event.FOREGROUND_CHANGE_ENTER, onForegroundListener);
            onForegroundListener = null;
        }
        if (onBackgroundListener!=null){
            getEventManager().unsubscribe(Event.FOREGROUND_CHANGE_EXIT, onBackgroundListener);
            onBackgroundListener = null;
        }
    }

    //endregion
}
