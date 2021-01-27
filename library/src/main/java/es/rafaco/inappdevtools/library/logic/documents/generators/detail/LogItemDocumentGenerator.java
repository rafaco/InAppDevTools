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

import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogLineFormatter;

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
        FriendlyDao dao = IadtDatabase.get().friendlyDao();
        rawData = dao.findById(logId);
    }

    private void insertRawData(DocumentData.Builder builder) {
        LogLineFormatter formatter = new LogLineFormatter(rawData);
        builder.add(formatter.getMultiLine());
        builder.add("");
        builder.add("Logcat formatted:");
        builder.add(formatter.getOneLine());
    }
}

