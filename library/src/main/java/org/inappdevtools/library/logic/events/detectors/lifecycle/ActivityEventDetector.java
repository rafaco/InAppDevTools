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

package org.inappdevtools.library.logic.events.detectors.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import org.inappdevtools.library.logic.session.ActivityTracker;
import org.inappdevtools.library.logic.session.ActivityUUID;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class ActivityEventDetector extends EventDetector implements Application.ActivityLifecycleCallbacks {

    private final ActivityTracker tracker;

    public ActivityEventDetector(EventManager eventManager) {
        super(eventManager);
        tracker = IadtController.get().getActivityTracker();
    }

    @Override
    public void subscribe() {
        //Intentionally empty
    }

    @Override
    public void start() {
        Application app = (Application)getContext().getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void stop() {
        Application app = (Application)getContext().getApplicationContext();
        app.unregisterActivityLifecycleCallbacks(this);
    }


    //region [ DETECTION ]

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        long uuid = ActivityUUID.onActivityCreated(activity, savedInstanceState);
        logEvent("D","Create", (Activity) activity);
        trackAndFire(Event.ACTIVITY_ON_CREATE, activity, uuid);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        long uuid = ActivityUUID.onLifecycle(activity);
        logEvent("D","Start", activity);
        trackAndFire(Event.ACTIVITY_ON_START, activity, uuid);
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        long uuid = ActivityUUID.onLifecycle(activity);
        logEvent("V","Resume", activity);
        trackAndFire(Event.ACTIVITY_ON_RESUME, activity, uuid);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        long uuid = ActivityUUID.onLifecycle(activity);
        logEvent("V","Pause",activity);
        trackAndFire(Event.ACTIVITY_ON_PAUSE, activity, uuid);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        long uuid = ActivityUUID.onLifecycle(activity);
        logEvent("D","Stop", activity);
        trackAndFire(Event.ACTIVITY_ON_STOP, activity, uuid);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        long uuid = ActivityUUID.onActivitySaveInstanceState(activity, outState);
        logEvent("V","SaveInstanceState", activity);
        trackAndFire(Event.ACTIVITY_ON_SAVE_INSTANCE, activity, uuid);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        long uuid = ActivityUUID.onLifecycle(activity);
        logEvent("D","Destroy", activity);
        trackAndFire(Event.ACTIVITY_ON_DESTROY, activity, uuid);
    }

    //endregion

    private void trackAndFire(Event activityEvent, Activity activity, long uuid) {
        //Log.d("DEMO_activity", "trackAndFire at " + activityEvent.getName() + ". UUID is " + uuid);
        tracker.track(activityEvent, activity, uuid);
        eventManager.fire(activityEvent, activity);
    }

    public void logEvent(String severity, String type, Activity activity) {
        String message = "Activity " + type.toLowerCase()
                + ": " + activity.getClass().getSimpleName();
        FriendlyLog.log(severity, "Activity", type, message);
    }
}
