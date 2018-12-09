package es.rafaco.inappdevtools.tools;

import es.rafaco.inappdevtools.R;
import es.rafaco.inappdevtools.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.view.overlay.screens.info.InfoHelper;
import es.rafaco.inappdevtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.view.components.DecoratedToolInfo;

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
