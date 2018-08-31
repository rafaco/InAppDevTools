package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.report.ReportHelper;
import es.rafaco.devtools.view.overlay.screens.report.ReportScreen;

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
        return new DecoratedToolInfo(ReportScreen.class,
                getName(), //"Send a Report"
                "Send a bug, exception or feedback straight to the developers. Choose which attachments to include and add your own description or steps to reproduce it later in GMail.",
                3,
                R.color.rally_green);
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}

