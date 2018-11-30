package es.rafaco.devtools.view.overlay.screens.sources;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.integrations.ThinItem;
import es.rafaco.devtools.logic.sources.SourceEntry;
import es.rafaco.devtools.view.components.FlexibleAdapter;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class SourcesScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;
    private List<SourceEntry> history;

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
        SourceEntry params = getParams();
        storeHistory(params);
        return getData(params);
    }

    private List<Object> getData(SourceEntry filter) {
        List<SourceEntry> filteredItems = DevTools.getSourcesManager().getFilteredItems(filter);
        List<Object> data = new ArrayList<>();

        if (filter != null && !TextUtils.isEmpty(filter.getOrigin())){
            data.add(new ThinItem(
                    "..",
                    R.string.gmd_folder,
                    R.color.rally_yellow,
                    () -> goUp()
            ));
        }

        for (SourceEntry entry : filteredItems) {
            data.add(new ThinItem(
                    getLinkName(entry),
                    entry.isDirectory() ? R.string.gmd_folder : R.string.gmd_subdirectory_arrow_right,
                    entry.isDirectory() ? R.color.rally_yellow : R.color.rally_blue_med,
                    entry.isDirectory() ? () -> updateFilter(entry) : () -> openSource(entry)));
        }

        return data;
    }

    private void openSource(SourceEntry entry) {
        String params = SourceDetailScreen.buildParams(entry.getOrigin(), entry.getName(), -1);
        OverlayUIService.performNavigation(SourceDetailScreen.class, params);
    }

    private void goUp() {
        SourceEntry current = history.remove(history.size() - 1);
        SourceEntry previous = history.remove(history.size() - 1);
        updateFilter(previous);
    }

    private String updateFilter(SourceEntry entry) {
        List<Object> filteredItems = getData(entry);
        adapter.replaceItems(filteredItems);
        storeHistory(entry);
        return null;
    }

    private void storeHistory(SourceEntry entry) {
        if (history == null){
            history = new ArrayList<>();
        }
        history.add(entry);
    }

    private String getLinkName(SourceEntry entry) {
        if (TextUtils.isEmpty(entry.getName())){
            return entry.getOrigin();
        }
        return entry.isDirectory() ? entry.getName(): entry.getFileName();
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

    public static String buildParams(String origin, String path){
        SourceEntry paramObject = new SourceEntry(origin, path, true);
        Gson gson = new Gson();
        return gson.toJson(paramObject);
    }

    public SourceEntry getParams(){
        Gson gson = new Gson();
        return gson.fromJson(getParam(), SourceEntry.class);
    }
}
