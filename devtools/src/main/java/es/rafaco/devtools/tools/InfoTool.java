package es.rafaco.devtools.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.components.DecoratedToolInfo;

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
        out += helper.getFormattedAppLong();
        out += "\n";
        out += helper.getFormattedDeviceLong();

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
