package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;

public class ToolsInfoHelper extends AbstractInfoHelper {

    public ToolsInfoHelper(Context context) {
        super(context);
    }

    @Override
    public String getOverview() {
        return null;
    }

    @Override
    public InfoReport getInfoReport() {
        return new InfoReport.Builder("")
                .add(getDevToolsInfo())
                .add(getCompileConfig())
                .add(DevTools.getDatabase().getOverview())
                .build();
    }

    private InfoGroup getCompileConfig() {
        return new InfoGroup.Builder("Compile config")
                .add(new CompileConfig(context).getAll())
                .build();
    }

    public InfoGroup getDevToolsInfo() {
        CompileConfig buildConfig = new CompileConfig(context);
        InfoGroup group = new InfoGroup.Builder("InAppDevTools")
                .add("Version", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")
                .add("Build type", BuildConfig.BUILD_TYPE)
                .add("Flavor", BuildConfig.FLAVOR)
                .add(CompileConfigFields.EXT_ENABLED, buildConfig.getString(CompileConfigFields.EXT_ENABLED))
                .add(CompileConfigFields.EXT_EMAIL, buildConfig.getString(CompileConfigFields.EXT_EMAIL))
                .build();
        return group;
    }
}
