package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;
import android.text.TextUtils;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.logic.config.GitConfig;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfigFields;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.storage.files.PluginList;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class BuildInfoHelper extends AbstractInfoHelper {

    JsonAssetHelper buildConfig;
    JsonAssetHelper gitConfig;

    public BuildInfoHelper(Context context) {
        super(context);

        buildConfig = new JsonAssetHelper(context, JsonAsset.COMPILE_CONFIG);
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
    public InfoReport getInfoReport() {
        return new InfoReport.Builder("")
                .add(getBuildInfo())
                .add(getRepositoryInfo())
                .add(getLocalChangesInfo())
                .build();
    }

    public InfoGroup getBuildInfo() {
        InfoGroup group = new InfoGroup.Builder("Build")
                .add("Host name", buildConfig.getString(Config.HOST_NAME.getKey()))
                .add("Host IP", buildConfig.getString(Config.HOST_ADDRESS.getKey()))
                .add("Build time", buildConfig.getString(Config.BUILD_TIME_UTC.getKey()))
                .add("Build type", AppBuildConfig.getStringValue(context, AppBuildConfigFields.BUILD_TYPE))
                .add("Flavor", AppBuildConfig.getStringValue(context, AppBuildConfigFields.FLAVOR))
                .add("Gradle version", PluginList.getGradleVersion())
                .add("Android plugin", PluginList.getAndroidGradleVersion())
                .add("Iadt plugin", PluginList.getIadtVersion())
                .build();
        return group;
    }

    public InfoGroup getRepositoryInfo() {
        InfoGroup.Builder group = new InfoGroup.Builder("Remote repository");

        if (!isGitEnabled()){
            group.add("No git repository found at build folder");
            return group.build();
        }

        group.add("Git url", gitConfig.getString(GitConfig.REMOTE_URL))
                .add("Branch", gitConfig.getString(GitConfig.LOCAL_BRANCH))
                .add("Tag", gitConfig.getString(GitConfig.TAG))
                .add("Tag Status", gitConfig.getString(GitConfig.INFO))
                .add(" - Last commit:")
                .add(gitConfig.getString(GitConfig.REMOTE_LAST).replace("\n\n", "\n-> "));
        return group.build();
    }

    public String getRepositoryOverview() {
        if (!isGitEnabled()){
            return null;
        }
        String build = getFriendlyBuildType();
        String branch = gitConfig.getString(GitConfig.LOCAL_BRANCH);
        String tag = gitConfig.getString(GitConfig.TAG);

        return String.format("%s from %s %s", build, branch, tag);
    }

    public String getBuildOverview() {
        String host = buildConfig.getString(Config.HOST_NAME.getKey());
        String time = getFriendlyElapsedTime();
        return String.format("%s %s", host, time);
    }

    public String getFriendlyElapsedTime() {
        return Humanizer.getElapsedTimeLowered(
                Long.parseLong(buildConfig.getString(Config.BUILD_TIME.getKey())));
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
        String local_commits = gitConfig.getString(GitConfig.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitConfig.getBoolean(GitConfig.HAS_LOCAL_CHANGES);
        String file_status = gitConfig.getString(GitConfig.LOCAL_CHANGES);
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

    public InfoGroup getLocalChangesInfo() {
        InfoGroup.Builder group = new InfoGroup.Builder("Local repository");

        String local_commits = gitConfig.getString(GitConfig.LOCAL_COMMITS);
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitConfig.getBoolean(GitConfig.HAS_LOCAL_CHANGES);
        String file_status = gitConfig.getString(GitConfig.LOCAL_CHANGES);
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
        return gitConfig.getBoolean(GitConfig.ENABLED);
    }

}
