package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.w3c.dom.Text;

import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.BuildConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;

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
        String firstLine = "Build "
                + DateUtils.getElapsedTimeLowered(
                        Long.parseLong(buildConfig.getString(CompileConfigFields.BUILD_TIME)))
                + " as "
                + AppBuildConfig.getStringValue(context, BuildConfigFields.BUILD_TYPE)
                + AppBuildConfig.getStringValue(context, BuildConfigFields.FLAVOR);

        String secondLine = gitConfig.getString("BRANCH") + " " + gitConfig.getString("INFO");
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

        if (!gitConfig.getBoolean("ENABLED")){
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

    public InfoGroup getLocalChangesInfo() {
        InfoGroup.Builder group = new InfoGroup.Builder("Local changes");

        String local_commits = gitConfig.getString("LOCAL_COMMITS");
        int local_commits_count = countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitConfig.getChildBoolean("LOCAL_CHANGES", "ISDIRTY");
        String file_status = gitConfig.getChildString("LOCAL_CHANGES", "STATUS");
        int file_changes_count = countLines(file_status);

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

    @NonNull
    private int countLines(String text) {
        if (TextUtils.isEmpty(text)){
            return 0;
        }
        return text.split("\n").length;
    }

    public String getLocalOverview(){
        String local_commits = gitConfig.getString("LOCAL_COMMITS");
        int local_commits_count = countLines(local_commits);
        boolean hasLocalCommits = local_commits_count > 0;
        boolean hasLocalChanges = gitConfig.getChildBoolean("LOCAL_CHANGES", "ISDIRTY");
        String file_status = gitConfig.getChildString("LOCAL_CHANGES", "STATUS");
        int file_changes_count = countLines(file_status);

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
}
