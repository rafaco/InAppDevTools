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

package es.rafaco.inappdevtools.library.view.overlay.screens.logcat;

import android.text.TextUtils;

import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;

public class LogcatFilterConfig {

    String presetFilter;
    String logFilter;
    String textFilter;

    public LogcatFilterConfig(String presetFilter, String logFilter, String textFilter) {
        this.presetFilter = presetFilter;
        this.logFilter = logFilter;
        this.textFilter = textFilter.toLowerCase();
    }

    public Boolean onlyMyPid(){
        return !presetFilter.equals("All");
    }

    public int pid(){
        if (onlyMyPid()){
            return ThreadUtils.myPid();
        }
        return -1;
    }

    public String tag(){
        if(presetFilter.equals("Cordova")){
            return "chromium";
        }else if(presetFilter.equals("Iadt")){
            return "Iadt";
        }else{
            return null;
        }
    }

    public Boolean validate(LogcatLine logLine) {

        //Filter by Level
        if (!LogcatLine.validateLevel(logLine.getLogLevel(), logFilter)) {
            return false;
        }

        //Filter by PID
        if (onlyMyPid()
                && logLine.getProcessId() != pid()) {
            return false;
        }
        //Filter by Tag
        if (tag() != null
                && !logLine.getTag().equals(tag())) {
            return false;
        }

        //Filter by text
        if (!TextUtils.isEmpty(textFilter)
                && !logLine.getLogOutput().toLowerCase().contains(textFilter)) {
            return false;
        }

        return true;
    }


}
