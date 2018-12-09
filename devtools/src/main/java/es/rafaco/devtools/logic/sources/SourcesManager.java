package es.rafaco.devtools.logic.sources;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class SourcesManager {

    public static final String APP_RES = "app_resources";
    public static final String APP = "app_sources";
    public static final String DEVTOOLS = "devtools_sources";
    public static final String ASSETS = "assets";

    Context context;
    private List<SourceOrigin> origins;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    protected void init() {        
        origins = new ArrayList<>();
        populateZipOrigin(APP_RES);
        populateJarOrigin(APP);
        populateJarOrigin(DEVTOOLS);
        populateAssetOrigin(ASSETS);
    }

    private void populateAssetOrigin(String originName) {
        AssetSourcesReader reader = new AssetSourcesReader(context);

        SourceOrigin newPackage = new SourceOrigin();
        newPackage.name = originName;
        newPackage.localZip = null;
        newPackage.items = reader.populateItems(originName);
        origins.add(newPackage);
    }

    private void populateJarOrigin(String originName) {
        JarSourcesReader jarReader = new JarSourcesReader(context);

        SourceOrigin newPackage = new SourceOrigin();
        newPackage.name = originName;
        newPackage.localZip = jarReader.populateLocalJar(originName);
        newPackage.items = jarReader.populateItemsFromJar(originName, newPackage.localZip);
        origins.add(newPackage);
    }

    private void populateZipOrigin(String originName) {
        ZipSourcesReader zipReader = new ZipSourcesReader(context);

        SourceOrigin newPackage = new SourceOrigin();
        newPackage.name = originName;
        newPackage.localZip = zipReader.populateLocal(originName);
        newPackage.items = zipReader.populateItems(originName, newPackage.localZip);
        origins.add(newPackage);
    }




    public List<SourceEntry> getFilteredItems(SourceEntry filter){

        if (filter == null || TextUtils.isEmpty(filter.getOrigin())){
            return getOriginIndexItems();
        }

        List<SourceEntry> items = getOrigin(filter.getOrigin()).items;
        if (TextUtils.isEmpty(filter.getName()))
            return items;

        ArrayList<SourceEntry> filteredItems = new ArrayList<>();
        for (SourceEntry entry : items) {
            if (entry.getName().contains(filter.getName())
                    && !entry.getName().equals(filter.getName())) {
                filteredItems.add(entry);
            }
        }
        return filteredItems;
    }

    private List<SourceEntry> getOriginIndexItems() {
        ArrayList<SourceEntry> indexItems = new ArrayList<>();
        for (SourceOrigin entry : origins) {
            indexItems.add(new SourceEntry(entry.name, "", true));
        }
        return indexItems;
    }

    public List<String> getOriginNames() {
        ArrayList<String> indexItems = new ArrayList<>();
        for (SourceOrigin entry : origins) {
            indexItems.add(entry.name);
        }
        return indexItems;
    }

    private SourceOrigin getOrigin(String originName) {
        for (SourceOrigin entry : origins) {
            if (entry.name.equals(originName)) return entry;
        }
        return null;
    }


    public String getContent(String originName, String entryName){
        return getOrigin(originName).getContent(entryName);
    }
}
