package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;

public class SourcesManager {

    public static final String ASSETS = "assets";
    private static final String SRC_TAIL = "_sources.jar";
    private static final String RES_TAIL = "_resources.zip";

    Context context;
    private List<SourceOrigin> origins;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        origins = new ArrayList<>();
        populateAsset();

        List<SourceEntry> ourAssets = getFilteredItems(new SourceEntry(ASSETS, "inappdevtools/", true));

        for (SourceEntry entry: ourAssets) {
            SourcesReader reader = null;
            if (entry.getFileName().endsWith(RES_TAIL)){
                reader = new ZipSourcesReader(context);
            }
            else if (entry.getFileName().endsWith(SRC_TAIL)){
                reader = new JarSourcesReader(context);
            }

            if (reader != null){
                populate(entry, reader);
            }
        }
    }

    private void populateAsset() {
        AssetSourcesReader reader = new AssetSourcesReader(context);
        SourceOrigin newPackage = new SourceOrigin();
        newPackage.name = ASSETS;
        newPackage.localZip = null;
        newPackage.items = reader.getSourceEntries(ASSETS, null);
        origins.add(newPackage);
    }

    private void populate(SourceEntry entry, SourcesReader reader) {
        String origin = entry.getFileName();
        SourceOrigin newPackage = new SourceOrigin();
        newPackage.name = origin;

        newPackage.localZip = reader.getFile(entry.getName());
        if (newPackage.localZip == null){
            return; //Unable to get file, skip
        }

        newPackage.items = reader.getSourceEntries(origin, newPackage.localZip);
        if(reader instanceof JarSourcesReader){
            newPackage.namespaces = ((JarSourcesReader)reader).getNamespaces(newPackage.localZip);
        }

        origins.add(newPackage);
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

    private SourceOrigin getOrigin(String originName) {
        for (SourceOrigin entry : origins) {
            if (entry.name.equals(originName)) return entry;
        }
        return null;
    }




    public String getContent(String originName, String entryName){
        return getOrigin(originName).getContent(entryName);
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

    public List<String> getOriginNames() {
        ArrayList<String> indexItems = new ArrayList<>();
        for (SourceOrigin entry : origins) {
            indexItems.add(entry.name);
        }
        return indexItems;
    }






    //TODO: REFACTOR/REMOVE
    private String getAppName(){
        return new CompileConfig(context).getString(CompileConfigFields.PROJECT_NAME);
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
