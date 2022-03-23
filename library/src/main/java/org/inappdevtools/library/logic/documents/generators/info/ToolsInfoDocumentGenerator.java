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

package org.inappdevtools.library.logic.documents.generators.info;

import android.content.Context;
import android.text.TextUtils;

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.files.utils.PluginListUtils;
import org.inappdevtools.library.BuildConfig;

import org.inappdevtools.library.R;
import org.inappdevtools.library.logic.builds.BuildFilesRepository;
import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import org.inappdevtools.library.logic.documents.data.DocumentSectionData;
import org.inappdevtools.library.logic.documents.data.DocumentData;
import org.inappdevtools.library.view.utils.Humanizer;

public class ToolsInfoDocumentGenerator extends AbstractDocumentGenerator {

    private final long sessionId;

    public ToolsInfoDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);
        this.sessionId = param;
    }

    @Override
    public String getTitle() {
        return getDocumentType().getName() + " from Session " + sessionId;
    }

    @Override
    public String getSubfolder() {
        return "session/" + sessionId;
    }

    @Override
    public String getFilename() {
        return "info_" + getDocumentType().getName().toLowerCase() + "_" + sessionId + ".txt";
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
        return new DocumentData.Builder(getDocumentType())
                .setTitle(getTitle())
                .setOverview(getOverview())
                .add(getLibraryInfo())
                .add(getDbInfo())
                .add(getBuildConfig())
                .add(getBuildInfo())
                .add(getGitInfo())
                .build();
    }

    private DocumentSectionData getDbInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Database")
                .setIcon(R.string.gmd_sd_storage)
                .add(IadtDatabase.get().getOverview())
                .build();
        return group;
    }

    private DocumentSectionData getBuildInfo() {
        String content = BuildFilesRepository.getBuildInfoHelper(sessionId).getAll();
        return new DocumentSectionData.Builder("Generated BuildInfo")
                .setIcon(R.string.gmd_build)
                .add(content)
                .build();
    }

    private DocumentSectionData getGitInfo() {
        String content = BuildFilesRepository.getGitInfoHelper(sessionId).getAll();
        return new DocumentSectionData.Builder("Generated GitInfo")
                .setIcon(R.string.gmd_kitchen)
                .add(content)
                .build();
    }

    private DocumentSectionData getBuildConfig() {
        String content = BuildFilesRepository.getBuildConfigHelper(sessionId).getAll();
        return new DocumentSectionData.Builder("Generated BuildConfig")
                .setIcon(R.string.gmd_settings_applications)
                .add(content)
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
