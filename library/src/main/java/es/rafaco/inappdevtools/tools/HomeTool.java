package es.rafaco.inappdevtools.tools;

import es.rafaco.inappdevtools.view.components.DecoratedToolInfo;
import es.rafaco.inappdevtools.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.view.overlay.screens.home.HomeScreen;

public class HomeTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return null;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return HomeScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        return null;
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}
