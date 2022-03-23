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

package org.inappdevtools.library.logic.events;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.inappdevtools.library.logic.events.detectors.app.AppEventDetector;
import org.inappdevtools.library.logic.events.detectors.app.ErrorAnrEventDetector;
import org.inappdevtools.library.logic.events.detectors.app.ForegroundChangeEventDetector;
import org.inappdevtools.library.logic.events.detectors.app.ForegroundEventDetector;
import org.inappdevtools.library.logic.events.detectors.device.AirplaneModeChangeEventDetector;
import org.inappdevtools.library.logic.events.detectors.device.ConnectivityChangeEventDetector;
import org.inappdevtools.library.logic.events.detectors.device.DeviceButtonsEventDetector;
import org.inappdevtools.library.logic.events.detectors.device.OrientationEventDetector;
import org.inappdevtools.library.logic.events.detectors.doze.DozeEventDetector;
import org.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import org.inappdevtools.library.logic.events.detectors.lifecycle.FragmentEventDetector;
import org.inappdevtools.library.logic.events.detectors.lifecycle.ProcessEventDetector;
import org.inappdevtools.library.logic.events.detectors.user.GestureEventDetector;
import org.inappdevtools.library.logic.events.detectors.user.ScreenChangeEventDetector;
import org.inappdevtools.library.logic.events.detectors.user.ShakeEventDetector;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.utils.ClassHelper;

public class EventDetectorsManager {

    private final Context context;
    private final EventManager eventManager;
    private List<EventDetector> eventDetectors = new ArrayList<>();

    public EventDetectorsManager(EventManager eventManager) {
        this.eventManager = eventManager;
        this.context = eventManager.getContext();

        //OldCrashInterceptor.initialise(context);

        initDetectors();
        startAll();
    }

    private void initDetectors() {
        // AppEventDetector should be the first one,
        // then ProcessEventDetector and then the other ones.
        initDetector(AppEventDetector.class);
        initDetector(ProcessEventDetector.class);

        initDetector(ForegroundChangeEventDetector.class);
        initDetector(ForegroundEventDetector.class);
        initDetector(ActivityEventDetector.class);
        initDetector(FragmentEventDetector.class);
        //TODO:
        //initDetector(ActivityTouchEventDetector.class);
        initDetector(OrientationEventDetector.class);
        initDetector(ErrorAnrEventDetector.class);
        initDetector(GestureEventDetector.class);
        initDetector(DeviceButtonsEventDetector.class);
        initDetector(ScreenChangeEventDetector.class);
        initDetector(ConnectivityChangeEventDetector.class);
        initDetector(AirplaneModeChangeEventDetector.class);
        initDetector(ShakeEventDetector.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            initDetector(DozeEventDetector.class);
        }
    }

    private void initDetector(Class<? extends EventDetector> className) {
        EventDetector eventDetector = new ClassHelper<EventDetector>().createClass(className,
                EventManager.class, eventManager);
        if (eventDetector != null){
            eventDetectors.add(eventDetector);
        }
    }

    private void startAll() {
        for (EventDetector eventDetector : eventDetectors) {
            if (IadtController.get().isDebug())
                Log.d(Iadt.TAG, "EventDetector started " + eventDetector.getClass().getSimpleName());
            eventDetector.start();
        }
    }

    private void stopAll() {
        if (eventDetectors==null || eventDetectors.isEmpty()){
            return;
        }
        if (IadtController.get().isDebug())
            Log.d(Iadt.TAG, "EventDetector stopping all detectors");
        for (EventDetector eventDetector : eventDetectors) {
            eventDetector.stop();
        }
    }

    public EventDetector get(Class<? extends EventDetector> className) {
        if (eventDetectors == null)
            return null;
        
        for (EventDetector eventDetector : eventDetectors) {
            if (eventDetector.getClass().equals(className)){
                return eventDetector;
            }
        }
        return  null;
    }

    public void destroy() {
        stopAll();
        eventDetectors = null;
    }
}
