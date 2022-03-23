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

package org.inappdevtools.library.logic.session;

//#ifdef ANDROIDX
//@import androidx.fragment.app.Fragment;
//#else
import android.support.v4.app.Fragment;
//#endif

import org.inappdevtools.library.logic.events.Event;
import org.inappdevtools.library.logic.utils.DateUtils;

public class FragmentTrack {

    public final String name;
    public final String className;
    public long fragmentUuid;
    public long activityUuid;
    public Event lastEvent;
    public long creationTime;
    public long startTime;
    public int startCount;
    public long resumeTime;
    public int resumeCount;

    public FragmentTrack(Fragment fragment, long fragmentUuid, long activityUuid) {
        this.name = fragment.getClass().getSimpleName();
        this.className = fragment.getClass().getName();
        this.fragmentUuid = fragmentUuid;
        this.activityUuid = activityUuid;
        this.creationTime = DateUtils.getLong();
        this.startCount = 0;
        this.resumeCount = 0;
    }

    public void onStart() {
        if (startCount==0){
            startTime = DateUtils.getLong();
        }
        startCount++;
    }

    public void onResume() {
        if (resumeCount==0){
            resumeTime = DateUtils.getLong();
        }
        resumeCount++;
    }

    public long getStartupTime() {
        return resumeTime - creationTime;
    }

    public String getFormattedLastEvent() {
        String eventName = lastEvent.toString();
        return eventName.replace("FRAGMENT_", "");
    }
}
