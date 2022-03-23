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

import android.content.Context;
import android.os.Bundle;
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.fragment.app.Fragment;
//@import androidx.fragment.app.FragmentManager;
//#else
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
//#endif

import org.inappdevtools.library.logic.session.FragmentTracker;
import org.inappdevtools.library.logic.session.FragmentUUID;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.events.EventManager;
import org.inappdevtools.library.logic.log.FriendlyLog;

public class SupportFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

    @Override
    public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
        long uuid = FragmentUUID.onFragmentPreAttached(f);
        trackAndFire(Event.FRAGMENT_PRE_ATTACHED, f, uuid);
        friendlyLog("V","PreAttach", fm, f);
        super.onFragmentPreAttached(fm, f, context);
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_ATTACHED, f, uuid);
        friendlyLog("D","Attach", fm, f);
        super.onFragmentAttached(fm, f, context);
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_CREATED, f, uuid);
        friendlyLog("D","Create", fm, f);
        super.onFragmentCreated(fm, f, savedInstanceState);
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_ACTIVITY_CREATED, f, uuid);
        friendlyLog("V","ActivityCreate", fm, f);
        super.onFragmentActivityCreated(fm, f, savedInstanceState);
    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_VIEW_CREATED, f, uuid);
        friendlyLog("V","ViewCreate", fm, f);
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_STARTED, f, uuid);
        friendlyLog("D","Start", fm, f);
        super.onFragmentStarted(fm, f);
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_RESUMED, f, uuid);
        friendlyLog("D","Resume", fm, f);

        //TODO: restore to info when Fragment Navigation features
        //TODO: skip on rotation like with activities
        friendlyLog("D","Shown", fm, f);

        super.onFragmentResumed(fm, f);
    }

    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_PAUSED, f, uuid);
        friendlyLog("V","Pause", fm, f);
        super.onFragmentPaused(fm, f);
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_STOPPED, f, uuid);
        friendlyLog("D","Stop", fm, f);
        super.onFragmentStopped(fm, f);
    }

    @Override
    public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_SAVE_INSTANCE, f, uuid);
        friendlyLog("V","SaveInstanceState", fm, f);
        super.onFragmentSaveInstanceState(fm, f, outState);
    }

    @Override
    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_VIEW_DESTROY, f, uuid);
        friendlyLog("V","ViewDestroyed", fm, f);
        super.onFragmentViewDestroyed(fm, f);
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_DESTROY, f, uuid);
        friendlyLog("D","Destroyed", fm, f);
        super.onFragmentDestroyed(fm, f);
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        long uuid = FragmentUUID.onLifecycle(f);
        trackAndFire(Event.FRAGMENT_DETACHED, f, uuid);
        friendlyLog("D","Detach", fm, f);
        super.onFragmentDetached(fm, f);
    }


    private void trackAndFire(Event fragmentEvent, Fragment fragment, long uuid) {
        //Log.d("DEMO_fragment", "trackAndFire at " + fragmentEvent.getName() + "for Fragment " + uuid);
        getTracker().track(fragmentEvent, fragment, uuid);
        getEventManager().fire(fragmentEvent, fragment);
    }

    private EventManager getEventManager() {
        return IadtController.get().getEventManager();
    }

    private FragmentTracker getTracker() {
        return IadtController.get().getFragmentTracker();
    }

    public static void friendlyLog(String severity, String type, FragmentManager fm, Fragment f) {
        String message = "Fragment " + type.toLowerCase() + ": "
                + f.getClass().getSimpleName() + " at "
                + f.getActivity().getClass().getSimpleName()
                + " [" + f.toString() + "]";
        FriendlyLog.log(severity, "Fragment", type, message);
    }
}
