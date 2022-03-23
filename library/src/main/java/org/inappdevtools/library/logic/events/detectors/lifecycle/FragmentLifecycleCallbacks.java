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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.annotation.RequiresApi;
//@import androidx.fragment.app.Fragment;
//@import androidx.fragment.app.FragmentManager;
//#else
import android.support.annotation.RequiresApi;
import android.app.Fragment;
import android.app.FragmentManager;
//#endif

import org.inappdevtools.library.logic.log.FriendlyLog;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

    @Override
    public void onFragmentPreCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentPreCreated(fm, f, savedInstanceState);
        friendlyLog("D","PreCreated", fm, f);
    }

    @Override
    public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentPreAttached(fm, f, context);
        friendlyLog("D","PreAttached", fm, f);
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentAttached(fm, f, context);
        friendlyLog("D","Attached", fm, f);
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentCreated(fm, f, savedInstanceState);
        friendlyLog("D","Created", fm, f);
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState);
        friendlyLog("D","ActivityCreated", fm, f);
    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);
        friendlyLog("D","ViewCreated", fm, f);
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        super.onFragmentStarted(fm, f);
        friendlyLog("D","Started", fm, f);
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        super.onFragmentResumed(fm, f);
        friendlyLog("D","Resumed", fm, f);
    }

    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        super.onFragmentPaused(fm, f);
        friendlyLog("D","Paused", fm, f);
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        super.onFragmentStopped(fm, f);
        friendlyLog("D","Stopped", fm, f);
    }

    @Override
    public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
        super.onFragmentSaveInstanceState(fm, f, outState);
        friendlyLog("D","SaveInstanceState", fm, f);
    }

    @Override
    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentViewDestroyed(fm, f);
        friendlyLog("D","ViewDestroyed", fm, f);
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentDestroyed(fm, f);
        friendlyLog("D","Destroyed", fm, f);
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        super.onFragmentDetached(fm, f);
        friendlyLog("D","Detached", fm, f);
    }


    public static void friendlyLog(String severity, String type, FragmentManager fm, Fragment f) {
        String message = "Fragment " + type.toLowerCase() + ": "
                + f.getClass().getSimpleName() + " at "
                + f.getActivity().getClass().getSimpleName();
        FriendlyLog.log(severity, "Fragment", type, message);
    }
}
