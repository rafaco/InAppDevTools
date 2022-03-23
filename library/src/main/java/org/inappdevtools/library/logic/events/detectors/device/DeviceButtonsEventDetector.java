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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class DeviceButtonsEventDetector extends EventDetector {

    private IntentFilter mFilter;
    private InnerReceiver mReceiver;

    public DeviceButtonsEventDetector(EventManager manager) {
        super(manager);

        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mReceiver = new InnerReceiver();
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.DEVICE_HOME_PRESSED, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "HomeKey", "Pressed home button");
            }
        });

        eventManager.subscribe(Event.DEVICE_RECENT_PRESSED, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "RecentKey", "Pressed recent button");
            }
        });

        eventManager.subscribe(Event.DEVICE_DREAM_PRESSED, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "DreamKey", "Pressed off button");
            }
        });

        eventManager.subscribe(Event.DEVICE_UNKNOWN_PRESSED, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "User", "UnknownKey", "Pressed Unknown button: " + param);
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
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_DREAM_KEY = "dream";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (Iadt.isDebug()) Log.v(Iadt.TAG,
                        "DeviceButtonsEventDetector - action:" + action + ", reason:" + reason);

                if (reason == null) {
                    return;
                }
                else if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    eventManager.fire(Event.DEVICE_HOME_PRESSED);
                }
                else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    eventManager.fire(Event.DEVICE_RECENT_PRESSED);
                }
                else if (reason.equals(SYSTEM_DIALOG_REASON_DREAM_KEY)) {
                    eventManager.fire(Event.DEVICE_DREAM_PRESSED);
                }
                else{
                    eventManager.fire(Event.DEVICE_UNKNOWN_PRESSED, reason);
                }
            }
        }
    }
}
