package es.rafaco.inappdevtools.library.tools;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfo;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;

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
                "Logcat",
                "Live log is available.",
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