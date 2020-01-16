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

package es.rafaco.inappdevtools.library.logic.documents;

import android.content.Context;

import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;

public class DocumentManager {

    private final Context context;

    public DocumentManager(Context context) {
        this.context = context;
    }


    public DocumentData getDocumentData(Document report) {
        return getGenerator(report).getData();
    }

    public AbstractDocumentGenerator getGenerator(Document report) {
        return report.getGenerator();
    }


    //TODO: remove
    public DocumentData getDocumentData(int infoReportIndex) {
        Document document = getInfoReport(infoReportIndex);
        return getDocumentData(document);
    }
    public Document getInfoReport(int infoReportIndex) {
        Document[] documents = Document.getInfoDocuments();
        return documents[infoReportIndex];
    }
}
