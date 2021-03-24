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
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;
import es.rafaco.inappdevtools.library.view.dialogs.IadtDialogBuilder;
import es.rafaco.inappdevtools.library.view.dialogs.ToolbarMoreDialog;
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

    private Boolean isOverlayMode;
    private EventManager.Listener orientationChangeListener;
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
                    "Dialog navigation to " + builder.getName());
        }
        show();
    }

    public void loadNonOverlay(IadtDialogBuilder builder){
        dismiss();
        Activity currentActivity = getCurrentActivity();
        if (currentActivity==null){
            return; // Will build when activity available (by listener)
        }
        currentActivityHash = currentActivity.toString();
        currentDialog = builder.createDialog(context);
        show();
    }

    private boolean isLoaded() {
        return builder != null;
    }



    private boolean canDrawOverlay() {
        return PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY);
    }

    private Activity getCurrentActivity() {
        return IadtController.get().getActivityTracker().getCurrent();
    }

    //region [ DIALOG ]

    public void dismiss() {
        if (currentDialog!=null && currentDialog.isShowing()){
            if (IadtController.get().isDebug()) {
                FriendlyLog.log("D", "Iadt", "Navigation",
                        "Dialog dismissed " + builder.getName());
            }
            currentDialog.setOnDismissListener(null);
            currentDialog.dismiss();
            currentActivityHash = null;
        }
    }

    public void destroy() {
        dismiss();
        if (IadtController.get().isDebug()) {
            FriendlyLog.log("D", "Iadt", "Navigation",
                    "Dialog destroy " + builder.getName());
        }
        cleanAll();
    }

    private void cleanAll() {
        builder = null;
        currentDialog = null;
        currentActivityHash = null;
    }

    public void onDialogDismissedExternally(DialogInterface dialog) {
        if (IadtController.get().isDebug()){
            FriendlyLog.log("D", "Iadt", "Navigation",
                    "Dialog dismissed externally, destroying.");
        }
        cleanAll();
    }

    private void build(IadtDialogBuilder builder, Context context) {
        currentDialog = builder.createDialog(context);

        if (canDrawOverlay()){
            currentDialog.getWindow().setType(Layer.getLayoutType());
        }
        //Else use default layout type for dialogs
    }

    private void show() {
        if (IadtController.get().isDebug()){
            FriendlyLog.log("D", "Iadt", "Navigation",
                    "Dialog show " + builder.getName());
        }
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

            if (IadtController.get().isDebug()){
                FriendlyLog.log("D", "Iadt", "Navigation",
                        "Dialog restoration over " + activity.toString() + " activity");
            }
            dismiss();
            currentActivityHash = activity.toString();
            build(builder, activity);

            show();
        }
    }

    private void onActivityDestroy(Activity activity) {
        if (isLoaded() && !isOverlayMode
                && currentActivityHash!=null && currentActivityHash.equals(activity.toString())){
            dismiss();
        }
    }

    private void onOrientationChanged(Event event) {
        if (isLoaded() && builder instanceof ToolbarMoreDialog){
            load(builder);
        }
    }

    //endregion

    //region [ LISTENERS ]

    private void updateMode() {
        if (isOverlayMode!=null && isOverlayMode.equals(getOverlayMode())){
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

        if (orientationChangeListener==null){
            registerOrientationListeners();
        }
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

    public void registerOrientationListeners() {
        orientationChangeListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                onOrientationChanged(event);
            }
        };
        getEventManager().subscribe(Event.ORIENTATION_LANDSCAPE, orientationChangeListener);
        getEventManager().subscribe(Event.ORIENTATION_PORTRAIT, orientationChangeListener);
    }

    //endregion
}
