package es.rafaco.devtools.view.overlay.screens.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import tech.linjiang.pandora.sandbox.Sandbox;
import tech.linjiang.pandora.ui.item.FileItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Utils;

public class FolderScreen extends BaseStorageListScreen {

    private File folder;

    public FolderScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    protected void onViewCreated() {
        folder = new File(getParam());
        getToolbar().setTitle(folder.getName());

        setListener();
        loadData();
    }

    private void setListener() {
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                NavigationStep step;
                if (((File) item.data).isDirectory()) {
                    step = new NavigationStep(FolderScreen.class, folder.getAbsolutePath());
                }else{
                    step = new NavigationStep(FileScreen.class, folder.getAbsolutePath());
                }
                OverlayUIService.performNavigationStep(step);
            }
        });
    }

    private void loadData() {
        List<File> files = Sandbox.getFiles(folder);
        if (Utils.isNotEmpty(files)) {
            List<BaseItem> data = new ArrayList<>();
            data.add(new TitleItem(String.format(Locale.getDefault(), "%d FILES", files.size())));
            for (int i = 0; i < files.size(); i++) {
                data.add(new FileItem(files.get(i)));
            }
            getAdapter().setItems(data);
        } else {
            showError(null);
        }
    }

    @Override
    public String getTitle() {
        return "Folder";
    }
}
