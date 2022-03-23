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

package org.inappdevtools.library.logic.session;

import android.content.Context;

//#ifdef ANDROIDX
//@import androidx.fragment.app.Fragment;
//#else
import android.support.v4.app.Fragment;
//#endif

import java.util.LinkedHashMap;

import org.inappdevtools.library.logic.events.Event;

public class FragmentTracker {

    Context context;
    private final ActivityTracker activityTracker;
    private LinkedHashMap<Long, LinkedHashMap<Long, FragmentTrack>> history;

    public FragmentTracker(Context context, ActivityTracker activityTracker) {
        this.context = context;
        this.activityTracker = activityTracker;
    }

    public void track(Event event, Fragment fragment, long fragmentUuid) {
        long activityUuid = ActivityUUID.readFromActivityBundle(fragment.getActivity());
        if (event.equals(Event.FRAGMENT_PRE_ATTACHED)) {
            FragmentTrack track = new FragmentTrack(fragment, fragmentUuid, activityUuid);
            addHistory(track);
        }

        getHistory(activityUuid, fragmentUuid).lastEvent = event;
    }

    //region [ HISTORY LIST ]

    public void addHistory(FragmentTrack track) {
        if (history == null)
            history = new LinkedHashMap<>();
        if (!history.containsKey(track.activityUuid))
            history.put(track.activityUuid, new LinkedHashMap<Long, FragmentTrack>());
        history.get(track.activityUuid).put(track.fragmentUuid, track);
    }

    public void removeHistory(FragmentTrack track) {
        if (history != null
                && history.containsKey(track.activityUuid)
                && history.get(track.activityUuid).containsKey(track.fragmentUuid)) {
            history.get(track.activityUuid).remove(track.fragmentUuid);
        }
    }

    public LinkedHashMap<Long, FragmentTrack> getActivityHistory(long activityUuid) {
        if (history != null
                && history.containsKey(activityUuid)) {
            return history.get(activityUuid);
        }
        return null;
    }

    public FragmentTrack getHistory(long activityUuid, long fragmentUuid) {
        LinkedHashMap<Long, FragmentTrack> activityHistory = getActivityHistory(activityUuid);
        if (activityHistory != null
                && activityHistory.containsKey(fragmentUuid)) {
            return activityHistory.get(fragmentUuid);
        }
        return null;
    }

    //endregion

    public LinkedHashMap<Long, FragmentTrack> getCurrentActivityHistory(){
        long activityUuid = activityTracker.getCurrentHistory().uuid;
        return getActivityHistory(activityUuid);
    }
}

