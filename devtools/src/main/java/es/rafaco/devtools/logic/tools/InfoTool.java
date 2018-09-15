package es.rafaco.devtools.logic.tools;

import android.os.Build;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;

public class InfoTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return InfoHelper.class;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return InfoScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {

        InfoHelper helper = new InfoHelper();
        String out = "";
        out += helper.getAppName() + " "  + helper.getPackageInfo().versionName + " (" + helper.getPackageInfo().versionCode + ")";
        out += "\n";
        out += Build.BRAND + " " + Build.MODEL + ". ";
        out += "Android " + Build.VERSION.RELEASE + " (" + helper.getVersionCodeName() + ")";

        NavigationStep step = new NavigationStep(InfoScreen.class, null);
        return new DecoratedToolInfo(
                "Info",
                out,
                R.color.rally_blue,
                1,
                step);
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        NavigationStep step = new NavigationStep(InfoScreen.class, null);
        return new DecoratedToolInfo(
                getName(),
                "Include all. Brief info is always added",
                R.color.rally_blue,
                1,
                step);
    }
}
