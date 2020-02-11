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

package es.rafaco.inappdevtools.library.storage.prefs.utils;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildInfo;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.AssetFileReader;
import es.rafaco.inappdevtools.library.storage.files.utils.JsonHelper;
import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class NewBuildUtil {

    public static final String LAST_BUILD_TIME = "LAST_BUILD_TIME";
    public static final String BUILD_INFO_SHOWN = "BUILD_INFO_SHOWN";
    public static final String BUILD_INFO_SKIPPED = "BUILD_INFO_SKIPPED";
    public static Boolean isNewBuildOnMemory;
    public static Long buildTimeOnMemory;

    public static boolean isNewBuild(){
        if (isNewBuildOnMemory == null){
            update();
        }
        return isNewBuildOnMemory;
    }

    public static long getBuildTime(){
        if (buildTimeOnMemory == null){
            update();
        }
        return buildTimeOnMemory;
    }

    private static void update(){

        long lastBuildTime = DevToolsPrefs.getLong(LAST_BUILD_TIME, -1);
        String fileContents = new AssetFileReader(IadtController.get().getContext())
                .getFileContents(IadtPath.BUILD_INFO);
        JsonHelper buildInfo = new JsonHelper(fileContents);
        long currentBuildTime = buildInfo.getLong(BuildInfo.BUILD_TIME);

        if (lastBuildTime<0){  //First start
            isNewBuildOnMemory = true;
            buildTimeOnMemory = currentBuildTime;
            storeBuildTime();
        }
        else if (lastBuildTime == currentBuildTime) {
            isNewBuildOnMemory = false;
            buildTimeOnMemory = currentBuildTime;
        }
        else{
            isNewBuildOnMemory = true;
            buildTimeOnMemory = currentBuildTime;
            storeBuildTime();
            clearBuildInfoShown();
        }
    }

    private static void storeBuildTime(){
        DevToolsPrefs.setLong(LAST_BUILD_TIME, buildTimeOnMemory);
    }



    public static boolean isBuildInfoShown(){
        return DevToolsPrefs.getBoolean(BUILD_INFO_SHOWN, false);
    }

    public static void saveBuildInfoShown(){
        DevToolsPrefs.setBoolean(BUILD_INFO_SHOWN, true);
    }

    public static boolean isBuildInfoSkipped(){
        return DevToolsPrefs.getBoolean(BUILD_INFO_SKIPPED, false);
    }

    public static void saveBuildInfoSkip() {
        DevToolsPrefs.setBoolean(BUILD_INFO_SKIPPED, true);
    }

    private static void clearBuildInfoShown() {
        DevToolsPrefs.setBoolean(BUILD_INFO_SHOWN, false);
    }
}
