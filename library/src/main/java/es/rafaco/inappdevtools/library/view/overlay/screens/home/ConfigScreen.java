package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.view.components.flex.ConfigItem;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

public class ConfigScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public ConfigScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Config";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

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
        data.add("You can change on runtime our configuration parameters.\n" +
                "Temp: press back to apply changes and we will restart your app.");

        List<Config> allConfigs = Config.getAll();
        for (Config item : allConfigs) {
            if (item.getDefaultValue() != null){
                data.add(new ConfigItem(item));
            }
        }

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(1, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    private void saveAll() {
        List<Object> adapterItems = adapter.getItems();
        boolean anyChange = false;
        for (Object item : adapterItems) {
            if (item instanceof ConfigItem){
                ConfigItem configItem = (ConfigItem) item;
                if (configItem.getNewValue() != null && configItem.getNewValue() != configItem.getInitialValue()){
                    anyChange = true;
                    IadtController.get().getConfig().set(configItem.getConfig(), configItem.getNewValue());
                }
            }
        }
        if (anyChange) IadtController.get().restartApp(false);
    }

    @Override
    protected void onStop() {
        saveAll();
    }

    @Override
    protected void onDestroy() {
    }
}
