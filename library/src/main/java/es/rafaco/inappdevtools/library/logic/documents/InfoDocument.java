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

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.info.AppInfoGenerator;
import es.rafaco.inappdevtools.library.logic.documents.info.DeviceInfoGenerator;
import es.rafaco.inappdevtools.library.logic.documents.info.BuildInfoGenerator;
import es.rafaco.inappdevtools.library.logic.documents.info.LiveInfoGenerator;
import es.rafaco.inappdevtools.library.logic.documents.info.OSInfoGenerator;
import es.rafaco.inappdevtools.library.logic.documents.info.ToolsInfoGenerator;

public enum InfoDocument {

    LIVE("Live", R.string.gmd_live_tv, LiveInfoGenerator.class),
    BUILD("Build", R.string.gmd_build, BuildInfoGenerator.class),
    APP("App", R.string.gmd_developer_board, AppInfoGenerator.class),
    OS("OS", R.string.gmd_android, OSInfoGenerator.class),
    DEVICE("Device", R.string.gmd_phone_android, DeviceInfoGenerator.class),
    TOOLS("Iadt", R.string.gmd_extension, ToolsInfoGenerator.class);

    private String title;
    private final int icon;
    private final Class<? extends AbstractDocumentGenerator> generatorClass;

    InfoDocument(String title, int icon, Class<? extends AbstractDocumentGenerator> generatorClass) {
        this.title = title;
        this.icon = icon;
        this.generatorClass = generatorClass;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends AbstractDocumentGenerator> getGeneratorClass() {
        return generatorClass;
    }

    public static InfoDocument[] getValues() {
        return values();
    }
}
