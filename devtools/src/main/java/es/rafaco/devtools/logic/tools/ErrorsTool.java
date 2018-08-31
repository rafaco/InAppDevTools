package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.commands.CommandsScreen;
import es.rafaco.devtools.view.overlay.screens.errors.CrashHelper;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;

public class ErrorsTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return CrashHelper.class;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return ErrorsScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        DecoratedToolInfo info = new DecoratedToolInfo(ErrorsScreen.class,
                getName(),
                "Crash handler activated." + "\n" + "ANR handler activated.",
                4,
                R.color.rally_orange);
        return  info;
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}
