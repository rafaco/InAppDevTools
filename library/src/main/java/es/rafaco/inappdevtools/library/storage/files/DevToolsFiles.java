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

package es.rafaco.inappdevtools.library.storage.files;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.rafaco.inappdevtools.library.storage.files.utils.FileCreator;

@Deprecated
//Remove in progress
public class DevToolsFiles {

    public static String storeInfo(String report, long timeMillis) {
        return FileCreator.withContent(
                "info",
                "info_" + timeMillis + ".txt",
                report);
    }

    public static String storeSources(String report, long timeMillis) {
        return FileCreator.withContent(
                "sources",
                "sources_" + timeMillis + ".txt",
                report);
    }

    public static File prepareLogcat(long timeMillis) {
        return FileCreator.prepare(
                "logcat",
                "logcat_" + timeMillis + ".txt");
    }

    public static File prepareDatabase(String dbName, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String formattedTime = sdf.format(new Date(time));
        return FileCreator.prepare(
                "db",
                "db_" + dbName + "_" + formattedTime + ".csv");
    }

    public static File prepareScreen(long id, boolean fromCrash) {
        String subfolder = fromCrash ? "crash" : "screenshots";
        String filename = fromCrash ?
                "crash_" + id + "_screen" :
                "screenshots" + id;

        return FileCreator.prepare(
                subfolder,
                filename + ".jpg");
    }
}
