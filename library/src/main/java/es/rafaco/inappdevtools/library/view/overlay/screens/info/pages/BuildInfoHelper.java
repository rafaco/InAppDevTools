package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;
import android.text.TextUtils;

//#ifdef MODERN
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.BuildConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class BuildInfoHelper extends AbstractInfoHelper {

    CompileConfig buildConfig;
    CompileConfig gitConfig;

    public BuildInfoHelper(Context context) {
        super(context);
        buildConfig = new CompileConfig(context);
        gitConfig = new CompileConfig(context, "inappdevtools/git_config.json");
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
                .add("Build time", buildConfig.getString(CompileConfigFields.BUILD_TIME_UTC))
                .add("Build type", AppBuildConfig.getStringValue(context, BuildConfigFields.BUILD_TYPE))
                .add("Flavor", AppBuildConfig.getStringValue(context, BuildConfigFields.FLAVOR))
                .build();
        return group;
    }

    public InfoGroup getRepositoryInfo() {
        InfoGroup.Builder group = new InfoGroup.Builder("Repository");

        if (!isGitEnabled()){
            group.add("No git found at build folder");
            return group.build();
        }

        group.add("Git url", gitConfig.getString("REMOTE"))
                .add("Branch", gitConfig.getString("BRANCH"))
                .add("Tag", gitConfig.getString("INFO"))
                .add()
                .add(gitConfig.getChildString("LAST_COMMIT", "LONG").replace("\n\n", "\n-> "));
        return group.build();
    }

    public String getRepositoryOverview() {
        if (!isGitEnabled()){
            return null;
        }
        return gitConfig.getString("BRANCH") + " " + gitConfig.getString("INFO");
    }

    public String getBuildOverview() {
        String build = getFriendlyBuildType();
        String time = getFriendlyElapsedTime();
        return String.format("%s build, %s", build, time);
    }

    public String getFriendlyElapsedTime() {
        return DateUtils.getElapsedTimeLowered(
                Long.parseLong(buildConfig.getString(CompileConfigFields.BUILD_TIME)));
    }

    @NonNull
    public String getFriendlyBuildType() {
        String flavor = AppBuildConfig.getStringValue(context, BuildConfigFields.FLAVOR);
        String buildType = AppBuildConfig.getStringValue(context, BuildConfigFields.BUILD_TYPE);
        String build = TextUtils.isEmpty(flavor) ? buildType
                :  flavor + Humanizer.capital(buildType);
        build = Humanizer.capital(build);
        return build;
    }

    public String getLocalOverview(){
        String local_commits = gitConfig.getString("LOCAL_COMMITS");
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitConfig.getChildBoolean("LOCAL_CHANGES", "ISDIRTY");
        String file_status = gitConfig.getChildString("LOCAL_CHANGES", "STATUS");
        int file_changes_count = Humanizer.countLines(file_status);

        if (!hasLocalCommits && !hasLocalChanges){
            return "No local changes";
        }

        String result = "";
        if (hasLocalCommits){
            result += local_commits_count + " commit ahead ";
        }
        if(hasLocalChanges){
            if (hasLocalCommits) result += " and ";
            result += file_changes_count + " files changed";
        }

        return result;
    }

    public InfoGroup getLocalChangesInfo() {
        InfoGroup.Builder group = new InfoGroup.Builder("Local changes");

        String local_commits = gitConfig.getString("LOCAL_COMMITS");
        int local_commits_count = Humanizer.countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitConfig.getChildBoolean("LOCAL_CHANGES", "ISDIRTY");
        String file_status = gitConfig.getChildString("LOCAL_CHANGES", "STATUS");
        int file_changes_count = Humanizer.countLines(file_status);

        if (!hasLocalCommits && !hasLocalChanges){
            group.add("No local changes");
            return group.build();
        }

        group.add("Annotations", local_commits_count + " ahead"
                + "\n" + local_commits)
                .add()
                .add("Files", file_changes_count + " changed"
                        + "\n" + gitConfig.getChildString("LOCAL_CHANGES", "STATUS"));

        return group.build();
    }

    public boolean isGitEnabled() {
        return gitConfig.getBoolean("ENABLED");
    }

}
