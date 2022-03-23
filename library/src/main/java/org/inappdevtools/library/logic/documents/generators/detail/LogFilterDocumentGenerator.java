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

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.Friendly;
import org.inappdevtools.library.storage.db.entities.FriendlyDao;

import java.util.List;

import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.data.DocumentData;
import org.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import org.inappdevtools.library.logic.log.datasource.LogQueryHelper;
import org.inappdevtools.library.logic.log.filter.LogFilterHelper;
import org.inappdevtools.library.logic.log.filter.LogFilterStore;
import org.inappdevtools.library.logic.utils.DateUtils;

public class LogFilterDocumentGenerator extends AbstractDocumentGenerator {

    private final long identifier;
    private List<Friendly> rawData;

    public LogFilterDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);
        this.identifier = param;
    }

    @Override
    public String getTitle() {
        return "Custom log filter";
    }

    @Override
    public String getSubfolder() {
        return "logFilter";
    }

    @Override
    public String getFilename() {
        return "logs_custom_" + identifier + ".txt";
    }

    @Override
    public String getOverview() {
        return "";
    }

    @Override
    public DocumentData getData() {
        LogFilterHelper logFilterHelper = new LogFilterHelper(LogFilterStore.get());
        DocumentData.Builder builder = new DocumentData.Builder(getDocumentType())
                .setTitle(getTitle())
                .setOverview(logFilterHelper.getOverview());

        extractRawData(logFilterHelper);
        insertRawData(builder);
        return builder.build();
    }


    private void extractRawData(LogFilterHelper logFilterHelper) {
        LogQueryHelper logQueryHelper = new LogQueryHelper(logFilterHelper.getBackFilter());
        FriendlyDao dao = IadtDatabase.get().friendlyDao();
        rawData = dao.filterListWithQuery(logQueryHelper.getFilterQuery());
    }

    private void insertRawData(DocumentData.Builder builder) {
        for (Friendly step: rawData) {
            String parsed = String.format("%s %s/%s: %s",
                    DateUtils.formatLogcatDate(step.getDate()),
                    step.getSeverity(),
                    step.getSubcategory(),
                    step.getMessage());
            builder.add(parsed);
        }
    }
}
