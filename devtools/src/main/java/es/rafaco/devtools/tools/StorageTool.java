package es.rafaco.devtools.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.storage.StorageHelper;
import es.rafaco.devtools.view.overlay.screens.storage.StorageScreen;
import es.rafaco.devtools.view.utils.DecoratedToolInfo;
import es.rafaco.devtools.view.utils.DecoratedToolInfoAdapter;

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
