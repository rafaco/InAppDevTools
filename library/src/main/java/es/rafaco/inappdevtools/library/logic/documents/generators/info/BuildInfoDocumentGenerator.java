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

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.builds.BuildFilesRepository;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.config.BuildInfo;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessData;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfigField;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.JsonHelper;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class BuildInfoDocumentGenerator extends AbstractDocumentGenerator {

    private final long sessionId;
    private final long buildId;

    JsonHelper buildInfo;
    JsonHelper buildConfig;
    JsonHelper appBuildConfig;

    public BuildInfoDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);

        //TODO: param should be buildId.
        //TODO: Remove deprecated firstSession param at Build object
        this.sessionId = param;
        this.buildId = BuildFilesRepository.getBuildIdForSession(sessionId);

        buildInfo = BuildFilesRepository.getBuildInfoHelper(sessionId);
        buildConfig = BuildFilesRepository.getBuildConfigHelper(sessionId);
        appBuildConfig = BuildFilesRepository.getAppBuildConfigHelper(sessionId);
    }

    @Override
    public String getTitle() {
        return "Build " + buildId;
    }

    @Override
    public String getSubfolder() {
        return "build/" + buildId;
    }

    @Override
    public String getFilename() {
        return "info_build_" + buildId + ".txt";
    }

    @Override
    public String getOverview() {
        String firstLine = getFriendlyBuildType();
        String secondLine = getFriendlyElapsedTime();
        String thirdLine = getHostOverview();
        return firstLine + "\n" + secondLine + "\n" + thirdLine;
    }



    public String getShortOverview() {
        return getFriendlyBuildType() + " at "
                + DateUtils.formatShortDate(Long.parseLong(buildInfo.getString(BuildInfo.BUILD_TIME)));
    }

    @Override
    public DocumentData getData() {
        DocumentData.Builder builder = new DocumentData.Builder(getDocumentType())
                .setTitle(getTitle())
                .setOverview(getOverview());

        String notes = IadtController.get().getConfig().getString(BuildConfigField.NOTES);
        if (!TextUtils.isEmpty(notes)){
            builder.add(getNotesInfo());
            //welcomeText += notes + Humanizer.newLine();
        }

        builder.add(getBuildInfo())
                .add(getBuildHostInfo())
                .add(getBuildEnvironment());

        return builder.build();
    }

    private DocumentSectionData getNotesInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Notes")
                .setIcon(R.string.gmd_speaker_notes)
                .setOverview("Added")
                .add(IadtController.get().getConfig().getString(BuildConfigField.NOTES))
                .build();
        return group;
    }

    public DocumentSectionData getBuildHostInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Host")
                .setIcon(R.string.gmd_desktop_windows)
                .setOverview(buildInfo.getString(BuildInfo.HOST_NAME))
                .add("Host name", buildInfo.getString(BuildInfo.HOST_NAME))
                .add("Host OS", buildInfo.getString(BuildInfo.HOST_OS))
                .add("Host IP", buildInfo.getString(BuildInfo.HOST_ADDRESS))
                .add("Host user", buildInfo.getString(BuildInfo.HOST_USER))
                .build();
        return group;
    }

    public DocumentSectionData getBuildInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Build")
                .setIcon(R.string.gmd_history)
                .setOverview(getFriendlyBuildType() + ", " + getFriendlyElapsedTime())
                .add("Build time", buildInfo.getString(BuildInfo.BUILD_TIME_UTC))
                .add("Build type", appBuildConfig.getString(AppBuildConfigField.BUILD_TYPE))
                .add("Flavor", appBuildConfig.getString(AppBuildConfigField.FLAVOR))

                //.add("Iadt plugin (Old)", PluginListUtils.getIadtVersion())
                .build();
        return group;
    }

    public DocumentSectionData getBuildEnvironment() {
        return new DocumentSectionData.Builder("Environment")
                .setIcon(R.string.gmd_extension)
                .setOverview("Gradle " + buildInfo.getString(BuildInfo.GRADLE_VERSION))
                .add("Gradle", buildInfo.getString(BuildInfo.GRADLE_VERSION))
                .add("Java version", buildInfo.getString(BuildInfo.JAVA_VERSION))
                .add("Java version", buildInfo.getString(BuildInfo.JAVA_VENDOR))
                .add("Android Gradle plugin", buildInfo.getString(BuildInfo.ANDROID_PLUGIN_VERSION))
                .add("Iadt plugin", buildInfo.getString(BuildInfo.IADT_PLUGIN_VERSION))
                .addButton(new ButtonBorderlessData("Plugins",
                        R.drawable.ic_format_align_left_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.PLUGIN_LIST_FILE);
                                String params = SourceDetailScreen.buildInternalParams(path);
                                OverlayService.performNavigation(SourceDetailScreen.class, params);
                            }
                        }))
                .addButton(new ButtonBorderlessData("Dependencies",
                        R.drawable.ic_format_align_left_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.DEPENDENCIES_FILE);
                                String params = SourceDetailScreen.buildInternalParams(path);
                                OverlayService.performNavigation(SourceDetailScreen.class, params);
                            }
                        }))
                .build();
    }



    public String getFriendlyElapsedTime() {
        return Humanizer.getElapsedTime(
                Long.parseLong(buildInfo.getString(BuildInfo.BUILD_TIME)));
    }

    @NonNull
    public String getFriendlyBuildType() {
        String flavor = appBuildConfig.getString(AppBuildConfigField.FLAVOR);
        String buildType = appBuildConfig.getString(AppBuildConfigField.BUILD_TYPE);
        String build = TextUtils.isEmpty(flavor) ? Humanizer.toCapitalCase(buildType)
                : Humanizer.toCapitalCase(flavor) + Humanizer.toCapitalCase(buildType);
        return build;
    }

    private String getHostOverview() {
        String user = buildInfo.getString(BuildInfo.HOST_USER);
        String machine = buildInfo.getString(BuildInfo.HOST_NAME);
        return String.format("By %s at %s", user, machine);
    }

    public String getBuildOverviewForWelcome() {
        String firstLine = getFriendlyBuildType();
        String secondLine = getFriendlyElapsedTime();
        return firstLine + " build from " + secondLine;
    }
}
