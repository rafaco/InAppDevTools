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

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.detail.CrashDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.detail.InfoOverviewDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.detail.SessionDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.detail.SessionLogsDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.detail.SessionStepsDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.LiveInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.OSInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.ToolsInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;

public enum DocumentType {

    LIVE_INFO("Live", R.string.gmd_live_tv, LiveInfoDocumentGenerator.class, long.class),
    BUILD_INFO("Build", R.string.gmd_build, BuildInfoDocumentGenerator.class, long.class),
    APP_INFO("App", R.string.gmd_developer_board, AppInfoDocumentGenerator.class, long.class),
    OS_INFO("OS", R.string.gmd_android, OSInfoDocumentGenerator.class, long.class),
    DEVICE_INFO("Device", R.string.gmd_phone_android, DeviceInfoDocumentGenerator.class, long.class),
    TOOLS_INFO("Tools", R.string.gmd_extension, ToolsInfoDocumentGenerator.class, long.class),

    INFO_OVERVIEW("Info Overview", R.string.gmd_info, InfoOverviewDocumentGenerator.class, long.class),
    SESSION("Session", R.string.gmd_timeline, SessionDocumentGenerator.class, Session.class),
    SESSION_STEPS("Steps", R.string.gmd_history, SessionStepsDocumentGenerator.class, Session.class),
    SESSION_LOGS("Logs", R.string.gmd_history, SessionLogsDocumentGenerator.class, Session.class),
    CRASH("Crash", R.string.gmd_bug_report, CrashDocumentGenerator.class, Crash.class);

    private String name;
    private final int icon;
    private final Class<? extends AbstractDocumentGenerator> generatorClass;
    private final Class<?> paramClass;

    DocumentType(String name, int icon, Class<? extends AbstractDocumentGenerator> generatorClass, Class<?> paramClass) {
        this.name = name;
        this.paramClass = paramClass;
        this.icon = icon;
        this.generatorClass = generatorClass;
    }

    public static DocumentType[] getInfoValues() {
        List<DocumentType> infoValues = new ArrayList<>();
        infoValues.add(DocumentType.LIVE_INFO);
        infoValues.add(DocumentType.BUILD_INFO);
        infoValues.add(DocumentType.APP_INFO);
        infoValues.add(DocumentType.OS_INFO);
        infoValues.add(DocumentType.DEVICE_INFO);
        infoValues.add(DocumentType.TOOLS_INFO);
        return infoValues.toArray(new DocumentType[0]);
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends AbstractDocumentGenerator> getGeneratorClass() {
        return generatorClass;
    }

    public Class<?> getParamClass() {
        return paramClass;
    }

    public static DocumentType[] getValues() {
        return values();
    }
}
