package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;
import android.support.annotation.NonNull;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.BuildConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoReport;

public class BuildInfoHelper {
    Context context;

    public BuildInfoHelper(Context context) {
        this.context = context;
    }

    @NonNull
    public InfoReport getReport() {
        return new InfoReport.Builder("")
                .add(getBuildInfo())
                .add("Git config", DevTools.getSourcesManager().getContent(
                        "assets/inappdevtools/git_config.json"))
                .build();
    }

    public InfoGroup getBuildInfo() {
        CompileConfig buildConfig = new CompileConfig(context);
        InfoGroup group = new InfoGroup.Builder("Build Info")
                .add("Elapsed time", DateUtils.getElapsedTime(Long.parseLong(buildConfig.getString(CompileConfigFields.BUILD_TIME))))
                .add("Build time", buildConfig.getString(CompileConfigFields.BUILD_TIME_UTC))
                .add("Build type", AppBuildConfig.getStringValue(context, BuildConfigFields.BUILD_TYPE))
                .add("Flavor", AppBuildConfig.getStringValue(context, BuildConfigFields.FLAVOR))
                .build();
        return group;
    }
}
