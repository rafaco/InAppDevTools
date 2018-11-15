package es.rafaco.devtools.view.overlay.screens.home;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.integrations.PandoraBridge;
import es.rafaco.devtools.logic.integrations.RunnableConfig;
import es.rafaco.devtools.view.components.FlexibleAdapter;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;
import es.rafaco.devtools.view.overlay.screens.network.NetworkScreen;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;
import es.rafaco.devtools.view.overlay.screens.storage.StorageScreen;

public class InspectViewScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public InspectViewScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Inspect View";
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

        data.add(new RunnableConfig("Select element",
                R.drawable.ic_touch_app_white_24dp,
                () ->  {
                    getScreenManager().hide();
                    PandoraBridge.select();
                }));

        data.add(new RunnableConfig("Browse hierarchy",
                R.drawable.ic_layers_white_24dp, () -> {
                    getScreenManager().hide();
                    PandoraBridge.hierarchy();
                }));

        data.add(new RunnableConfig("Take Measure",
                R.drawable.ic_format_line_spacing_white_24dp,
                () -> {
                    getScreenManager().hide();
                    PandoraBridge.measure();
                }));

        data.add(new RunnableConfig("Show gridline",
                R.drawable.ic_grid_on_white_24dp,
                () ->  PandoraBridge.grid()));

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(2, data);
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
