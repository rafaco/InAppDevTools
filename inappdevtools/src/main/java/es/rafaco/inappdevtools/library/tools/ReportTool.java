package es.rafaco.inappdevtools.library.tools;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.DecoratedToolInfo;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;

public class ReportTool extends Tool {
    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return ReportHelper.class;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return ReportScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        NavigationStep step = new NavigationStep(ReportScreen.class, null);
        return new DecoratedToolInfo(
                "Send a Report",
                "Send a bug, exception or feedback straight to the developers. Choose which attachments to include and add your own description or steps to reproduce it later in GMail.",
                R.color.rally_green,
                3,
                step);
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}

