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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.config.BuildConfigField;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class ShakeEventDetector extends EventDetector {

    private final SensorManager sensorManager;
    private final Sensor sensor;
    private InnerReceiver mReceiver;

    public ShakeEventDetector(EventManager manager) {
        super(manager);

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.SHAKE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "Shake", "Shake detected");
                IadtController iadtController = IadtController.get();
                if (!iadtController.isPendingInitFull &&
                        iadtController.getConfig().getBoolean(BuildConfigField.INVOCATION_BY_SHAKE)){
                    IadtController.get().getOverlayHelper().showToggle();
                }
            }
        });

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
    }

    @Override
    public void start() {
        sensorManager.registerListener(mReceiver, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void stop() {
        sensorManager.unregisterListener(mReceiver);
    }

    class InnerReceiver implements SensorEventListener {

        private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
        private static final int SHAKE_SLOP_TIME_MS = 500;
        private long mShakeTimestamp;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }
                mShakeTimestamp = now;
                eventManager.fire(Event.SHAKE);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
