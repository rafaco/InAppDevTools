package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

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
                .add(Humanizer.newLine() + IadtController.get().getDatabase().getOverview())
                .build();
    }

    private InfoGroup getCompileConfig() {
        return new InfoGroup.Builder("Compile config")
                .add(new JsonAssetHelper(context, JsonAsset.COMPILE_CONFIG).getAll())
                .build();
    }

    public InfoGroup getDevToolsInfo() {
        InfoGroup group = new InfoGroup.Builder("InAppDevTools library")
                .add("Version", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")
                .add("Build type", BuildConfig.BUILD_TYPE)
                .add("Flavor", BuildConfig.FLAVOR)
                .build();
        return group;
    }
}
