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

package org.inappdevtools.library.logic.events.detectors.device;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

import static android.content.Context.SENSOR_SERVICE;

public class OrientationEventDetector extends EventDetector implements SensorEventListener{

    SensorManager sm;
    private int previousOrientation = -1;

    public OrientationEventDetector(EventManager eventManager) {
        super(eventManager);
        sm = (SensorManager) eventManager.getContext().getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.ACTIVITY_ON_RESUME, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                start();
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_PAUSE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                stop();
            }
        });

        //Log initial orientation
        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (previousOrientation == -1){
                    previousOrientation = getOrientation();
                    String orientation = getOrientationString();
                    FriendlyLog.log("D", "Device", orientation,
                            "Orientation is " + orientation.toLowerCase());
                }
            }
        });

        //Log orientation changes
        eventManager.subscribe(Event.ORIENTATION_PORTRAIT, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Device", "Portrait",
                        "Orientation changed to portrait");
            }
        });
        eventManager.subscribe(Event.ORIENTATION_LANDSCAPE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Device", "Landscape",
                        "Orientation changed to landscape");
            }
        });
    }

    @Override
    public void start() {
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void stop() {
        sm.unregisterListener(this, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int detectedOrientation = getOrientation();
        if (detectedOrientation == Configuration.ORIENTATION_PORTRAIT
                && previousOrientation != detectedOrientation) {

            previousOrientation = detectedOrientation;
            eventManager.fire(Event.ORIENTATION_PORTRAIT);
        }
        else if (detectedOrientation == Configuration.ORIENTATION_LANDSCAPE
                && previousOrientation != detectedOrientation) {

            previousOrientation = detectedOrientation;
            eventManager.fire(Event.ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public static int getOrientation(){
        Context context = IadtController.get().getContext();
        return context.getResources().getConfiguration().orientation;
    }

    public static String getOrientationString(){
        return (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) ? "Landscape" : "Portrait";
    }
}
