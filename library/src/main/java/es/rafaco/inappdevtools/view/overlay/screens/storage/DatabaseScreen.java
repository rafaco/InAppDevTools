package es.rafaco.inappdevtools.view.overlay.screens.storage;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import es.rafaco.inappdevtools.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.view.overlay.layers.NavigationStep;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.ui.item.NameItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;

public class DatabaseScreen extends BaseStorageListScreen {


    public DatabaseScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Database";
    }

    @Override
    public void onViewCreated() {
        final int key = Integer.parseInt(getParam());

        loadData(key);
        initClickListener(key);
    }

    private void loadData(int key) {
        List<String> tables = Pandora.get().getDatabases().getTableNames(key);
        Collections.sort(tables);
        List<BaseItem> data = new ArrayList<>(tables.size());
        data.add(new TitleItem(String.format(Locale.getDefault(), "%d TABLES", tables.size())));
        for (int i = 0; i < tables.size(); i++) {
            data.add(new NameItem(tables.get(i)));
        }
        getAdapter().setItems(data);
    }

    private void initClickListener(final int key) {
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof NameItem) {
                    DatabaseSelection selection = new DatabaseSelection(key, ((NameItem) item).data, false);
                    String args = new Gson().toJson(selection);
                    NavigationStep step = new NavigationStep(TableScreen.class, args);
                    OverlayUIService.performNavigationStep(step);
                }
            }
        });
    }
}
