package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;

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
