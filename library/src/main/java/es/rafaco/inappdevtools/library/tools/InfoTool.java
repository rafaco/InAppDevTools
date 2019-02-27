package es.rafaco.inappdevtools.library.tools;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfo;

public class InfoTool extends Tool {

    @Override
    protected void onRegister() {
        //Nothing needed
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
