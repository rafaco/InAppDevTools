package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogHelper;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;

public class LogTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return LogHelper.class;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return LogScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        NavigationStep step = new NavigationStep(LogScreen.class, null);
        return new DecoratedToolInfo(
                getName(),
                "Live log is available. Automatic log to disk coming soon.",
                R.color.rally_yellow,
                2,
                step);
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        NavigationStep step = new NavigationStep(LogScreen.class, null);
        return new DecoratedToolInfo(
                getName(),
                "Include full log.",
                R.color.rally_yellow,
                2,
                step);
    }
}