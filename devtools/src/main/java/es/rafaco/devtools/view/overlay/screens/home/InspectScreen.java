package es.rafaco.devtools.view.overlay.screens.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;
import es.rafaco.devtools.view.overlay.screens.network.NetworkScreen;
import es.rafaco.devtools.view.overlay.screens.report.ReportScreen;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;
import es.rafaco.devtools.view.overlay.screens.storage.StorageScreen;

public class InspectScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public InspectScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Inspect";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_flexible; }

    @Override
    protected void onCreate() {
    }
    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        data.add(new RunnableConfig("Logcat",
                R.drawable.ic_android_white_24dp,
                () ->  OverlayUIService.performNavigation(LogScreen.class)));

        data.add(new RunnableConfig("Layout",
                R.drawable.ic_layers_white_24dp,
                () ->  DevTools.showMessage("TODO")));

        data.add(new RunnableConfig("Storage",
                R.drawable.ic_storage_white_24dp,
                () ->  OverlayUIService.performNavigation(StorageScreen.class)));

        data.add(new RunnableConfig("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                () -> OverlayUIService.performNavigation(NetworkScreen.class)));

        data.add(new RunnableConfig("Screens",
                R.drawable.ic_photo_library_white_24dp,
                () -> OverlayUIService.performNavigation(ScreensScreen.class)));

        data.add(new RunnableConfig("Errors",
                R.drawable.ic_bug_report_rally_24dp,
                () -> OverlayUIService.performNavigation(ErrorsScreen.class)));

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(3, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
