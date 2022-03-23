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

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AppCompatActivity;
//@import androidx.fragment.app.FragmentManager;
//#else
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
//#endif

import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventDetector;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class FragmentEventDetector extends EventDetector {

    private SupportFragmentLifecycleCallbacks supportFragmentCallbacks;
    //private FragmentLifecycleCallbacks fragmentCallbacks;

    public FragmentEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.ACTIVITY_ON_CREATE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                registerFragmentLifecycleCallbacks((Activity)param);
            }
        });

        eventManager.subscribe(Event.ACTIVITY_ON_DESTROY, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                unregisterFragmentLifecycleCallbacks((Activity)param);
            }
        });
    }

    @Override
    public void start() {
        //Intentionally empty
    }

    @Override
    public void stop() {
        //Intentionally empty.
    }

    //region [ DETECTOR ]

    private void registerFragmentLifecycleCallbacks(Activity activity) {
        if (activity instanceof AppCompatActivity){
            if (supportFragmentCallbacks==null)
                this.supportFragmentCallbacks = new SupportFragmentLifecycleCallbacks();

            FragmentManager supportFragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            supportFragmentManager.registerFragmentLifecycleCallbacks(supportFragmentCallbacks, true);

            String message = "FragmentCallbacks registered: "
                    + activity.getClass().getSimpleName()
                    + " - " + supportFragmentManager.toString();
            FriendlyLog.log("D", "Fragment", "CallbacksRegistered", message);
        }
        else{
            FriendlyLog.log("W", "Fragment", "CallbacksRegistered", "Unregistered: not an AppCompatActivity");
        }

        //TODO
        /*else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if (fragmentCallbacks==null)
                this.fragmentCallbacks = new FragmentLifecycleCallbacks();

            activity.getFragmentManager()
                    .registerFragmentLifecycleCallbacks(fragmentCallbacks, true);
        }*/
    }

    private void unregisterFragmentLifecycleCallbacks(Activity activity){
        if (activity instanceof AppCompatActivity){
            FragmentManager supportFragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            supportFragmentManager.unregisterFragmentLifecycleCallbacks(supportFragmentCallbacks);

            String message = "FragmentCallbacks unregistered: "
                    + activity.getClass().getSimpleName()
                    + " - " + supportFragmentManager.toString();
            FriendlyLog.log("V", "Fragment", "CallbacksUnregistered", message);
        }

        //TODO
        /*else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           activity.getFragmentManager()
                    .unregisterFragmentLifecycleCallbacks(fragmentCallbacks);
        }*/
    }

    //endregion
}
