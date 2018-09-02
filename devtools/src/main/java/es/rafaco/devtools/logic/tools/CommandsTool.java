package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.commands.CommandsScreen;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;

public class CommandsTool extends Tool {
    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return null;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return CommandsScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        NavigationStep step = new NavigationStep(CommandsScreen.class, null);
        DecoratedToolInfo info = new DecoratedToolInfo(
                getName(),
                "Type shell commands, run a predefined ones or configure your owns.",
                R.color.rally_white,
                4,
                step);
        return  info;
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}
