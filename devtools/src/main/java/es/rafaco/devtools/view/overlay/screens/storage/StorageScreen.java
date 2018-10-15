package es.rafaco.devtools.view.overlay.screens.storage;

import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.sandbox.Sandbox;
import tech.linjiang.pandora.ui.item.DBItem;
import tech.linjiang.pandora.ui.item.FileItem;
import tech.linjiang.pandora.ui.item.SPItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.SimpleTask;


public class StorageScreen extends BaseStorageListScreen {

    public StorageScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Storage";
    }

    @Override
    protected void onViewCreated() {
        initAdapterListener();
        loadData();
    }

    //region [ SandboxFragment ]

    private void initAdapterListener() {

        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                Bundle bundle = new Bundle();
                if (item instanceof DBItem) {
                    NavigationStep step = new NavigationStep(DatabaseScreen.class,
                            String.valueOf(((DBItem) item).key));
                    OverlayUIService.performNavigationStep(step);
                }
                else if (item instanceof SPItem) {
                    File file = ((SPItem) item).descriptor;
                    NavigationStep step = new NavigationStep(SharedPrefsScreen.class,
                            file.getAbsolutePath());
                    OverlayUIService.performNavigationStep(step);

                }else if (item instanceof FileItem) {
                    File file = ((File) item.data);
                    NavigationStep step;
                    if (((File) item.data).isDirectory()) {
                        step = new NavigationStep(FolderScreen.class, file.getAbsolutePath());
                    }else{
                        step = new NavigationStep(FileScreen.class, file.getAbsolutePath());
                    }
                    OverlayUIService.performNavigationStep(step);

                }

            }
        });
    }

    private void loadData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<BaseItem>>() {
            @Override
            public List<BaseItem> doInBackground(Void[] params) {
                SparseArray<String> databaseNames = Pandora.get().getDatabases().getDatabaseNames();
                List<BaseItem> data = new ArrayList<>(databaseNames.size());
                data.add(new TitleItem("SQLite DATABASES".toUpperCase()));
                for (int i = 0; i < databaseNames.size(); i++) {
                    data.add(new DBItem(databaseNames.valueAt(i), databaseNames.keyAt(i)));
                }
                data.add(new TitleItem("Shared Preferences".toUpperCase()));
                List<File> spFiles = Pandora.get().getSharedPref().getSharedPrefDescs();
                for (int i = 0; i < spFiles.size(); i++) {
                    data.add(new SPItem(spFiles.get(i).getName(), spFiles.get(i)));
                }

                data.add(new TitleItem("Files".toUpperCase()));

                List<File> descriptors = Sandbox.getRootFiles();
                for (int i = 0; i < descriptors.size(); i++) {
                    data.add(new FileItem(descriptors.get(i)));
                }

                if (Config.getSANDBOX_DPM() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    data.add(new TitleItem("Device-protect-mode Files"));
                    List<File> dpm = Sandbox.getDPMFiles();
                    for (int i = 0; i < dpm.size(); i++) {
                        data.add(new FileItem(dpm.get(i)));
                    }
                }

                return data;
            }

            @Override
            public void onPostExecute(List<BaseItem> result) {
                hideLoading();
                getAdapter().setItems(result);
            }
        }).execute();


    }

    //endregion

}
