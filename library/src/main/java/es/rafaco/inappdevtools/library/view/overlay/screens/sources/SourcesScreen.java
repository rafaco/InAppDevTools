package es.rafaco.inappdevtools.library.view.overlay.screens.sources;

import android.text.TextUtils;
import android.view.ViewGroup;

//#ifdef MODERN
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//#else
//@import android.support.annotation.NonNull;
//@import android.support.v7.widget.RecyclerView;
//#endif

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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
        return getData(params);
    }

    private List<Object> getData(SourceEntry filter) {
        String path = (filter!=null) ? filter.getName() : null;
        List<SourceEntry> filteredItems = DevTools.getSourcesManager().getFilteredItems(path);
        List<Object> data = new ArrayList<>();

        if (filter != null) { // Not root

            data.add(new ThinItem(
                    "Go to root",
                    R.string.gmd_home,
                    R.color.rally_white,
                    new Runnable() {
                        @Override
                        public void run() {
                            SourcesScreen.this.goRoot();
                        }
                    }
            ));

            if (filter.getDeepLevel() != 0){
                data.add(new ThinItem(
                        "Up from " + removeLastChar(filter.getName()),
                        R.string.gmd_arrow_upward,
                        R.color.rally_white,
                        new Runnable() {
                            @Override
                            public void run() {
                                SourcesScreen.this.goUp();
                            }
                        }
                ));
            }
        }

        for (final SourceEntry entry : filteredItems) {
            String label = getLinkName(entry);
            if (filter!=null){
                label = label.replace(filter.getName(), "");
            }

            data.add(new ThinItem(label,
                    entry.isDirectory() ? R.string.gmd_folder_filled : R.string.gmd_insert_drive_file,
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

    private void goRoot() {
        updateFilter(null);
    }

    private void goUp() {
        String currentName = removeLastChar(getParams().getName());
        String upName = currentName.substring(0, currentName.lastIndexOf("/")+1);
        updateFilter(new SourceEntry("TODO", upName, true));
    }

    private void updateFilter(SourceEntry entry) {
        if (entry == null)
            updateParam(null);
        else
            updateParam(buildParams("TODO", entry.getName()));

        List<Object> filteredItems = getData(entry);
        adapter.replaceItems(filteredItems);
    }

    private String getLinkName(SourceEntry entry) {
        if (TextUtils.isEmpty(entry.getName())){
            return entry.getOrigin();
        }
        return entry.isDirectory() ? removeLastChar(entry.getName())
                : entry.getFileName();
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

    @NonNull
    private String removeLastChar(String text) {
        return text.substring(0, text.length()-1);
    }
}
