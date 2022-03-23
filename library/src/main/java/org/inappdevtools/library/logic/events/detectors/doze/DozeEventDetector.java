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

package org.inappdevtools.library.logic.events.detectors.doze;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;
//#ifdef ANDROIDX
//@import androidx.annotation.RequiresApi;
//#else
import android.support.annotation.RequiresApi;
//#endif

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

import static android.content.Context.POWER_SERVICE;

public class DozeEventDetector extends EventDetector {

    private IntentFilter mFilter;
    private InnerReceiver mReceiver;

    public DozeEventDetector(EventManager manager) {
        super(manager);

        mFilter = new IntentFilter();
        mFilter.addAction(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.POWER_INTERACTIVE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("W", "Device", "PowerInteractive",
                        "PowerManager: interactive mode");
            }
        });

        eventManager.subscribe(Event.POWER_DOZE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("W", "Device", "PowerDoze",
                        "PowerManager: doze mode");
            }
        });

        eventManager.subscribe(Event.POWER_SAVE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("W", "Device", "PowerSave",
                        "PowerManager: save mode");
            }
        });
    }

    @Override
    public void start() {
        if (mReceiver != null) {
            getContext().registerReceiver(mReceiver, mFilter);
        }
    }

    @Override
    public void stop() {
        if (mReceiver != null) {
            getContext().unregisterReceiver(mReceiver);
        }
    }

   class InnerReceiver extends BroadcastReceiver {

       private PowerManager powerManager;
       private PowerManager.WakeLock wakeLock;

       @RequiresApi(api = Build.VERSION_CODES.M)
       @Override
        public void onReceive(final Context context, final Intent intent) {
           try {
               powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
               wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                       Iadt.TAG + ":DozeDetector");
               wakeLock.acquire(30 * 1000L);

               if (powerManager.isDeviceIdleMode()) {
                   eventManager.fire(Event.POWER_DOZE);
               }
               else if (powerManager.isInteractive()){
                   eventManager.fire(Event.POWER_INTERACTIVE);
               }
               else if (powerManager.isPowerSaveMode()) {
                   eventManager.fire(Event.POWER_SAVE);
               }
               wakeLock.release();
               powerManager = null;
               wakeLock = null;
           }
           catch (Exception ex) {
               FriendlyLog.logException("Exception at DozeEventDetector receiver", ex);
               if (wakeLock != null) {
                   wakeLock.release();
                   powerManager = null;
                   wakeLock = null;
               }
           }
        }
    }
}
