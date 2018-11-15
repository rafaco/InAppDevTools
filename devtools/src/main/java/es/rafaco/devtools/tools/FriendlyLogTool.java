package es.rafaco.devtools.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.screens.friendlylog.FriendlyLogHelper;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.devtools.view.components.DecoratedToolInfo;
import es.rafaco.devtools.view.components.DecoratedToolInfoAdapter;

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
