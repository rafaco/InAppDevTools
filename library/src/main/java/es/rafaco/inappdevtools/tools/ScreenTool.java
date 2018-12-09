package es.rafaco.inappdevtools.tools;

import es.rafaco.inappdevtools.DevTools;
import es.rafaco.inappdevtools.R;
import es.rafaco.inappdevtools.view.components.DecoratedToolInfo;
import es.rafaco.inappdevtools.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.view.overlay.screens.info.InfoScreen;

public class ScreenTool extends Tool {
    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return null;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return null;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        return null;
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        NavigationStep step = new NavigationStep(InfoScreen.class, null);
        return new DecoratedToolInfo(
                getName(),
                "Included last one of " + DevTools.getDatabase().screenDao().count(),
                R.color.rally_purple,
                3,
                step);
    }
}
