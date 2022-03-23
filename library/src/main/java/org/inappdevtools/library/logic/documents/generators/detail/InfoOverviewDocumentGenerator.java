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

import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import org.inappdevtools.library.logic.documents.DocumentRepository;
import org.inappdevtools.library.logic.documents.data.DocumentData;
import org.inappdevtools.library.logic.documents.data.DocumentSectionData;

public class InfoOverviewDocumentGenerator extends AbstractDocumentGenerator {

    private long sessionId;

    public InfoOverviewDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);
        this.sessionId = param;
    }

    @Override
    public String getTitle() {
        return "Info overview from session " + sessionId;
    }

    @Override
    public String getSubfolder() {
        return "session/" + sessionId;
    }

    @Override
    public String getFilename() {
        return "info_overview_" + sessionId + ".txt";
    }

    @Override
    public String getOverview() {
        return "";
    }

    @Override
    public DocumentData getData() {
        DocumentType[] values = DocumentType.getInfoValues();
        DocumentData reportOverview = new DocumentData.Builder(getDocumentType())
                .setTitle(getTitle()).build();

        for (DocumentType documentType : values){
            DocumentData infoData = DocumentRepository.getDocument(documentType);
            reportOverview.getSections().add(new DocumentSectionData.Builder(getTitle())
                    .add(infoData.getOverview()).build());
        }
        return reportOverview;
    }
}
