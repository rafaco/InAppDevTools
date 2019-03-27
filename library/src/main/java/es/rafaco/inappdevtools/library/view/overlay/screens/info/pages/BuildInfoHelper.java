package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;

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
        return AppBuildConfig.getStringValue(context, BuildConfigFields.BUILD_TYPE)
                + AppBuildConfig.getStringValue(context, BuildConfigFields.FLAVOR)
                + " build "
                + DateUtils.getElapsedTimeLowered(
                        Long.parseLong(buildConfig.getString(CompileConfigFields.BUILD_TIME)));
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

        group.add("Git url", "TODO")
                .add("Branch", gitConfig.getString("BRANCH"))
                .add("Tag", gitConfig.getString("TAG"))
                .add("Ahead from tag", "TODO")
                .add("Last annotation", "\n" +
                        gitConfig.getChildString("LAST_COMMIT", "LONG").replace("\n\n", "\n-> "));
        return group.build();
    }

    public InfoGroup getLocalChangesInfo() {
        InfoGroup.Builder group = new InfoGroup.Builder("Local changes");
        if (!gitConfig.getChildBoolean("LOCAL_CHANGES", "ISDIRTY")){
            group.add("No local changes");
            return group.build();
        }

        group.add("Local commits", "TODO")
                .add("Local commits count", "TODO")
                .add("Local files changed", "\n" +
                        gitConfig.getChildString("LOCAL_CHANGES", "STATUS"));

        return group.build();
    }
}
