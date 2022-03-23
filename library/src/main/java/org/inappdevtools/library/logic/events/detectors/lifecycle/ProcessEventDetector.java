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

package org.inappdevtools.library.logic.events.detectors.lifecycle;

//#ifdef ANDROIDX
//@import androidx.lifecycle.Lifecycle;
//@import androidx.lifecycle.LifecycleObserver;
//@import androidx.lifecycle.OnLifecycleEvent;
//@import androidx.lifecycle.ProcessLifecycleOwner;
//#else
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
//#endif

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class ProcessEventDetector extends EventDetector implements LifecycleObserver {

    public ProcessEventDetector(EventManager manager) {
        super(manager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.PROCESS_ON_CREATE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Create", "Process created");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_START, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Start", "Process started");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_STOP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Stop", "Process stopped");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_RESUME, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Resume", "Process resumed");
                IadtController.get().initFullIfPending();
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_PAUSE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Pause", "Process paused");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_DESTROY, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I", "Process", "Destroy", "Process destroyed");
            }
        });
    }

    @Override
    public void start() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void stop() {
        //ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);
    }



    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        eventManager.fire(Event.PROCESS_ON_CREATE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        eventManager.fire(Event.PROCESS_ON_START);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        eventManager.fire(Event.PROCESS_ON_STOP);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        eventManager.fire(Event.PROCESS_ON_RESUME);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        eventManager.fire(Event.PROCESS_ON_PAUSE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        eventManager.fire(Event.PROCESS_ON_DESTROY);
    }
}
