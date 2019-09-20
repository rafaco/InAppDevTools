package es.rafaco.inappdevtools.library.logic.info.reporters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.storage.files.JsonAsset;
import es.rafaco.inappdevtools.library.storage.files.JsonAssetHelper;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.storage.files.PluginList;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ToolsInfoReporter extends AbstractInfoReporter {

    public ToolsInfoReporter(Context context) {
        this(context, InfoReport.TOOLS);
    }

    public ToolsInfoReporter(Context context, InfoReport report) {
        super(context, report);
    }

    @Override
    public String getOverview() {
        return "Iadt v" + getVersionFormatted() + "\n"
                + getFriendlyBuildType();
    }

    @Override
    public InfoReportData getData() {
        return new InfoReportData.Builder(getReport())
                .setOverview(getOverview())
                .add(getLibraryInfo())
                .add(Humanizer.newLine() + IadtController.get().getDatabase().getOverview())
                .add(getBuildConfig())
                .add(getBuildInfo())
                .build();
    }

    private InfoGroupData getBuildInfo() {
        return new InfoGroupData.Builder("Generated BuildInfo")
                .add(new JsonAssetHelper(context, JsonAsset.BUILD_INFO).getAll())
                .build();
    }

    private InfoGroupData getBuildConfig() {
        return new InfoGroupData.Builder("Generated BuildConfig")
                .add(new JsonAssetHelper(context, JsonAsset.BUILD_CONFIG).getAll())
                .build();
    }

    public InfoGroupData getLibraryInfo() {
        InfoGroupData group = new InfoGroupData.Builder("InAppDevTools")
                .add("Library version", getVersionFormatted())
                .add("Build type", BuildConfig.BUILD_TYPE)
                .add("Flavor", BuildConfig.FLAVOR)
                .add("Plugin version", PluginList.getIadtVersion())
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
