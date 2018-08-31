package es.rafaco.devtools.logic.tools;

import android.os.Build;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;

public class InfoTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return null;
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
        out += Build.BRAND + " " + Build.MODEL;
        out += "\n";
        out += "Android " + Build.VERSION.RELEASE + " (" + helper.getVersionCodeName() + ")";

        return new DecoratedToolInfo(InfoScreen.class,
                getName(),
                out,
                1,
                R.color.rally_blue);
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return new DecoratedToolInfo(InfoScreen.class,
                getName(),
                "Include all. Brief info is always added",
                1,
                R.color.rally_blue);
    }
}
