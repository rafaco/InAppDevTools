package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
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
        NavigationStep step = new NavigationStep(ErrorsScreen.class, null);
        DecoratedToolInfo info = new DecoratedToolInfo(
                "Error log",
                "Crash handler activated." + "\n" + "ANR handler activated.",
                R.color.rally_orange,
                4,
                step);
        return  info;
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}
