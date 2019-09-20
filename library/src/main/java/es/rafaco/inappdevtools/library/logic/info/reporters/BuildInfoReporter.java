package es.rafaco.inappdevtools.library.logic.info.reporters;

import android.content.Context;
import android.text.TextUtils;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import es.rafaco.inappdevtools.library.logic.config.BuildInfo;
import es.rafaco.inappdevtools.library.logic.config.GitInfo;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfigFields;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.storage.files.PluginList;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
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
                .add(getBuildInfo())
                .add(getRepositoryInfo())
                .add(getLocalChangesInfo())
                .build();
    }



    public InfoGroupData getBuilderInfo() {
        InfoGroupData group = new InfoGroupData.Builder("Builder")
                .add("User name", buildInfo.getString(BuildInfo.USER_NAME))
                .add("User email", buildInfo.getString(BuildInfo.USER_EMAIL))
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
        InfoGroupData.Builder group = new InfoGroupData.Builder("Remote repository");

        if (!isGitEnabled()){
            group.add("No git repository found at build folder");
            return group.build();
        }

        group.add("Git url", gitConfig.getString(GitInfo.REMOTE_URL))
                .add("Branch", gitConfig.getString(GitInfo.LOCAL_BRANCH))
                .add("Tag", gitConfig.getString(GitInfo.TAG))
                .add("Tag Status", gitConfig.getString(GitInfo.INFO))
                .add(" - Last commit:")
                .add(gitConfig.getString(GitInfo.REMOTE_LAST).replace("\n\n", "\n-> "));
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
        String host = buildInfo.getString(BuildInfo.HOST_NAME);
        String time = getFriendlyElapsedTime();
        return String.format("%s %s", host, time);
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
                :  Humanizer.toCapitalCase(flavor) + Humanizer.toCapitalCase(buildType);
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

    public InfoGroupData getLocalChangesInfo() {
        InfoGroupData.Builder group = new InfoGroupData.Builder("Local repository");

        String local_commits = gitConfig.getString(GitInfo.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitConfig.getBoolean(GitInfo.HAS_LOCAL_CHANGES);
        String file_status = gitConfig.getString(GitInfo.LOCAL_CHANGES);
        int file_changes_count = Humanizer.countLines(file_status);

        if (!hasLocalCommits && !hasLocalChanges){
            group.add("No local commits or changes");
            return group.build();
        }

        group.add(local_commits_count + " commits ahead"
                + "\n" + local_commits)
                .add()
                .add(file_changes_count + " files changed"
                        + "\n" + file_status);

        return group.build();
    }

    public boolean isGitEnabled() {
        return gitConfig.getBoolean(GitInfo.ENABLED);
    }

}
