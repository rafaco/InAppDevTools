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

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.config.BuildInfo;
import es.rafaco.inappdevtools.library.logic.config.GitInfo;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.files.GitAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.storage.files.PluginList;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class BuildInfoReporter extends AbstractInfoReporter {

    JsonAssetHelper buildInfo;
    JsonAssetHelper buildConfig;
    JsonAssetHelper gitConfig;

    public BuildInfoReporter(Context context) {
        this(context, InfoReport.BUILD);
    }

    public BuildInfoReporter(Context context, InfoReport report) {
        super(context, report);
        buildInfo = new JsonAssetHelper(context, JsonAsset.BUILD_INFO);
        buildConfig = new JsonAssetHelper(context, JsonAsset.BUILD_CONFIG);
        gitConfig = new JsonAssetHelper(context, JsonAsset.GIT_CONFIG);
    }

    @Override
    public String getOverview() {
        String firstLine = getBuildOverview();
        String secondLine = getRepositoryOverview();
        String thirdLine = getLocalOverview();
        return firstLine + "\n" + secondLine + "\n" + thirdLine;
    }

    @Override
    public InfoReportData getData() {
        return new InfoReportData.Builder(getReport())
                .setOverview(getOverview())
                .add(getBuilderInfo())
                .add(getBuildHostInfo())
                .add(getBuildInfo())
                .add(getRepositoryInfo())
                .add(getLocalRepositoryInfo())
                .add(getLocalChangesInfo())
                .build();
    }



    public InfoGroupData getBuilderInfo() {
        InfoGroupData group = new InfoGroupData.Builder("Builder")
                .setIcon(R.string.gmd_person)
                .setOverview(buildInfo.getString(BuildInfo.USER_NAME))
                .add("User name", buildInfo.getString(BuildInfo.USER_NAME))
                .add("User email", buildInfo.getString(BuildInfo.USER_EMAIL))
                .addButton(new RunButton("Send Email",
                    R.drawable.ic_message_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            ExternalIntentUtils.composeEmail(buildInfo.getString(BuildInfo.USER_EMAIL), "Email from IADT");
                        }
                    }))
                .build();
        return group;
    }

    public InfoGroupData getBuildHostInfo() {
        InfoGroupData group = new InfoGroupData.Builder("Host")
                .setIcon(R.string.gmd_desktop_windows)
                .setOverview(buildInfo.getString(BuildInfo.HOST_NAME))
                .add("Host name", buildInfo.getString(BuildInfo.HOST_NAME))
                .add("Host OS", buildInfo.getString(BuildInfo.HOST_OS))
                .add("Host version", buildInfo.getString(BuildInfo.HOST_VERSION))
                .add("Host arch", buildInfo.getString(BuildInfo.HOST_ARCH))
                .add("Host IP", buildInfo.getString(BuildInfo.HOST_ADDRESS))
                .build();
        return group;
    }

    public InfoGroupData getBuildInfo() {
        InfoGroupData group = new InfoGroupData.Builder("Build")
                .setIcon(R.string.gmd_history)
                .setOverview(getFriendlyBuildType() + ", " + getFriendlyElapsedTime())
                .add("Build time", buildInfo.getString(BuildInfo.BUILD_TIME_UTC))
                .add("Build type", AppBuildConfig.getStringValue(context, AppBuildConfigFields.BUILD_TYPE))
                .add("Flavor", AppBuildConfig.getStringValue(context, AppBuildConfigFields.FLAVOR))
                .add("Gradle version", buildInfo.getString(BuildInfo.GRADLE_VERSION))
                .add("Android plugin", PluginList.getAndroidVersion())
                .add("Iadt plugin", PluginList.getIadtVersion())
                .build();
        return group;
    }

    public InfoGroupData getRepositoryInfo() {
        InfoGroupData.Builder group = new InfoGroupData.Builder("Remote repo")
                .setIcon(R.string.gmd_assignment_turned_in);

        if (!isGitEnabled()){
            group.setOverview("N/A");
            group.add("No git repository found at build folder");
            return group.build();
        }

        group.setOverview(gitConfig.getString(GitInfo.LOCAL_BRANCH) + "-" + gitConfig.getString(GitInfo.TAG));
        group.add("Git url", gitConfig.getString(GitInfo.REMOTE_URL))
                .add("Branch", gitConfig.getString(GitInfo.LOCAL_BRANCH))
                .add("Tag", gitConfig.getString(GitInfo.TAG))
                .add("Tag Status", gitConfig.getString(GitInfo.INFO))
                .add(" - Last commit:")
                .add(gitConfig.getString(GitInfo.REMOTE_LAST).replace("\n\n", "\n-> "));
        group.addButton(new RunButton("Remote",
                R.drawable.ic_public_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        ExternalIntentUtils.viewUrl(gitConfig.getString(GitInfo.REMOTE_URL));
                    }
                }));
        return group.build();
    }

    public String getRepositoryOverview() {
        if (!isGitEnabled()){
            return null;
        }
        String build = getFriendlyBuildType();
        String branch = gitConfig.getString(GitInfo.LOCAL_BRANCH);
        String tag = gitConfig.getString(GitInfo.TAG);

        return String.format("%s from %s %s", build, branch, tag);
    }

    public String getBuildOverview() {
        String time = getFriendlyElapsedTime();
        String user = buildInfo.getString(BuildInfo.USER_NAME);
        return String.format("%s by %s", time, user);
    }

    public String getFriendlyElapsedTime() {
        return Humanizer.getElapsedTime(
                Long.parseLong(buildInfo.getString(BuildInfo.BUILD_TIME)));
    }

    @NonNull
    public String getFriendlyBuildType() {
        String flavor = AppBuildConfig.getStringValue(context, AppBuildConfigFields.FLAVOR);
        String buildType = AppBuildConfig.getStringValue(context, AppBuildConfigFields.BUILD_TYPE);
        String build = TextUtils.isEmpty(flavor) ? Humanizer.toCapitalCase(buildType)
                : Humanizer.toCapitalCase(flavor) + Humanizer.toCapitalCase(buildType);
        return build;
    }

    public String getLocalOverview(){
        String local_commits = gitConfig.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitConfig.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        String file_status = gitConfig.getString(GitInfo.LOCAL_CHANGES);
        int file_changes_count = Humanizer.countLines(file_status);

        if (!hasLocalCommits && !hasLocalChanges){
            return "No local changes";
        }

        String result = "";
        if (hasLocalCommits){
            result += local_commits_count + " commit ahead";
        }
        if(hasLocalChanges){
            if (hasLocalCommits) result += " and ";
            result += file_changes_count + " files changed";
        }

        return result;
    }

    public InfoGroupData getLocalRepositoryInfo() {
        String local_commits = gitConfig.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;

        InfoGroupData.Builder group = new InfoGroupData.Builder("Local repo")
                .setIcon(R.string.gmd_assignment_ind);

        if (!hasLocalCommits){
            group.setOverview("CLEAN");
            group.add("No local commits");
            return group.build();
        }

        group.setOverview(local_commits_count + " commits ahead");
        group.add(local_commits);
        group.addButton(new RunButton("View Commits",
                R.drawable.ic_add_circle_outline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SourceDetailScreen.class,
                                SourceDetailScreen.buildParams(GitAsset.LOCAL_COMMITS));
                    }
                }));

        return group.build();
    }

    public InfoGroupData getLocalChangesInfo() {
        boolean hasLocalChanges = gitConfig.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        String file_status = gitConfig.getString(GitInfo.LOCAL_CHANGES);
        int file_changes_count = Humanizer.countLines(file_status);

        InfoGroupData.Builder group = new InfoGroupData.Builder("Local changes")
                .setIcon(R.string.gmd_assignment_late);

        if (!hasLocalChanges){
            group.setOverview("CLEAN");
            group.add("No local changes");
            return group.build();
        }

        group.setOverview(file_changes_count + " files");
        group.add(file_status);
        group.addButton(new RunButton("View Changes",
                R.drawable.ic_remove_circle_outline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SourceDetailScreen.class,
                                SourceDetailScreen.buildParams(GitAsset.LOCAL_CHANGES));
                    }
                }));

        return group.build();
    }

    public boolean isGitEnabled() {
        return gitConfig.getBoolean(GitInfo.ENABLED);
    }

}
