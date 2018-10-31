package es.rafaco.devtools.view.overlay.screens.home;

import android.view.View;
import android.view.ViewGroup;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;
import es.rafaco.devtools.view.overlay.screens.network.NetworkScreen;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;
import es.rafaco.devtools.view.overlay.screens.storage.StorageScreen;

public class AdvancedScreen extends OverlayScreen {


    public AdvancedScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Advanced";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_advanced; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {
        initView(view);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    private void initView(View view) {


        view.findViewById(R.id.logcat_button)
                .setOnClickListener(v ->
                        OverlayUIService.performNavigationStep(new NavigationStep(LogScreen.class, null)));

        view.findViewById(R.id.layout_button)
                .setOnClickListener(v ->
                        DevTools.showMessage("Not already implemented"));

        view.findViewById(R.id.storage_button)
                .setOnClickListener(v ->
                        OverlayUIService.performNavigationStep(new NavigationStep(StorageScreen.class, null)));

        view.findViewById(R.id.network_button)
                .setOnClickListener(v ->
                        OverlayUIService.performNavigationStep(new NavigationStep(NetworkScreen.class, null)));

        view.findViewById(R.id.screens_button)
                .setOnClickListener(v ->
                        OverlayUIService.performNavigationStep(new NavigationStep(ScreensScreen.class, null)));

        view.findViewById(R.id.errors_button)
                .setOnClickListener(v ->
                        OverlayUIService.performNavigationStep(new NavigationStep(ErrorsScreen.class, null)));
    }
}
