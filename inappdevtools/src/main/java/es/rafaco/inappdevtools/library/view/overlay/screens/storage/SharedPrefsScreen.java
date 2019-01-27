package es.rafaco.inappdevtools.library.view.overlay.screens.storage;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.ui.item.KeyValueItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;

public class SharedPrefsScreen extends BaseStorageListScreen {

    private File descriptor;

    public SharedPrefsScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    protected void onViewCreated() {

        descriptor = new File(getParam());
        getScreenManager().setTitle(descriptor.getName());

        //registerAdapter
        loadData();
    }

    private void loadData() {
        Map<String, String> contents = Pandora.get().getSharedPref().getSharedPrefContent(descriptor);
        if (contents != null && !contents.isEmpty()) {
            List<BaseItem> data = new ArrayList<>();
            data.add(new TitleItem(String.format(Locale.getDefault(), "%d ITEMS", contents.size())));
            data.add(new KeyValueItem(new String[]{"KEY", "VALUE"}, true));
            for (Map.Entry<String, String> entry : contents.entrySet()) {
                data.add(new KeyValueItem(new String[]{entry.getKey(), entry.getValue()}, false, true));
            }
            getAdapter().setItems(data);

        } else {
            showError(null);
        }
    }

    @Override
    public String getTitle() {
        return "SharedPrefs";
    }
}
