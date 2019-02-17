package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.BuildConfigFields;

public class SourcesManager {

    public static final String ASSETS = "assets";
    private static final String SRC_TAIL = "_sources";
    private static final String RES_TAIL = "_resources";

    Context context;
    private List<SourceOrigin> origins;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    protected void init() {        
        origins = new ArrayList<>();
        populateJarOrigin(getAppSrcOrigin());
        populateZipOrigin(getAppName() + RES_TAIL);
        populateJarOrigin(getLibrarySrcOrigin());
        populateZipOrigin(getLibraryName() + RES_TAIL);
        populateAssetOrigin(ASSETS);

        //TODO: Allow to add Sources from another modules (plugin work)
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
        if (newPackage.localZip == null){
            return; //Cancel adding item to origin
        }
        newPackage.items = jarReader.populateItemsFromJar(originName, newPackage.localZip);
        origins.add(newPackage);
    }

    private void populateZipOrigin(String originName) {
        ZipSourcesReader zipReader = new ZipSourcesReader(context);

        SourceOrigin newPackage = new SourceOrigin();
        newPackage.name = originName;
        newPackage.localZip = zipReader.populateLocal(originName);
        if (newPackage.localZip == null){
            return; //Cancel adding item to origin
        }
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
        if (origins.size()>0){
            for (SourceOrigin entry : origins) {
                indexItems.add(new SourceEntry(entry.name, "", true));
            }
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


    private String getAppName(){
        return AppBuildConfig.getStringValue(context, BuildConfigFields.PROJECT_NAME);
    }

    private String getLibraryName(){
        return context.getString(R.string.library_name).toLowerCase();
    }

    public String getLibrarySrcOrigin() {
        return getLibraryName() + SRC_TAIL;
    }

    public String getAppSrcOrigin() {
        return getAppName() + SRC_TAIL;
    }
}
