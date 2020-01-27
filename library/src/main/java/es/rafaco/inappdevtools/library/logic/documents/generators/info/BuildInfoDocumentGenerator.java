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
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.config.BuildInfo;
import es.rafaco.inappdevtools.library.logic.config.GitInfo;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.AssetJsonHelper;
import es.rafaco.inappdevtools.library.storage.files.utils.PluginListUtils;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class BuildInfoDocumentGenerator extends AbstractDocumentGenerator {

    private final long sessionId;
    AssetJsonHelper buildInfo;
    AssetJsonHelper buildConfig;
    AssetJsonHelper gitConfig;

    public BuildInfoDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);
        this.sessionId = param;
        buildInfo = new AssetJsonHelper(context, IadtPath.BUILD_INFO);
        buildConfig = new AssetJsonHelper(context, IadtPath.BUILD_CONFIG);
        gitConfig = new AssetJsonHelper(context, IadtPath.GIT_CONFIG);
    }

    @Override
    public String getTitle() {
        return getDocumentType().getName() + " Info from Session " + sessionId;
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
        String firstLine = getBuildOverview();
        String secondLine = getRepositoryOverview();
        String thirdLine = getLocalOverview();
        return firstLine + "\n" + secondLine + "\n" + thirdLine;
    }

    public String getShortOverview() {
        return getFriendlyBuildType() + " at "
                + DateUtils.formatShortDate(Long.parseLong(buildInfo.getString(BuildInfo.BUILD_TIME)));
    }

    public String getShortOverviewSources() {
        if (!isGitEnabled()){
            return "Unavailable";
        }
        String branch = gitConfig.getString(GitInfo.LOCAL_BRANCH);
        String tag = gitConfig.getString(GitInfo.TAG);
        return branch + " " + tag;
    }

    public String getShortOverviewChanges() {
        if (!isGitEnabled()){
            return "Unavailable";
        }
        return getLocalOverview();
    }

    @Override
    public DocumentData getData() {
        DocumentData.Builder builder = new DocumentData.Builder(getTitle())
                .setOverview(getOverview());

        String notes = IadtController.get().getConfig().getString(BuildConfig.NOTES);
        if (!TextUtils.isEmpty(notes)){
            builder.add(getNotesInfo());
            //welcomeText += notes + Humanizer.newLine();
        }

        builder.add(getBuilderInfo())
                .add(getBuildHostInfo())
                .add(getBuildInfo())
                .add(getRepositoryInfo())
                .add(getLocalRepositoryInfo())
                .add(getLocalChangesInfo());

        return builder.build();
    }

    private DocumentSectionData getNotesInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Notes")
                .setIcon(R.string.gmd_speaker_notes)
                .setOverview("Added")
                .add(IadtController.get().getConfig().getString(BuildConfig.NOTES))
                .build();
        return group;
    }

    public String getBuildWelcome() {
        String firstLine = getFriendlyBuildType();
        String secondLine = getFriendlyElapsedTime();
        return firstLine + " build from " + secondLine;
    }


    public DocumentSectionData getBuilderInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Builder")
                .setIcon(R.string.gmd_person)
                .setOverview(buildInfo.getString(BuildInfo.USER_NAME))
                .add("User name", buildInfo.getString(BuildInfo.USER_NAME))
                .add("User email", buildInfo.getString(BuildInfo.USER_EMAIL))
                .addButton(new RunButton("Send Email",
                    R.drawable.ic_email_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            ExternalIntentUtils.composeEmail(buildInfo.getString(BuildInfo.USER_EMAIL), "Email from IADT");
                        }
                    }))
                .build();
        return group;
    }

    public DocumentSectionData getBuildHostInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Host")
                .setIcon(R.string.gmd_desktop_windows)
                .setOverview(buildInfo.getString(BuildInfo.HOST_NAME))
                .add("Host name", buildInfo.getString(BuildInfo.HOST_NAME))
                .add("Host OS_INFO", buildInfo.getString(BuildInfo.HOST_OS))
                .add("Host version", buildInfo.getString(BuildInfo.HOST_VERSION))
                .add("Host arch", buildInfo.getString(BuildInfo.HOST_ARCH))
                .add("Host IP", buildInfo.getString(BuildInfo.HOST_ADDRESS))
                .build();
        return group;
    }

    public DocumentSectionData getBuildInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Build")
                .setIcon(R.string.gmd_history)
                .setOverview(getFriendlyBuildType() + ", " + getFriendlyElapsedTime())
                .add("Build time", buildInfo.getString(BuildInfo.BUILD_TIME_UTC))
                .add("Build type", AppBuildConfig.getStringValue(context, AppBuildConfigFields.BUILD_TYPE))
                .add("Flavor", AppBuildConfig.getStringValue(context, AppBuildConfigFields.FLAVOR))
                .add("Gradle version", buildInfo.getString(BuildInfo.GRADLE_VERSION))
                .add("Android plugin", PluginListUtils.getAndroidVersion())
                .add("Iadt plugin", PluginListUtils.getIadtVersion())
                .build();
        return group;
    }

    public DocumentSectionData getRepositoryInfo() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Remote repo")
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
        return Humanizer.getElapsedTimeLowered(
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

    public DocumentSectionData getLocalRepositoryInfo() {
        String local_commits = gitConfig.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;

        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Local repo")
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
                                SourceDetailScreen.buildParams(IadtPath.ASSETS  + "/" + IadtPath.LOCAL_COMMITS));
                    }
                }));

        return group.build();
    }

    public DocumentSectionData getLocalChangesInfo() {
        boolean hasLocalChanges = gitConfig.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        String file_status = gitConfig.getString(GitInfo.LOCAL_CHANGES);
        int file_changes_count = Humanizer.countLines(file_status);

        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Local changes")
                .setIcon(R.string.gmd_assignment_late);

        if (!hasLocalChanges){
            group.setOverview("CLEAN");
            group.add("No local changes");
            return group.build();
        }

        group.setOverview(file_changes_count + " files");
        group.add(file_status);
        group.addButton(new RunButton("View Diff",
                R.drawable.ic_code_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SourceDetailScreen.class,
                                SourceDetailScreen.buildParams(IadtPath.ASSETS  + "/" + IadtPath.LOCAL_CHANGES));
                    }
                }));

        return group.build();
    }

    public boolean isGitEnabled() {
        return gitConfig.getBoolean(GitInfo.ENABLED);
    }

}
