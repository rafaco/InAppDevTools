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

package es.rafaco.inappdevtools.library.storage.prefs.utils;

import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class DatabaseVersionUtil {

    public static final String PREF_VALUE_KEY = "DATABASE_VERSION";

    public static int get(){
        return DevToolsPrefs.getInt(PREF_VALUE_KEY, -1);
    }

    public static void set(int value){
        DevToolsPrefs.setInt(PREF_VALUE_KEY, value);
    }
}
