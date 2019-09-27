package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.content.DialogInterface;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.view.components.flex.ConfigItem;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AlertDialog;
//#endif

public class ConfigScreen extends Screen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;
    private boolean changesDiscarted = false;

    public ConfigScreen(ScreenManager manager) {
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
        data.add("You can change your runtime configuration for our library."
                + Humanizer.newLine()
                + "Press back to apply your changes"
                + Humanizer.newLine()
                + "(Work in progress: not all changes should be allowed)"
                + Humanizer.fullStop());

        List<BuildConfig> allConfigs = BuildConfig.getAll();
        for (BuildConfig item : allConfigs) {
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

    private boolean haveAnyChange() {
        List<Object> adapterItems = adapter.getItems();
        boolean anyChange = false;
        for (Object item : adapterItems) {
            if (item instanceof ConfigItem){
                ConfigItem configItem = (ConfigItem) item;
                if (configItem.getNewValue() != null && configItem.getNewValue() != configItem.getInitialValue()){
                    anyChange = true;
                }
            }
        }
        return anyChange;
    }

    private void saveAnyChange() {
        List<Object> adapterItems = adapter.getItems();
        for (Object item : adapterItems) {
            if (item instanceof ConfigItem){
                ConfigItem configItem = (ConfigItem) item;
                if (configItem.getNewValue() != null && configItem.getNewValue() != configItem.getInitialValue()){
                    IadtController.get().getConfig().set(configItem.getConfig(), configItem.getNewValue());
                }
            }
        }
    }

    public boolean canGoBack() {
        if (haveAnyChange() && !changesDiscarted){
            showConfirmationDialog();
            return false;
        }
        else{
            return true;
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Confirm your changes")
                .setMessage("To apply your changes safely, we currently need to restart your app. You can also discard them by now.")
                .setCancelable(false)
                .setPositiveButton("Apply and restart",
                       new DialogInterface.OnClickListener(){
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               saveAnyChange();
                               IadtController.get().restartApp(false);
                           }})
                .setNegativeButton("Discard",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                changesDiscarted = true;
                                getScreenManager().goBack();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
