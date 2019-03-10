package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.ThinItem;
import es.rafaco.inappdevtools.library.logic.sources.SourceEntry;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;

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
        //Nothing needed
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

        if (filter != null){
            String label = filter.getName();
            if (TextUtils.isEmpty(label)){
                label = filter.getOrigin();
            }
            label = ".. (" + label + ")";

            data.add(new ThinItem(
                    label,
                    R.string.gmd_arrow_upward,
                    R.color.rally_green,
                    new Runnable() {
                        @Override
                        public void run() {
                            SourcesScreen.this.goUp();
                        }
                    }
            ));
        }

        for (final SourceEntry entry : filteredItems) {
            String label = getLinkName(entry);
            if (filter!=null){
                label = label.replace(filter.getName(), "");
            }

            data.add(new ThinItem(label,
                    entry.isDirectory() ? R.string.gmd_folder : R.string.gmd_insert_drive_file,
                    entry.isDirectory() ? R.color.rally_yellow : R.color.rally_blue_med,
                    entry.isDirectory() ? new Runnable() {
                        @Override
                        public void run() {
                            SourcesScreen.this.updateFilter(entry);
                        }
                    } : new Runnable() {
                        @Override
                        public void run() {
                            SourcesScreen.this.openSource(entry);
                        }
                    }));
        }

        return data;
    }

    private void openSource(SourceEntry entry) {
        String params = SourceDetailScreen.buildParams(entry.getOrigin(), entry.getName(), -1);
        OverlayUIService.performNavigation(SourceDetailScreen.class, params);
    }

    private void goUp() {
        history.remove(history.size() - 1);
        SourceEntry secondLast = history.remove(history.size() - 1);
        updateFilter(secondLast);
    }

    private String updateFilter(SourceEntry filter) {
        List<Object> filteredItems = getData(filter);
        adapter.replaceItems(filteredItems);
        storeHistory(filter);
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
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
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
