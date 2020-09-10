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

package es.rafaco.inappdevtools.library.logic.log.filter;

import com.google.gson.Gson;

import es.rafaco.inappdevtools.library.storage.prefs.IadtPrefs;

public class LogFilterStore {

    public static final String PREF_VALUE_KEY = "LOG_FILTER";

    public static LogUiFilter get(){
        Gson gson = new Gson();
        String string = IadtPrefs.getString(PREF_VALUE_KEY, null);
        if (string == null){
            return null;
        }
        return gson.fromJson(string, LogUiFilter.class);
    }

    public static void store(LogUiFilter filter){
        Gson gson = new Gson();
        String json = gson.toJson(filter);
        IadtPrefs.setString(PREF_VALUE_KEY, json);
    }
}
