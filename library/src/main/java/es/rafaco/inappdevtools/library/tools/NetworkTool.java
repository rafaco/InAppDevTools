package es.rafaco.inappdevtools.library.tools;

import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfo;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfoAdapter;

import static es.rafaco.inappdevtools.library.DevTools.getDatabase;

public class NetworkTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return NetworkHelper.class;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return NetworkScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        NavigationStep step = new NavigationStep(NetworkScreen.class, null);
        DecoratedToolInfo info = new DecoratedToolInfo(
                "Inspect Network",
                "Retrofit interceptor enabled",
                R.color.rally_purple,
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
