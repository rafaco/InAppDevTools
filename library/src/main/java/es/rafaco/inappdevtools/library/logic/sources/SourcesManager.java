package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;

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

        //TODO implement similarly for other types
        if(reader instanceof JarSourcesReader){
            newPackage.firstFolders = ((JarSourcesReader)reader).getFirstFolders(newPackage.localZip);
        }

        origins.add(newPackage);
    }

    private List<SourceEntry> getOriginIndexItems() {
        ArrayList<SourceEntry> indexItems = new ArrayList<>();
        if (!origins.isEmpty()){
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


    public boolean canOpen(String path) {
        String origin = DevTools.getSourcesManager().findOriginByClassName(path);
        return !TextUtils.isEmpty(origin);
    }
    
    public String findOriginByClassName(String namespace) {
        String path = namespace.replace(".", "/");
        return findOriginByPath(path);
    }

    public String findOriginByPath(String sourcePath) {
        for (SourceOrigin entry : origins) {
            if (entry.firstFolders != null && !entry.firstFolders.isEmpty()){
                for (String folder : entry.firstFolders) {
                    if(sourcePath.contains(folder)){
                        return entry.name;
                    }
                }
            }
        }
        return "";
    }


    public String getContent(String path){
        String origin = findOriginByPath(path);
        if (TextUtils.isEmpty(origin)){
            return "";
        }

        SourceOrigin sourceOrigin = getOrigin(origin);
        if (sourceOrigin==null)
            return null;

        return sourceOrigin.getContent(path);
    }

    public String getContent(String originName, String entryName){
        if (!TextUtils.isEmpty(originName)){
            SourceOrigin sourceOrigin = getOrigin(originName);
            if (sourceOrigin==null)
                return null;

            return sourceOrigin.getContent(entryName);
        }else{
            return getContent(entryName);
        }
    }

    public List<SourceEntry> getFilteredItems(SourceEntry filter){

        if (filter == null || TextUtils.isEmpty(filter.getOrigin())){
            return getOriginIndexItems();
        }


        SourceOrigin sourceOrigin = getOrigin(filter.getOrigin());
        if (sourceOrigin==null)
            return new ArrayList<>();

        List<SourceEntry> items = sourceOrigin.items;
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
}
