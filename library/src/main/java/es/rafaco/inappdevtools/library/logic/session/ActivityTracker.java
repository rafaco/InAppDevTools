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

package es.rafaco.inappdevtools.library.logic.session;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.LinkedHashMap;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ActivityTracker {

    Context context;
    private LinkedHashMap<Long, ActivityTrack> history;
    protected int startedActivities = 0;
    protected boolean isInBackground = true;
    private long currentActivityUuid;
    private Activity currentActivity = null;

    public ActivityTracker(Context context) {
        this.context = context;
    }

    public Activity getCurrent() {
        return currentActivity;
    }

    public String getCurrentName() {
        ActivityTrack currentHistory = getCurrentHistory();
        if (currentHistory==null)
            return null;
        return currentHistory.name;
    }

    public boolean isInBackground(){
        return isInBackground;
    }

    private EventManager getEventManager(){
        return IadtController.get().getEventManager();
    }

    public void track(Event event, Activity activity, long uuid){
        if (event.equals(Event.ACTIVITY_ON_CREATE)){
            if (getHistory(uuid) != null){
                Log.w("ACTIVITY_TRACKER", "ACTIVITY_ON_CREATE: UUID exist already on history");
            }
            ActivityTrack activityTrack = new ActivityTrack(activity, uuid);
            addHistory(activityTrack);
        }
        else if (event.equals(Event.ACTIVITY_ON_START)){
            getHistory(uuid).onStart();
            
            startedActivities++;
            isInBackground = (startedActivities == 0);
        }
        else if (event.equals(Event.ACTIVITY_ON_RESUME)){
            getHistory(uuid).onResume();

            long previousUuid = currentActivityUuid;
            currentActivityUuid = uuid;
            currentActivity = activity;
            if (currentActivityUuid != previousUuid){
                getEventManager().fire(Event.APP_NAVIGATION, getCurrentName());
            }
        }
        else if (event.equals(Event.ACTIVITY_ON_STOP)){
            startedActivities--;
            isInBackground = (startedActivities == 0);
        }

        getHistory(uuid).lastEvent = event;
    }

    //region [ HISTORY LIST ]

    public void addHistory(ActivityTrack track){
        if (history == null)
            history = new LinkedHashMap<>();
        history.put(track.uuid, track);
    }

    public void removeHistory(ActivityTrack track){
        if (history !=null && history.containsKey(track.uuid)) {
            history.remove(track.uuid);
        }
    }

    public ActivityTrack getHistory(long uuid){
        if (history ==null || !history.containsKey(uuid))
            return null;
        return history.get(uuid);
    }

    //endregion

    public int getCurrentActivityInstanceCount(){
        ActivityTrack current = getCurrentHistory();
        int count = 0;
        for (ActivityTrack track : history.values()) {
            if (track.name.equals(current.name)){
                count++;
            }
        }
        return count;
    }

    public ActivityTrack getCurrentHistory() {
        return getHistory(currentActivityUuid);
    }

    public String getCurrentActivityStartupTime(){
        return Humanizer.getDuration(getCurrentHistory().getStartupTime());
    }

    public String getCurrentActivityCreationElapsed(){
        return Humanizer.getElapsedTime(getCurrentHistory().creationTime);
    }

    public String getCurrentActivityPackage() {
        return Humanizer.packageFromClass(getCurrent().getClass());
    }

    public String getCurrentActivityLastEvent() {
        return getCurrentHistory().getFormattedLastEvent();
    }
}
