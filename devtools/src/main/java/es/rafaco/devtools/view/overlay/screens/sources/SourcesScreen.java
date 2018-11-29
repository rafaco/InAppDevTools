package es.rafaco.devtools.view.overlay.screens.sources;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.integrations.LinkConfig;
import es.rafaco.devtools.logic.sources.SourceEntry;
import es.rafaco.devtools.logic.sources.SourcesManager;
import es.rafaco.devtools.view.components.FlexibleAdapter;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class SourcesScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public SourcesScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Sources";
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

        SourcesManager manager = new SourcesManager(getContext());

        List<SourceEntry> filteredItems = manager.getFilteredItems(SourcesManager.DEVTOOLS, getParam());
        List<Object> data = new ArrayList<>();

        for (SourceEntry entry : filteredItems) {
            data.add(new LinkConfig(
                    entry.isDirectory() ? entry.getName(): entry.getFileName(),
                    entry.isDirectory() ? R.string.gmd_folder : R.string.gmd_subdirectory_arrow_right,
                    entry.isDirectory() ? R.color.rally_yellow : R.color.rally_blue_med,
                    entry.isDirectory() ? SourcesScreen.class : SourceDetailScreen.class,
                    entry.isDirectory() ? entry.getName() :
                            SourceDetailScreen.buildParams(SourcesManager.DEVTOOLS,
                                    entry.getName(), -1)));
        }

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(1, data);
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
