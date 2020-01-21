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

package es.rafaco.inappdevtools.library.logic.documents.generators.info;

import android.content.Context;
import android.text.TextUtils;

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.AssetJsonHelper;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.storage.files.utils.PluginListUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ToolsInfoDocumentGenerator extends AbstractDocumentGenerator {

    private final long sessionId;

    public ToolsInfoDocumentGenerator(Context context, Document report, long param) {
        super(context, report, param);
        this.sessionId = param;
    }

    @Override
    public String getTitle() {
        return getDocument().getName() + " Info from Session " + sessionId;
    }

    @Override
    public String getSubfolder() {
        return "session/" + sessionId;
    }

    @Override
    public String getFilename() {
        return "info_" + getDocument().getName().toLowerCase() + "_" + sessionId + ".txt";
    }

    @Override
    public String getOverview() {
        return "Iadt v" + getVersionFormatted() + "\n"
                + getFriendlyBuildType();
    }

    public String getShortOverview() {
        return "v" + BuildConfig.VERSION_NAME;
    }

    @Override
    public DocumentData getData() {
        return new DocumentData.Builder(getTitle())
                .setOverview(getOverview())
                .add(getLibraryInfo())
                .add(getDbInfo())
                .add(getBuildConfig())
                .add(getBuildInfo())
                .build();
    }

    private DocumentSectionData getDbInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Database")
                .setIcon(R.string.gmd_sd_storage)
                .add(IadtController.get().getDatabase().getOverview())
                .build();
        return group;
    }

    private DocumentSectionData getBuildInfo() {
        return new DocumentSectionData.Builder("Generated BuildInfo")
                .setIcon(R.string.gmd_settings_system_daydream)
                .add(new AssetJsonHelper(context, IadtPath.BUILD_INFO).getAll())
                .build();
    }

    private DocumentSectionData getBuildConfig() {
        return new DocumentSectionData.Builder("Generated BuildConfig")
                .setIcon(R.string.gmd_settings_applications)
                .add(new AssetJsonHelper(context, IadtPath.BUILD_CONFIG).getAll())
                .build();
    }

    public DocumentSectionData getLibraryInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("InAppDevTools")
                .setIcon(R.string.gmd_assignment)
                .setOverview(BuildConfig.VERSION_NAME)
                .add("Library version", getVersionFormatted())
                .add("Plugin version", PluginListUtils.getIadtVersion())
                .add("Build type", BuildConfig.BUILD_TYPE)
                .add("Flavor", BuildConfig.FLAVOR)
                .build();
        return group;
    }

    private String getVersionFormatted() {
        return BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
    }

    public String getFriendlyBuildType() {
        String flavor = BuildConfig.FLAVOR;
        String buildType = BuildConfig.BUILD_TYPE;
        String build = TextUtils.isEmpty(flavor) ? Humanizer.toCapitalCase(buildType)
                :  Humanizer.toCapitalCase(flavor) + Humanizer.toCapitalCase(buildType);
        return build;
    }
}
