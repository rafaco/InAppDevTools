package es.rafaco.devtools.logic.tools;

import java.util.List;

import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Anr;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.CrashHelper;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;

import static es.rafaco.devtools.DevTools.getDatabase;

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
    public void updateHomeInfo(DecoratedToolInfoAdapter adapter) {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                List<Crash> crash = getDatabase().crashDao().getAll();
                List<Anr> anrs = getDatabase().anrDao().getAll();

                String message = crash.size() + " crash detected (hace 5 min)";
                message += "\n";
                message += anrs.size() + " ANRs detected (hace 3 dias)";

                final String finalMessage = message;
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //adapter.update();
                    }
                });
            }
        });
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}
