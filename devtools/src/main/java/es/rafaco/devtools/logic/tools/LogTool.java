package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
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
        return new DecoratedToolInfo( LogScreen.class,
                getName(),
                "Live log is available. Automatic log to disk coming soon.",
                2,
                R.color.rally_white);
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return new DecoratedToolInfo(LogScreen.class,
                getName(),
                "Include full log.",
                2,
                R.color.rally_white);
    }
}