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

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Logcat;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;

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

    public DocumentData parseToInfoGroup(Crash data){

        DocumentSectionData status = new DocumentSectionData.Builder("App status")
                //.add("When", DateUtils.getElapsedTime(data.getDate())) //TODO: no an app status
                .add("AppStatus", data.isForeground() ? "Foreground" : "Background")
                .add("LastActivity", data.getLastActivity())
                .build();

        DocumentSectionData basic = new DocumentSectionData.Builder("Crash info")
                .add("CrashId", data.getUid())
                .add("Date", DateUtils.format(data.getDate()))
                .add("AppStatus", data.isForeground() ? "Foreground" : "Background")
                .add("LastActivity", data.getLastActivity())
                .add("Exception", data.getException())
                .add("Message", data.getMessage())
                .add("ExceptionAt", data.getExceptionAt())
                .add("CauseException", data.getCauseException())
                .add("CauseMessage", data.getCauseMessage())
                .add("CauseAt", data.getCauseExceptionAt())
                .build();

        DocumentSectionData thread = new DocumentSectionData.Builder("Thread info")
                .add("Thread ID", data.getThreadId())
                .add("Name", data.getThreadName())
                .add("Group", data.getThreadGroupName())
                .add("isMain", data.isMainThread())
                .build();

        DocumentSectionData links = new DocumentSectionData.Builder("Linked info")
                .add("ReportPath", String.valueOf(data.getReportPath()))
                .add("LogcatId", String.valueOf(data.getLogcatId()))
                .add("ScreenId", String.valueOf(data.getScreenId()))
                .build();

        DocumentSectionData stacktrace = new DocumentSectionData.Builder("Stacktrace")
                .add("", data.getStacktrace())
                .build();

        return new DocumentData.Builder("")
                .add(status)
                .add(basic)
                .add(thread)
                .add(links)
                .add(stacktrace)
                .build();
    }

    public List<String> getReportPaths(final Crash crash) {
        List<String> filePaths = new ArrayList<>();
        addCrashDetailFile(crash, filePaths);
        addLogcatFile(crash, filePaths);
        addScreenFile(crash, filePaths);

        return filePaths;
    }

    private void addCrashDetailFile(Crash crash, List<String> filePaths) {
        if (!TextUtils.isEmpty(crash.getReportPath())) {
            filePaths.add(crash.getReportPath());
        }
    }

    private void addScreenFile(Crash crash, List<String> filePaths) {
        Screenshot screenshot = IadtController.get().getDatabase().screenshotDao().findById(crash.getScreenId());
        String filePath = screenshot.getPath();

        if (!TextUtils.isEmpty(filePath)) {
            filePaths.add(filePath);
        }
    }

    private void addLogcatFile(Crash crash, List<String> filePaths) {
        Logcat logcat = IadtController.get().getDatabase().logcatDao().findById(crash.getLogcatId());
        String filePath = logcat.getPath();

        if (!TextUtils.isEmpty(filePath)) {
            filePaths.add(filePath);
        }
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
