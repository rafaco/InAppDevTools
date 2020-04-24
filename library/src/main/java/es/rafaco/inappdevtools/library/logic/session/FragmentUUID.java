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

package es.rafaco.inappdevtools.library.logic.session;

//#ifdef ANDROIDX
//@import androidx.fragment.app.Fragment;
//#else
import android.support.v4.app.Fragment;
//#endif

import android.os.Bundle;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

//Idea extracted from https://stackoverflow.com/a/50791431/10209414
public class FragmentUUID {

    private static final String PREFS_KEY = "FragmentUUID";
    private static final long INVALID_UUID = 0;

    private static long _lastUUID = INVALID_UUID;

    public static long onFragmentPreAttached(Fragment fragment) {
        long uuid = readFromBundle(fragment.getArguments());
        writeOnBundle(fragment, uuid);
        return uuid;
    }

    public static long onLifecycle(Fragment fragment) {
        return readFromBundle(fragment.getArguments());
    }

    private static void writeOnBundle(Fragment fragment, long uuid) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        bundle.putLong(PREFS_KEY, uuid);
    }

    public static long readFromBundle(Bundle bundle) {
        long result = (bundle != null) ? bundle.getLong(PREFS_KEY, INVALID_UUID) : INVALID_UUID;
        return (result != INVALID_UUID) ? result : generateUUID();
    }

    private static long generateUUID() {
         _lastUUID = DevToolsPrefs.getLong(PREFS_KEY, INVALID_UUID);
        long result = ++_lastUUID;
        if (result != INVALID_UUID) {
            DevToolsPrefs.setLong(PREFS_KEY, result);
        } else {
            throw new RuntimeException("You have somehow managed to create a total of 9223372036854775807 fragments during lifetime of your app! =)");
        }
        return result;
    }
}
