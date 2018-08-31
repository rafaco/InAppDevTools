package es.rafaco.devtools.logic.tools;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.DecoratedToolInfo;
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
        final DecoratedToolInfo info = new DecoratedToolInfo(ScreensScreen.class,
                getName(),
                "No screen saved.",
                3,
                R.color.rally_purple);

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                final int count = DevTools.getDatabase().screenDao().count();
                if (count > 0){
                    //TODO: updateHomeInfoContent?????
                    //getScreenManager().updateHomeInfoContent(ScreensScreen.class, count + " screens saved." );
                }
            }
        });

        return  info;
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return new DecoratedToolInfo(InfoScreen.class,
                getName(),
                "Included last one of " + DevTools.getDatabase().screenDao().count(),
                3,
                R.color.rally_purple);
    }
}
