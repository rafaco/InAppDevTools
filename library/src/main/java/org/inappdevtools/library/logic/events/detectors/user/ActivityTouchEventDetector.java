/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package org.inappdevtools.library.logic.events.detectors.user;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;

//TODO: Review this touch watcher.
// It seems not used, only print to logcat
public class ActivityTouchEventDetector extends EventDetector {

    public ActivityTouchEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.ACTIVITY_ON_RESUME, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                addTouchListener((Activity) param);
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_PAUSE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                removeTouchListener((Activity) param);
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private void addTouchListener(final Activity activity) {
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(Iadt.TAG, "Resumed activity without decorView");
            return;
        }
        decorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(Iadt.TAG, "Click X:" + event.getX() + " Y:" + event.getY() + " at " + activity.getClass().getSimpleName() +
                        " - " + v.getClass().getSimpleName() + ": " + getResourceName(v, activity));
                return false;
            }
        });
    }

    private void removeTouchListener(Activity activity) {
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(Iadt.TAG, "Paused activity without decorView");
            return;
        }
        decorView.setOnTouchListener(null);
    }

    private String getResourceName(View v, Activity activity) {
        try{
            return activity.getResources().getResourceName(v.getId());
        }catch (Resources.NotFoundException e){
            return "[ not set ]";
        }
    }
}
