package es.rafaco.inappdevtools.view.overlay.screens.storage;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.view.overlay.layers.MainOverlayLayerManager;
import tech.linjiang.pandora.ui.item.ContentItem;
import tech.linjiang.pandora.ui.item.TitleItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.FileUtil;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;


public class FileScreen extends BaseStorageListScreen {

    private File file;

    public FileScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    protected void onViewCreated() {

        file = new File(getParam());
        getToolbar().setTitle(file.getName());

        if (!file.exists()) {
            showError("The file doesn't exists");
            return;
        }

        //loadMenu
        setListener();
        loadData();
    }

    private void setListener() {
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof ContentItem) {
                    Utils.copy2ClipBoard((String) item.data);
                }
            }
        });
    }

    private void loadData() {
        List<BaseItem> data = new ArrayList<>();
        data.add(new TitleItem("NAME"));
        data.add(new ContentItem(file.getName()));
        data.add(new TitleItem("PATH"));
        data.add(new ContentItem(file.getPath()));
        data.add(new TitleItem("TYPE"));
        String type = FileUtil.getFileType(file.getPath());
        data.add(new ContentItem(TextUtils.isEmpty(type) ? "other" : type));
        data.add(new TitleItem("SIZE"));
        data.add(new ContentItem(FileUtil.fileSize(file)));
        data.add(new TitleItem("MODIFIED"));
        data.add(new ContentItem(Utils.millis2String(file.lastModified(), Utils.NO_MILLIS)));
        data.add(new TitleItem("AUTHORITY"));
        data.add(new ContentItem(String.format("X: %b    W: %b    R: %b",
                file.canExecute(), file.canWrite(), file.canRead())));
        data.add(new TitleItem("HASH"));
        data.add(new ContentItem(FileUtil.bytesToHexString(String.valueOf(file.hashCode()).getBytes())));
        getAdapter().setItems(data);

        new SimpleTask<>(new SimpleTask.Callback<File, List<BaseItem>>() {
            @Override
            public List<BaseItem> doInBackground(File[] params) {
                List<BaseItem> data = new ArrayList<>();
                data.add(new TitleItem("MD5"));
                data.add(new ContentItem(FileUtil.md5File(params[0])));
                return data;
            }

            @Override
            public void onPostExecute(List<BaseItem> result) {
                if (Utils.isNotEmpty(result)) {
                    getAdapter().insertItems(result, 10);
                }
            }
        }).execute(file);
    }

    @Override
    public String getTitle() {
        return "File";
    }



    @Override
    protected boolean needDefaultDivider() {
        return false;
    }
}
