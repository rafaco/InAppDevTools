package es.rafaco.devtools.logic.sources;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import es.rafaco.devtools.logic.steps.FriendlyLog;

public class SourcesManager {

    public static final String APP = "app_sources";
    public static final String DEVTOOLS = "devtools_sources";
    public static final String ASSETS = "assets_resources";

    Context context;
    private List<SourceOrigin> origins;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    protected void init() {        
        origins = new ArrayList<>();
        populateJarPackage(APP);
        populateJarPackage(DEVTOOLS);
        populateAssets();
    }

    private void populateAssets() {
        SourceOrigin newPackage = new SourceOrigin();
        newPackage.name = ASSETS;

        AssetManager aMan = context.getApplicationContext().getAssets();
        List<String> filelist = new ArrayList<>();
        try {
            filelist =  Arrays.asList(aMan.list("fonts"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FriendlyLog.log(filelist.toString());
        //newPackage.items = filelist;
        origins.add(newPackage);
    }

    private void populateJarPackage(String originName) {
        JarSourcesReader jarReader = new JarSourcesReader(context);

        SourceOrigin newPackage = new SourceOrigin();
        newPackage.name = originName;
        newPackage.localJar = jarReader.populateLocalJar(originName);
        newPackage.items = jarReader.populateItemsFromJar(originName, newPackage.localJar);
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


    public String getContent(String packageName, String entryName){
        JarFile jarFile = getOrigin(packageName).localJar;
        JarEntry jarEntry = jarFile.getJarEntry(entryName);
        return extractContent(jarFile, jarEntry);
    }

    private String extractContent(JarFile jar, JarEntry entry){
        StringBuilder codeStringBuilder = new StringBuilder();
        try {
            InputStream inputStream = jar.getInputStream(entry);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = r.readLine()) != null; ) {
                codeStringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return codeStringBuilder.toString();
    }
}
