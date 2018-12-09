package es.rafaco.inappdevtools.tools;

import es.rafaco.inappdevtools.R;
import es.rafaco.inappdevtools.view.overlay.screens.friendlylog.FriendlyLogHelper;
import es.rafaco.inappdevtools.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.inappdevtools.view.components.DecoratedToolInfo;
import es.rafaco.inappdevtools.view.components.DecoratedToolInfoAdapter;

public class FriendlyLogTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return FriendlyLogHelper.class;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return FriendlyLogScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        NavigationStep step = new NavigationStep(getMainScreen(), null);
        DecoratedToolInfo info = new DecoratedToolInfo(
                "Friendly Log",
                "Designed for humans :)",
                R.color.rally_blue_med,
                4,
                step);
        return  info;
    }

    @Override
    public void updateHomeInfo(DecoratedToolInfoAdapter adapter) {
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}
