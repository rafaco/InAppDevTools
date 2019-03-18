package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreensScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;

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

        data.add(new RunnableConfig("Sources",
                R.drawable.ic_code_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(SourcesScreen.class);
                    }
                }));

        data.add(new RunnableConfig("View",
                R.drawable.ic_layers_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(InspectViewScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Storage",
                R.drawable.ic_storage_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        //OverlayUIService.performNavigation(StorageScreen.class);
                        InspectScreen.this.getScreenManager().hide();
                        PandoraBridge.storage();
                    }
                }));

        data.add(new RunnableConfig("Logcat",
                R.drawable.ic_android_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(LogScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(NetworkScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Screens",
                R.drawable.ic_photo_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(ScreensScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Errors",
                R.drawable.ic_bug_report_rally_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(ErrorsScreen.class);
                    }
                }));

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
