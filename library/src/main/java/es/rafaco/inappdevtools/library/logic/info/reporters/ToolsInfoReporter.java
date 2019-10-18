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

package es.rafaco.inappdevtools.library.logic.info.reporters;

import android.content.Context;
import android.text.TextUtils;

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.storage.files.PluginList;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ToolsInfoReporter extends AbstractInfoReporter {

    public ToolsInfoReporter(Context context) {
        this(context, InfoReport.TOOLS);
    }

    public ToolsInfoReporter(Context context, InfoReport report) {
        super(context, report);
    }

    @Override
    public String getOverview() {
        return "Iadt v" + getVersionFormatted() + "\n"
                + getFriendlyBuildType();
    }

    @Override
    public InfoReportData getData() {
        return new InfoReportData.Builder(getReport())
                .setOverview(getOverview())
                .add(getLibraryInfo())
                .add(getDbInfo())
                .add(getBuildConfig())
                .add(getBuildInfo())
                .build();
    }

    private InfoGroupData getDbInfo() {
        InfoGroupData group = new InfoGroupData.Builder("Database")
                .setIcon(R.string.gmd_sd_storage)
                .add(IadtController.get().getDatabase().getOverview())
                .build();
        return group;
    }

    private InfoGroupData getBuildInfo() {
        return new InfoGroupData.Builder("Generated BuildInfo")
                .setIcon(R.string.gmd_settings_system_daydream)
                .add(new JsonAssetHelper(context, JsonAsset.BUILD_INFO).getAll())
                .build();
    }

    private InfoGroupData getBuildConfig() {
        return new InfoGroupData.Builder("Generated BuildConfig")
                .setIcon(R.string.gmd_settings_applications)
                .add(new JsonAssetHelper(context, JsonAsset.BUILD_CONFIG).getAll())
                .build();
    }

    public InfoGroupData getLibraryInfo() {
        InfoGroupData group = new InfoGroupData.Builder("InAppDevTools")
                .setIcon(R.string.gmd_assignment)
                .setOverview(BuildConfig.VERSION_NAME)
                .add("Library version", getVersionFormatted())
                .add("Plugin version", PluginList.getIadtVersion())
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
