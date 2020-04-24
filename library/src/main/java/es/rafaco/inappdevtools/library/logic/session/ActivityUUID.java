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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

//Idea extracted from https://stackoverflow.com/a/50791431/10209414
public class ActivityUUID {

    private static final String PREFS_KEY = "ActivityUUID";
    private static final long INVALID_UUID = 0;

    private static long _lastUUID = INVALID_UUID;

    public static long onActivityCreated(Activity activity, Bundle savedInstanceState) {
        long uuid = readFromSavedInstanceBundle(savedInstanceState);
        writeOnActivityBundle(activity, uuid);
        return uuid;
    }

    public static long onLifecycle(Activity activity) {
        return readFromActivityBundle(activity);
    }

    public static long onActivitySaveInstanceState(Activity activity, Bundle outState) {
        long uuid = readFromActivityBundle(activity);
        writeOnSavedInstanceState(outState, uuid);
        return uuid;
    }

    private static void writeOnSavedInstanceState(Bundle outState, long uuid) {
        outState.putLong(PREFS_KEY, uuid);
    }

    private static void writeOnActivityBundle(Activity activity, long uuid) {
        activity.getIntent().putExtra(PREFS_KEY, uuid);
    }

    public static long readFromSavedInstanceBundle(Bundle savedInstanceState) {
        long result = (savedInstanceState != null) ? savedInstanceState.getLong(PREFS_KEY, INVALID_UUID) : INVALID_UUID;
        return (result != INVALID_UUID) ? result : generateUUID();
    }

    public static long readFromActivityBundle(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent == null) return INVALID_UUID;
        Bundle extras = intent.getExtras();
        if (extras == null) return INVALID_UUID;
        return extras.getLong(PREFS_KEY, INVALID_UUID);
    }

    private static long generateUUID() {
         _lastUUID = DevToolsPrefs.getLong(PREFS_KEY, INVALID_UUID);
        long result = ++_lastUUID;
        if (result != INVALID_UUID) {
            DevToolsPrefs.setLong(PREFS_KEY, result);
        } else {
            throw new RuntimeException("You have somehow managed to create a total of 9223372036854775807 activities during lifetime of your app! =)");
        }
        return result;
    }
}
