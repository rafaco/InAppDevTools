/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.builds.BuildFilesRepository;
import es.rafaco.inappdevtools.library.logic.config.GitInfo;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.components.base.FlexData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.JsonHelper;
import es.rafaco.inappdevtools.library.view.components.items.TimelineFlexData;
import es.rafaco.inappdevtools.library.view.components.items.TimelineFlexHelper;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RepoInfoDocumentGenerator extends AbstractDocumentGenerator {

    private final long sessionId;
    private final long buildId;

    JsonHelper gitInfo;

    public RepoInfoDocumentGenerator(Context context, DocumentType report, long param) {
        super(context, report, param);

        //TODO: param should be buildId.
        //TODO: Remove deprecated firstSession param at Build object
        this.sessionId = param;
        this.buildId = BuildFilesRepository.getBuildIdForSession(sessionId);

        gitInfo = BuildFilesRepository.getGitInfoHelper(sessionId);
    }

    @Override
    public String getTitle() {
        return "Repository from build " + buildId;
    }

    @Override
    public String getSubfolder() {
        return "build/" + buildId;
    }

    @Override
    public String getFilename() {
        return "info_repo_" + buildId + ".txt";
    }

    @Override
    public String getOverview() {
        String firstLine = getRepoType();
        String secondLine = getBranchAndTag();
        String thirdLine = getLocalOverview();
        return firstLine + "\n" + secondLine + "\n" + thirdLine;
    }

    public String getShortOverview() {
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
                .setOverview(getOverview())
                .setScreenItem(getTimeLineCards());

        if (!isGitEnabled()){
            builder.add(getGitUnavailableInfo());
        }
        else{
            builder
                    .add(getLocalRepoInfo())
                    .add(getLocalChangesInfo())
                    .add(getLocalCommitsInfo())
                    .add(getRemoteLastCommitInfo())
                    .add(getRemoteRepoInfo());
        }

        return builder.build();
    }

    private DocumentSectionData getGitUnavailableInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Git Info")
                .setIcon(R.string.gmd_assignment_late)
                .setOverview("UNAVAILABLE")
                .add("Git command was not reachable on build time or your project is not in a Git repository.")
                .build();
        return group;
    }

    public FlexData getTimeLineCards() {
        LinearGroupFlexData result = new LinearGroupFlexData();

        TimelineFlexData localTimeline;
        boolean hasLocalChanges = gitInfo.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        if (!hasLocalChanges) {
            localTimeline = TimelineFlexHelper.buildRepoItem(TimelineFlexData.Position.START,
                    R.color.iadt_text_low,
                    R.string.gmd_assignment_late,
                    "No local files changed",
                    -1,
                    null,
                    null);
        }
        else{
            int file_changes_count = gitInfo.getInt(GitInfo.LOCAL_CHANGES_COUNT);
            int file_untracked_count = gitInfo.getInt(GitInfo.LOCAL_UNTRACKED_COUNT);
            int unstaggedCount = file_changes_count + file_untracked_count;

            String title = "Local files changed";
            String message = file_untracked_count + " untracked files\n" + gitInfo.getString(GitInfo.LOCAL_CHANGES_STATS);
            List<Object> buttons = new ArrayList<>();
            buttons.add(new ButtonBorderlessFlexData("View Files",
                    R.drawable.ic_format_list_bulleted_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_LOCAL_CHANGES_TXT_FILE);
                            String params = SourceDetailScreen.buildInternalParams(path);
                            OverlayService.performNavigation(SourceDetailScreen.class, params);
                        }
                    }));

            buttons.add(new ButtonBorderlessFlexData("View Diffs",
                    R.drawable.ic_code_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_LOCAL_CHANGES_DIFF_FILE);
                            String params = SourceDetailScreen.buildInternalParams(path);
                            OverlayService.performNavigation(SourceDetailScreen.class, params);
                        }
                    }));

            localTimeline = TimelineFlexHelper.buildRepoItem(TimelineFlexData.Position.START,
                    R.color.rally_orange,
                    R.string.gmd_assignment_late,
                    title,
                    unstaggedCount,
                    message,
                    buttons);
        }
        result.add(localTimeline);


        TimelineFlexData localRepoTimeline;
        String local_commits = gitInfo.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        if (!hasLocalCommits) {
            localRepoTimeline = TimelineFlexHelper.buildRepoItem(TimelineFlexData.Position.MIDDLE,
                    R.color.iadt_text_low,
                    R.string.gmd_assignment_ind,
                    "No local commits",
                    -1,
                    "",
                    null);
        }
        else{
            List<Object> localRepoButtons = new ArrayList<>();
            localRepoButtons.add(new ButtonBorderlessFlexData("View Commits",
                    R.drawable.ic_format_list_bulleted_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_LOCAL_BRANCH_TXT_FILE);
                            String params = SourceDetailScreen.buildInternalParams(path);
                            OverlayService.performNavigation(SourceDetailScreen.class, params);
                        }
                    }));

            localRepoButtons.add(new ButtonBorderlessFlexData("View Diffs",
                    R.drawable.ic_code_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_LOCAL_BRANCH_DIFF_FILE);
                            String params = SourceDetailScreen.buildInternalParams(path);
                            OverlayService.performNavigation(SourceDetailScreen.class, params);
                        }
                    }));

            localRepoTimeline = TimelineFlexHelper.buildRepoItem(TimelineFlexData.Position.MIDDLE,
                    R.color.rally_yellow,
                    R.string.gmd_assignment_ind,
                    "Local commits",
                    local_commits_count,
                    "Branch: " + gitInfo.getString(GitInfo.LOCAL_BRANCH)
                            + "\nTotal commits: " + gitInfo.getString(GitInfo.LOCAL_BRANCH_COUNT),
                    localRepoButtons);
        }
        result.add(localRepoTimeline);

        boolean hasRemote = gitInfo.getBoolean(GitInfo.HAS_REMOTE);
        boolean hasTag = !TextUtils.isEmpty(gitInfo.getString(GitInfo.TAG_NAME));

        TimelineFlexData remoteRepoTimeline;
        if (!hasRemote) {
            remoteRepoTimeline = TimelineFlexHelper.buildRepoItem(
                    hasTag ? TimelineFlexData.Position.MIDDLE : TimelineFlexData.Position.END,
                    R.color.iadt_text_low,
                    R.string.gmd_assignment,
                    "No remote repo",
                    -1,
                    "",
                    null);
        }
        else{
            List<Object> remoteRepoButtons = new ArrayList<>();
            remoteRepoButtons.add(new ButtonBorderlessFlexData("View Commits",
                    R.drawable.ic_format_list_bulleted_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_REMOTE_BRANCH_TXT_FILE);
                            String params = SourceDetailScreen.buildInternalParams(path);
                            OverlayService.performNavigation(SourceDetailScreen.class, params);
                        }
                    }));

            remoteRepoTimeline = TimelineFlexHelper.buildRepoItem(TimelineFlexData.Position.MIDDLE,
                    R.color.material_green,
                    R.string.gmd_assignment, //TODO: why is not integration_instructions??
                    "Remote branch",
                    0, //gitInfo.getInt(GitInfo.REMOTE_BRANCH_COUNT),
                    "Branch: " + gitInfo.getString(GitInfo.REMOTE_BRANCH)
                            + "\nTotal commits: " + gitInfo.getString(GitInfo.REMOTE_BRANCH_COUNT),
                    remoteRepoButtons);
        }
        result.add(remoteRepoTimeline);

        if (hasRemote) {
            List<Object> remoteHeadButtons = new ArrayList<>();
            remoteHeadButtons.add(new ButtonBorderlessFlexData("View Commits",
                    R.drawable.ic_format_list_bulleted_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_REMOTE_BRANCH_TXT_FILE);
                            String params = SourceDetailScreen.buildInternalParams(path);
                            OverlayService.performNavigation(SourceDetailScreen.class, params);
                        }
                    }));

            int remoteHeadRelativeDistance = gitInfo.getInt(GitInfo.REMOTE_HEAD_DISTANCE) - gitInfo.getInt(GitInfo.REMOTE_BRANCH_DISTANCE);
            TimelineFlexData remoteHeadTimeline = TimelineFlexHelper.buildRepoItem(
                    hasTag ? TimelineFlexData.Position.MIDDLE : TimelineFlexData.Position.END,
                    R.color.iadt_text_high,
                    R.string.gmd_assignment_turned_in,
                    "Remote head",
                    -remoteHeadRelativeDistance,
                    "Branch: " + gitInfo.getString(GitInfo.REMOTE_HEAD)
                            + "\nTotal commits: " + gitInfo.getString(GitInfo.REMOTE_HEAD_COUNT),
                    remoteHeadButtons);
            result.add(remoteHeadTimeline);
        }

        TimelineFlexData tagTimeline;

        if (!hasTag) {
            tagTimeline = TimelineFlexHelper.buildRepoItem(TimelineFlexData.Position.END,
                    R.color.iadt_text_low,
                    R.string.gmd_assignment_ind,
                    "No tag",
                    null,
                    "",
                    null);
        }
        else{
            int tagRelativeDistance = gitInfo.getInt(GitInfo.TAG_DISTANCE) - gitInfo.getInt(GitInfo.REMOTE_BRANCH_DISTANCE);
            tagTimeline = TimelineFlexHelper.buildRepoItem(TimelineFlexData.Position.END,
                    R.color.iadt_primary,
                    R.string.gmd_local_offer,
                    "Last tag",
                    -tagRelativeDistance,
                    "Tag: " + gitInfo.getString(GitInfo.TAG_NAME)
                            + "\nCommit Id: " + gitInfo.getString(GitInfo.TAG_LAST_COMMIT),
                    null);
        }
        result.add(tagTimeline);

        return result;
    }

    public DocumentSectionData getRemoteRepoInfo() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Remote repo")
                .setIcon(R.string.gmd_public);

        if (TextUtils.isEmpty(gitInfo.getString(GitInfo.REMOTE_NAME))){
            group.setOverview("N/A");
            group.add("There are not remote repository configured.");
            return group.build();
        }

        group.setOverview(gitInfo.getString(GitInfo.LOCAL_BRANCH) + "-" + gitInfo.getString(GitInfo.TAG_NAME));
        group   .add("Name", gitInfo.getString(GitInfo.REMOTE_NAME))
                .add("Url", gitInfo.getString(GitInfo.REMOTE_URL))
                .add("")
                .add("Head", gitInfo.getString(GitInfo.REMOTE_HEAD))
                .add("Head commits", gitInfo.getString(GitInfo.REMOTE_HEAD_COUNT))
                .add("")
                .add("Branch", gitInfo.getString(GitInfo.LOCAL_BRANCH))
                .add("Branch commits", gitInfo.getString(GitInfo.REMOTE_BRANCH_COUNT))
                .add("")
                .add("Tag", gitInfo.getString(GitInfo.TAG_NAME))
                .add("Tag distance", gitInfo.getString(GitInfo.TAG_DISTANCE))
                .add("Tag last commit", gitInfo.getString(GitInfo.TAG_LAST_COMMIT))
                .add("Tag dirty", gitInfo.getString(GitInfo.TAG_DIRTY))
                .add("Tag Description", gitInfo.getString(GitInfo.TAG_DESCRIPTION))
        ;

        group.addButton(new ButtonBorderlessFlexData("Browse repo",
                R.drawable.ic_public_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        ExternalIntentUtils.viewUrl(gitInfo.getString(GitInfo.REMOTE_URL));
                    }
                }));
        return group.build();
    }

    public DocumentSectionData getRemoteLastCommitInfo() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Last remote commit")
                .setIcon(R.string.gmd_assignment_turned_in);

        String commitId = Humanizer.removeHead(gitInfo.getString(GitInfo.REMOTE_LAST_COMMIT), "commit ");
        commitId = Humanizer.truncate(commitId, 8, "");
        group.setOverview(commitId)
                .add("Last Fetch", DateUtils.formatFull(gitInfo.getLong(GitInfo.REMOTE_LAST_FETCH_TIME)))
                .add("Last commit", gitInfo.getString(GitInfo.LAST_COMMIT_TIME))
                .add("")
                .add(gitInfo.getString(GitInfo.REMOTE_LAST_COMMIT));
        return group.build();
    }

    public DocumentSectionData getLocalRepoInfo() {
        String userEmail = gitInfo.getString(GitInfo.USER_EMAIL);
        String localBranch = gitInfo.getString(GitInfo.LOCAL_BRANCH);
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Local repo")
                .setIcon(R.string.gmd_computer)
                .setOverview(localBranch)
                .add("Head branch", localBranch)
                .add("Head commits count", gitInfo.getString(GitInfo.LOCAL_BRANCH_COUNT));

        if (gitInfo.getBoolean(GitInfo.HAS_REMOTE)){
            group.add("Distance to remote branch", gitInfo.getString(GitInfo.REMOTE_BRANCH_DISTANCE))
                    .add("Distance to remote head", gitInfo.getString(GitInfo.REMOTE_HEAD_DISTANCE));
        }

        group.add("")
                .add("Tag", Humanizer.unavailable(gitInfo.getString(GitInfo.TAG_NAME), "N/A"))
                .add("Tag distance", gitInfo.getString(GitInfo.TAG_DISTANCE))
                .add()
                .add("User name", gitInfo.getString(GitInfo.USER_NAME))
                .add("User email", userEmail)
                .add("")
                .add("VCS", "Git")
                .add("Version", gitInfo.getString(GitInfo.VERSION))
                .add("Path", gitInfo.getString(GitInfo.PATH));
        
        if (!TextUtils.isEmpty(userEmail)){
            group.addButton(new ButtonBorderlessFlexData("Write Email",
                    R.drawable.ic_email_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            ExternalIntentUtils.composeEmail(gitInfo.getString(GitInfo.USER_EMAIL), "Email from IADT");
                        }
                    }));
        }
        return group.build();
    }

    public DocumentSectionData getLocalCommitsInfo() {
        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Local commits")
                .setIcon(R.string.gmd_assignment_ind);

        boolean hasLocalCommits = gitInfo.getBoolean(GitInfo.HAS_LOCAL_COMMITS);
        if (!hasLocalCommits){
            group.setOverview("CLEAN");
            group.add("No local commits");
            return group.build();
        }


        String local_commits = gitInfo.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        String local_log = gitInfo.getString(GitInfo.LOCAL_BRANCH_GRAPH);
        group.setOverview(local_commits_count + " commits ahead");
        group.add(local_log);


        group.addButton(new ButtonBorderlessFlexData("View Files",
                R.drawable.ic_format_list_bulleted_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_LOCAL_BRANCH_TXT_FILE);
                        String params = SourceDetailScreen.buildInternalParams(path);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));

        group.addButton(new ButtonBorderlessFlexData("View Diffs",
                R.drawable.ic_code_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_LOCAL_BRANCH_DIFF_FILE);
                        String params = SourceDetailScreen.buildInternalParams(path);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));

        return group.build();
    }

    public DocumentSectionData getLocalChangesInfo() {
        boolean hasLocalChanges = gitInfo.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        int file_changes_count = gitInfo.getInt(GitInfo.LOCAL_CHANGES_COUNT);
        int file_untracked_count = gitInfo.getInt(GitInfo.LOCAL_UNTRACKED_COUNT);

        DocumentSectionData.Builder group = new DocumentSectionData.Builder("Uncommitted changes")
                .setIcon(R.string.gmd_assignment_late)
                .setExpandable(false);

        if (!hasLocalChanges){
            group.setOverview("CLEAN");
            group.add("No local changes");
            return group.build();
        }

        group.setOverview(Humanizer.plural(file_changes_count + file_untracked_count, "file"));
        group.add(gitInfo.getString(GitInfo.LOCAL_CHANGES_STATS));
        group.add(file_untracked_count + " new files (untracked)");


        group.addButton(new ButtonBorderlessFlexData("View Files",
                R.drawable.ic_format_list_bulleted_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_LOCAL_CHANGES_TXT);
                        String params = SourceDetailScreen.buildInternalParams(path);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));

        group.addButton(new ButtonBorderlessFlexData("View Diffs",
                R.drawable.ic_code_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String path = BuildFilesRepository.getBuildFile(sessionId, IadtPath.GIT_LOCAL_CHANGES_DIFF_FILE);
                        String params = SourceDetailScreen.buildInternalParams(path);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));

        return group.build();
    }



    public boolean isGitEnabled() {
        return gitInfo.getBoolean(GitInfo.ENABLED);
    }

    private String getRepoType() {
        if (!isGitEnabled()){
            return "Unavailable";
        }
        return "Git Repository";
    }

    public String getBranchAndTag() {
        if (!isGitEnabled()){
            return "No available";
        }
        String branch = gitInfo.getString(GitInfo.LOCAL_BRANCH);
        String tag = gitInfo.getString(GitInfo.TAG_NAME);
        return String.format("%s %s", branch, tag);
    }

    public String getLocalOverview(){
        if (!isGitEnabled()){
            return "No Git repository";
        }
        String local_commits = gitInfo.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitInfo.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        int file_changes_count = gitInfo.getInt(GitInfo.LOCAL_CHANGES_COUNT);

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

    public Boolean hasLocalCommitsOrChanges(){
        if (!isGitEnabled()){
            return null;
        }
        boolean hasLocalCommits = gitInfo.getBoolean(GitInfo.HAS_LOCAL_COMMITS);
        boolean hasLocalChanges = gitInfo.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        if (!hasLocalCommits && !hasLocalChanges){
            return false;
        }
        return true;
    }

}
