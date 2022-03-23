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

package org.inappdevtools.library.storage.files.utils;

import android.content.Context;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.storage.files.IadtPath;
import org.inappdevtools.library.view.utils.Humanizer;

public class PluginListUtils {

    public static String getIadtVersion() {
        return getPluginVersion("inappdevtools-plugin");
    }

    public static String getAndroidVersion() {
        return getPluginVersion("gradle-api");
    }

    private static String getPluginVersion(String pluginName) {
        AssetFileReader reader = new AssetFileReader(getContext());
        String fileContents = reader.getFileContents(IadtPath.PLUGIN_LIST);
        String[] lines = fileContents.split(Humanizer.newLine());
        for (String line : lines) {
            if (line.startsWith(pluginName + "-")){
                return getVersion(line);
            }
        }
        return "";
    }

    private static String getVersion(String line) {
        int i = 0;
        while (i < line.length() && !Character.isDigit(line.charAt(i)))
            i++;

        return line.substring(i, line.length()-4);
    }

    private static Context getContext(){
        return IadtController.get().getContext();
    }
}
