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

package org.inappdevtools.library.logic.events.detectors.app;

import android.app.Activity;

import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;

public class ForegroundChangeEventDetector extends EventDetector {

    private EventManager.Listener onForegroundListener;
    private EventManager.Listener onBackgroundListener;

    private boolean isForeground = true;
    private boolean isSuspended = false;

    public ForegroundChangeEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        onForegroundListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (!isForeground && isSuspended) {
                    isForeground = true;
                    isSuspended = false;
                    eventManager.fire(Event.FOREGROUND_CHANGE_ENTER, (Activity) param);
                }
            }
        };
        onBackgroundListener = new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (isForeground) {
                    isForeground = false;
                    isSuspended = true;
                    eventManager.fire(Event.FOREGROUND_CHANGE_EXIT);
                }
            }
        };
        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, onForegroundListener);
        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, onBackgroundListener);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
