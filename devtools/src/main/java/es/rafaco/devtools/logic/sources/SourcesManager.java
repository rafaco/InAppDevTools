package es.rafaco.devtools.logic.sources;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import es.rafaco.devtools.storage.files.DevToolsFiles;
import es.rafaco.devtools.storage.files.FileCreator;

public class SourcesManager {

    Context context;
    private JarFile localJar;
    private List<SourceEntry> items;

    public SourcesManager(Context context) {
        this.context = context;
        init();
    }

    protected void init() {
        populateLocalJar();
        populateItems();
    }

    private void populateLocalJar() {

        File sourcesFile = new File(FileCreator.getCategoryFolder("sources"), "app_sources.jar");
        if (!sourcesFile.exists()){
            sourcesFile = copyToLocal();
        }

        JarFile jar = null;
        try {
            jar = new JarFile(sourcesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        localJar = jar;
    }

    private File copyToLocal() {
        File file = DevToolsFiles.prepareSources();

        int resId = context.getResources().getIdentifier(
                "app_sources",
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

    private void populateItems() {
        items = new ArrayList<>();
        Enumeration<? extends JarEntry> enumeration = localJar.entries();
        int position = 0;
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            items.add(new SourceEntry(entry.getName(), position, entry.isDirectory()));
            position++;
        }
    }

    public List<SourceEntry> getFilteredItems(String param){
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

    public String getContent(String name){
        JarEntry jarEntry = localJar.getJarEntry(name);
        return extractContent(localJar, jarEntry);
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
