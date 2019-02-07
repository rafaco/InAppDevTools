package es.rafaco.inappdevtools.library.tools;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.storage.StorageHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.storage.StorageScreen;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfo;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfoAdapter;

public class StorageTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return StorageHelper.class;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return StorageScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        NavigationStep step = new NavigationStep(StorageScreen.class, null);
        DecoratedToolInfo info = new DecoratedToolInfo(
                "Inspect Local Storage",
                "Databases, SharedPrefs and filesystem.",
                R.color.rally_blue_med,
                4,
                step);
        return  info;
    }

    @Override
    public void updateHomeInfo(DecoratedToolInfoAdapter adapter) {
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}
