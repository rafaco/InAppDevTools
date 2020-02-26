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

package es.rafaco.inappdevtools.library.view.overlay.screens.errors;

import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;

public class CrashHelper extends ScreenHelper {

    public static final String CAUSED_BY = "Caused by:";

    @Override
    public String getReportPath() {
        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    //region [ TEXT FORMATTERS ]

    public String getFormattedAt(Crash data) {
        String[] split = data.getStacktrace().split("\n\t");
        return formatAt(split[1]);
    }

    public String getCaused(Crash data) {
        String[] split = data.getStacktrace().split("\n\t");
        for (int i=0; i<split.length; i++){
            String line = split[i];
            if (line.contains(CAUSED_BY)){
                return line.substring(line.indexOf(CAUSED_BY));
            }
        }
        return null;
    }

    public String getCausedAt(Crash data) {
        String[] split = data.getStacktrace().split("\n\t");
        for (int i=0; i<split.length; i++){
            String line = split[i];
            if (line.contains(CAUSED_BY)){
                return formatAt(split[i+1]);
            }
        }
        return null;
    }

    private String formatAt(String text){
        return text.replace("(", " (");
    }

    //endregion
}
