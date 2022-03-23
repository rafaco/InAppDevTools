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

package org.inappdevtools.library.logic.documents.generators.detail;

import android.content.Context;

import org.inappdevtools.library.storage.db.entities.Crash;
import org.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.data.DocumentData;
import org.inappdevtools.library.logic.documents.data.DocumentSectionData;
import org.inappdevtools.library.logic.utils.DateUtils;

public class CrashDocumentGenerator extends AbstractDocumentGenerator {

    private final Crash data;

    public CrashDocumentGenerator(Context context, DocumentType report, Crash param) {
        super(context, report, param);
        this.data = param;
    }

    @Override
    public String getTitle() {
        return "Crash " + data.getUid() + " from Session " + data.getSessionId();
    }

    @Override
    public String getSubfolder() {
        return "session/" + data.getSessionId();
    }

    @Override
    public String getFilename() {
        return "crash_" + data.getUid() + "_detail.txt";
    }

    @Override
    public String getOverview() {
        return "";
    }

    @Override
    public DocumentData getData() {
        DocumentSectionData status = new DocumentSectionData.Builder("App status")
                //.add("When", DateUtils.getElapsedTime(data.getDate())) //TODO: no an app status
                .add("AppStatus", data.isForeground() ? "Foreground" : "Background")
                .add("LastActivity", data.getLastActivity())
                .build();

        DocumentSectionData basic = new DocumentSectionData.Builder("Crash info")
                .add("CrashId", data.getUid())
                .add("Date", DateUtils.format(data.getDate()))
                .add("")
                .add("Exception", data.getException())
                .add("Message", data.getMessage())
                .add("ExceptionAt", data.getExceptionAt())
                .add("")
                .add("Cause", data.getCauseException())
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

        return new DocumentData.Builder(getDocumentType())
                .setTitle(getTitle())
                .add(status)
                .add(basic)
                .add(thread)
                .add(links)
                .add(stacktrace)
                .build();
    }
}
