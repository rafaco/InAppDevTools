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

package org.inappdevtools.library.logic.documents.generators;

import android.content.Context;

import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.data.DocumentData;

public abstract class AbstractDocumentGenerator {

    protected Context context;
    private final DocumentType documentType;
    private final Object param;

    public AbstractDocumentGenerator(Context context, DocumentType documentType, Object param) {
        this.context = context;
        this.documentType = documentType;
        this.param = param;
    }

    public abstract String getTitle();
    public abstract String getSubfolder();
    public abstract String getFilename();
    public abstract String getOverview();
    public abstract DocumentData getData();

    protected Object getParam() {
        return param;
    }

    protected DocumentType getDocumentType() {
        return documentType;
    }
}
