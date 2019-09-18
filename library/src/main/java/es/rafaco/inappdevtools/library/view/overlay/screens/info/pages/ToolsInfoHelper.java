package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.IadtController;
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
        return "Iadt v" + getVersionFormatted() + "\n"
                + getFriendlyBuildType();
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
                .add("Version", getVersionFormatted())
                .add("Build type", BuildConfig.BUILD_TYPE)
                .add("Flavor", BuildConfig.FLAVOR)
                .build();
        return group;
    }

    private String getVersionFormatted() {
        return BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
    }

    @NonNull
    public String getFriendlyBuildType() {
        String flavor = BuildConfig.FLAVOR;
        String buildType = BuildConfig.BUILD_TYPE;
        String build = TextUtils.isEmpty(flavor) ? Humanizer.toCapitalCase(buildType)
                :  Humanizer.toCapitalCase(flavor) + Humanizer.toCapitalCase(buildType);
        return build;
    }
}
