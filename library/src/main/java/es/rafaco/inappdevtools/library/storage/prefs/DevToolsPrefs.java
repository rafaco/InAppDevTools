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

package es.rafaco.inappdevtools.library.storage.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import es.rafaco.inappdevtools.library.IadtController;

public class DevToolsPrefs {

    public static final String SHARED_PREFS_KEY = "iadt";

    public static boolean contains(String key){
        return getPrefs().contains(key);
    }

    public static String getString(String key, String defValue){
        return getPrefs().getString(key, defValue);
    }

    public static void setString(String key, String value){
        getPrefs().edit().putString(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue){
        return getPrefs().getBoolean(key, defValue);
    }

    public static void setBoolean(String key, boolean value){
        getPrefs().edit().putBoolean(key, value).apply();
    }

    public static int getInt(String key, int defValue){
        return getPrefs().getInt(key, defValue);
    }

    public static void setInt(String key, int value){
        getPrefs().edit().putInt(key, value).apply();
    }

    public static long getLong(String key, long defValue){
        return getPrefs().getLong(key, defValue);
    }

    public static void setLong(String key, long value){
        getPrefs().edit().putLong(key, value).apply();
    }

    private static SharedPreferences getPrefs() {
        return getContext().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }

    private static Context getContext() {
        return IadtController.get().getContext();
    }

    public static void deleteAll() {
        getPrefs().edit().clear().commit();
    }

    public static String getOverview() {
        return "SharedPrefs " + SHARED_PREFS_KEY + " contains " + getPrefs().getAll().size() + " entries.";
    }
}
