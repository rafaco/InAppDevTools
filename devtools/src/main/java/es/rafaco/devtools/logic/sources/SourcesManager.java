package es.rafaco.devtools.logic.sources;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import es.rafaco.devtools.logic.steps.FriendlyLog;
import es.rafaco.devtools.storage.files.DevToolsFiles;
import es.rafaco.devtools.storage.files.FileCreator;

public class SourcesManager {

    public static final String APP = "app_sources";
    public static final String DEVTOOLS = "devtools_sources";
    public static final String ASSETS = "assets_resources";

    Context context;
    private List<SourcePackage> packages;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    protected void init() {
        packages = new ArrayList<>();
        populatePackage(APP);
        populatePackage(DEVTOOLS);
        populateAssets();
    }

    private void populateAssets() {
        SourcePackage newPackage = new SourcePackage();
        newPackage.packageName = ASSETS;

        AssetManager aMan = context.getApplicationContext().getAssets();
        List<String> filelist = new ArrayList<>();
        try {
            filelist =  Arrays.asList(aMan.list("fonts"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FriendlyLog.log(filelist.toString());
        //newPackage.items = filelist;
        packages.add(newPackage);

    }

    class SourcePackage {
        private String packageName;
        private JarFile localJar;
        private List<SourceEntry> items;
    }

    private SourcePackage getSourcePackage(String packageName) {
        //TODO: make dynamic
        if (packageName.equals(APP))
            return packages.get(0);
        else
            return packages.get(1);
    }

    private void populatePackage(String packageName) {
        SourcePackage newPackage = new SourcePackage();
        newPackage.packageName = packageName;
        newPackage.localJar = populateLocalJar(packageName);
        newPackage.items = populateItemsFromJar(newPackage.localJar);
        packages.add(newPackage);
    }

    private JarFile populateLocalJar(String target) {
        File sourcesFile = new File(FileCreator.getCategoryFolder("sources"), target + ".jar");
        if (!sourcesFile.exists()){
            sourcesFile = copyToLocal(target);
        }

        JarFile jar = null;
        try {
            jar = new JarFile(sourcesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jar;
    }

    private File copyToLocal(String target) {
        File file = DevToolsFiles.prepareSources();

        int resId = context.getResources().getIdentifier(
                target,
                "raw",
                context.getPackageName());
        InputStream input = context.getResources().openRawResource(resId);

        try {
            OutputStream output = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;

                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }

                output.flush();
            } finally {
                output.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private List<SourceEntry> populateItemsFromJar(JarFile localJar) {
        List<SourceEntry> items = new ArrayList<>();
        Enumeration<? extends JarEntry> enumeration = localJar.entries();
        int position = 0;
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            items.add(new SourceEntry(entry.getName(), position, entry.isDirectory()));
            position++;
        }
        return  items;
    }

    public List<SourceEntry> getFilteredItems(String packageName, String param){
        List<SourceEntry> items = getSourcePackage(packageName).items;
        if (TextUtils.isEmpty(param))
            return items;

        ArrayList<SourceEntry> filteredItems = new ArrayList<>();
        for (SourceEntry entry : items) {
            if (entry.getName().contains(param) && !entry.getName().equals(param)) {
                filteredItems.add(entry);
            }
        }
        return filteredItems;
    }

    public String getContent(String packageName, String entryName){
        JarFile jarFile = getSourcePackage(packageName).localJar;
        JarEntry jarEntry = jarFile.getJarEntry(entryName);
        return extractContent(jarFile, jarEntry);
    }

    public String extractContent(JarFile jar, JarEntry entry){
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
