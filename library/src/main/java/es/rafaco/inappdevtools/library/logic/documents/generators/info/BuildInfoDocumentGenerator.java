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
import es.rafaco.inappdevtools.library.logic.config.GitInfo;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfigField;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
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
    JsonHelper gitInfo;
    JsonHelper appBuildConfig;

    public BuildInfoDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);

        //TODO: param should be buildId.
        //TODO: Remove deprecated firstSession param at Build object
        this.sessionId = param;
        this.buildId = BuildFilesRepository.getBuildIdForSession(sessionId);

        buildInfo = BuildFilesRepository.getBuildInfoHelper(sessionId);
        buildConfig = BuildFilesRepository.getBuildConfigHelper(sessionId);
        gitInfo = BuildFilesRepository.getGitInfoHelper(sessionId);
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
        String branch = gitInfo.getString(GitInfo.LOCAL_BRANCH);


        String local_commits = gitInfo.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitInfo.getBoolean(GitInfo.HAS_LOCAL_CHANGES);

        if (!hasLocalCommits && !hasLocalChanges){
            return branch + " branch without changes";
        }
        else{
            return branch + " branch with local changes";
        }
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

        if (!isGitEnabled()){
            builder.add(getGitUnavailableInfo());
        }
        else{
            builder.add(getGitUserInfo())
                    .add(getGitRepoInfo())
                    .add(getGitLocalRepoInfo())
                    .add(getGitLocalChangesInfo());
        }

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

    public String getBuildWelcome() {
        String firstLine = getFriendlyBuildType();
        String secondLine = getFriendlyElapsedTime();
        return firstLine + " build from " + secondLine;
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
        DocumentSectionData group = new DocumentSectionData.Builder("Environment")
                .setIcon(R.string.gmd_extension)
                .setOverview("Gradle " + buildInfo.getString(BuildInfo.GRADLE_VERSION))
                .add("Gradle", buildInfo.getString(BuildInfo.GRADLE_VERSION))
                .add("Java version", buildInfo.getString(BuildInfo.JAVA_VERSION))
                .add("Java version", buildInfo.getString(BuildInfo.JAVA_VENDOR))
                .add("Android Gradle plugin", buildInfo.getString(BuildInfo.ANDROID_PLUGIN_VERSION))
                .add("Iadt plugin", buildInfo.getString(BuildInfo.IADT_PLUGIN_VERSION))
                .addButton(new RunButton("Plugins",
                        R.drawable.ic_format_align_left_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.PLUGIN_LIST_FILE);
                                String params = SourceDetailScreen.buildInternalParams(path);
                                OverlayService.performNavigation(SourceDetailScreen.class, params);
                            }
                        }))
                .addButton(new RunButton("Dependencies",
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
        return group;
    }



    private DocumentSectionData getGitUnavailableInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Git Info")
                .setIcon(R.string.gmd_assignment_late)
                .setOverview("UNAVAILABLE")
                .add("Git command was not reachable on build time or your project is not in a Git repository.")
                .build();
        return group;
    }

    public DocumentSectionData getGitUserInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Git User")
                .setIcon(R.string.gmd_person)
                .setOverview(gitInfo.getString(GitInfo.USER_NAME))
                .add("Git name", gitInfo.getString(GitInfo.USER_NAME))
                .add("Git email", gitInfo.getString(GitInfo.USER_EMAIL))
                .add("Git version", gitInfo.getString(GitInfo.VERSION))
                .addButton(new RunButton("Write Email",
                        R.drawable.ic_email_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                ExternalIntentUtils.composeEmail(gitInfo.getString(GitInfo.USER_EMAIL), "Email from IADT");
                            }
                        }))
                .build();
        return group;
    }

    public DocumentSectionData getGitRepoInfo() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Remote repo")
                .setIcon(R.string.gmd_assignment_turned_in);

        if (!isGitEnabled()){
            group.setOverview("N/A");
            group.add("No git repository found at build folder");
            return group.build();
        }

        group.setOverview(gitInfo.getString(GitInfo.LOCAL_BRANCH) + "-" + gitInfo.getString(GitInfo.TAG_LAST));
        group.add("Git url", gitInfo.getString(GitInfo.REMOTE_URL))
                .add("Branch", gitInfo.getString(GitInfo.LOCAL_BRANCH))
                .add("Last Tag", gitInfo.getString(GitInfo.TAG_LAST))
                .add("Tag Info", gitInfo.getString(GitInfo.TAG_INFO))
                .add(" - Last commit:")
                .add(gitInfo.getString(GitInfo.REMOTE_LAST).replace("\n\n", "\n-> "));
        group.addButton(new RunButton("Browse repo",
                R.drawable.ic_public_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        ExternalIntentUtils.viewUrl(gitInfo.getString(GitInfo.REMOTE_URL));
                    }
                }));
        return group.build();
    }

    public String getRepositoryOverview() {
        String build = getFriendlyBuildType();
        if (!isGitEnabled()){
            return build;
        }
        String branch = gitInfo.getString(GitInfo.LOCAL_BRANCH);
        String tag = gitInfo.getString(GitInfo.TAG_LAST);

        return String.format("%s from %s %s", build, branch, tag);
    }

    public String getBuildOverview() {
        String time = getFriendlyElapsedTime();
        String user = gitInfo.getString(GitInfo.USER_NAME);
        if (TextUtils.isEmpty(user))
            user = buildInfo.getString(BuildInfo.HOST_USER);
        return String.format("%s by %s", time, user);
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

    public String getLocalOverview(){
        if (!isGitEnabled()){
            return "No Git repository";
        }
        String local_commits = gitInfo.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitInfo.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        String file_status = gitInfo.getString(GitInfo.LOCAL_CHANGES);
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

    public DocumentSectionData getGitLocalRepoInfo() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Local repo")
                .setIcon(R.string.gmd_assignment_ind);

        boolean hasLocalCommits = gitInfo.getBoolean(GitInfo.HAS_LOCAL_COMMITS);
        if (!hasLocalCommits){
            group.setOverview("CLEAN");
            group.add("No local commits");
            return group.build();
        }

        String local_commits = gitInfo.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        group.setOverview(local_commits_count + " commits ahead");
        group.add(local_commits);
        group.addButton(new RunButton("View Commits",
                R.drawable.ic_add_circle_outline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.LOCAL_COMMITS_FILE);
                        String params = SourceDetailScreen.buildInternalParams(path);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));

        return group.build();
    }

    public DocumentSectionData getGitLocalChangesInfo() {
        boolean hasLocalChanges = gitInfo.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        String file_status = gitInfo.getString(GitInfo.LOCAL_CHANGES);
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
        group.addButton(new RunButton("View Diffs",
                R.drawable.ic_code_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.LOCAL_CHANGES_FILE);
                        String params = SourceDetailScreen.buildInternalParams(path);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));

        return group.build();
    }

    public boolean isGitEnabled() {
        return gitInfo.getBoolean(GitInfo.ENABLED);
    }

}
