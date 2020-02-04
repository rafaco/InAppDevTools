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

package es.rafaco.inappdevtools.library.logic.documents.generators.detail;

import android.content.Context;

import java.util.List;

import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogQueryHelper;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterStore;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogViewHolder;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LogItemDocumentGenerator extends AbstractDocumentGenerator {

    private final long logId;
    private Friendly rawData;

    public LogItemDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);
        this.logId = param;
    }

    @Override
    public String getTitle() {
        return "Log item " + logId;
    }

    @Override
    public String getSubfolder() {
        return "logItem";
    }

    @Override
    public String getFilename() {
        return "log_item_" + logId + ".txt";
    }

    @Override
    public String getOverview() {
        return "";
    }

    @Override
    public DocumentData getData() {
        DocumentData.Builder builder = new DocumentData.Builder(getDocumentType())
                .setTitle(getTitle())
                .setOverview(getOverview());

        extractRawData();
        insertRawData(builder);
        return builder.build();
    }


    private void extractRawData() {
        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        rawData = dao.findById(logId);
    }

    private void insertRawData(DocumentData.Builder builder) {
        String severity = Humanizer.toCapitalCase(FriendlyLog.convertCharToLongString(rawData.getSeverity()));

        String textOverview = "Message: " + rawData.getMessage() + Humanizer.fullStop()
                + "Extra: " + rawData.getExtra() + Humanizer.fullStop()
                + "LogId: " + rawData.getUid() + Humanizer.newLine()
                + "LinkedId: " + rawData.getLinkedId() + Humanizer.newLine()
                + "Severity: " + severity + Humanizer.newLine()
                + LogViewHolder.getFormattedDetails(rawData);
        builder.add(textOverview);

        String parsedLine = String.format("%s %s/%s: %s",
                DateUtils.formatLogcatDate(rawData.getDate()),
                rawData.getSeverity(),
                rawData.getSubcategory(),
                rawData.getMessage());
        builder.add("");
        builder.add("Logcat formatted:");
        builder.add(parsedLine);
    }
}

