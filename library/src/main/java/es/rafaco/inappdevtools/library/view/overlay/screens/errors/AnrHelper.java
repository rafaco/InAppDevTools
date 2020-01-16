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

import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.view.overlay.screens.ScreenHelper;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;


public class AnrHelper extends ScreenHelper {


    @Override
    public String getReportPath() {
        return null;
    }

    @Override
    public String getReportContent() {
        return null;
    }

    public DocumentData parseToInfoGroup(Anr data){
        DocumentSectionData basic = new DocumentSectionData.Builder("Anr info")
                .add("AnrId", data.getUid())
                .add("Date", DateUtils.format(data.getDate()))
                .add("Message", data.getMessage())
                .add("Cause", data.getCause())
                .build();

        DocumentSectionData stacktrace = new DocumentSectionData.Builder("Stacktrace")
                .add("", "")
                .add("", data.getStacktrace())
                .build();

        return new DocumentData.Builder("")
                .add(basic)
                .add(stacktrace)
                .build();
    }
}
