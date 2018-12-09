package es.rafaco.devtools.logic.sources;

import android.content.Context;

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
import java.util.zip.ZipFile;

import es.rafaco.devtools.storage.files.DevToolsFiles;


public class ZipSourcesReader {

    private final Context context;

    public ZipSourcesReader(Context context) {
        this.context = context;
    }

    public ZipFile populateLocal(String target) {
        //TODO: validate if override needed
        // Now is overriding for every application start and could check build changes
        File sourcesFile = copyToLocal(target);

        ZipFile zip = null;
        try {
            zip = new JarFile(sourcesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zip;
    }

    private File copyToLocal(String target) {
        File file = DevToolsFiles.prepareSource(target, "zip");

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

    public List<SourceEntry> populateItems(String originName, ZipFile localZip) {
        List<SourceEntry> items = new ArrayList<>();
        Enumeration<? extends ZipEntry> enumeration = localZip.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            if(!isExcluded(entry)){
                items.add(new SourceEntry(originName, entry.getName(), entry.isDirectory()));
            }
        }
        return  items;
    }

    private boolean isExcluded(ZipEntry entry) {
        String name = entry.getName();
        if (name.startsWith("META-INF/"))
            return true;

        String packagePath = context.getPackageName().replace(".", "/");
        if ( (name.startsWith("source/") || name.startsWith("not_namespaced_r_class_sources")) && !name.contains(packagePath))
            return true;

        if (name.startsWith("res/"))
            return true;

        return false;
    }

    public static String extractContent(ZipFile zip, ZipEntry entry){
        StringBuilder codeStringBuilder = new StringBuilder();
        try {
            InputStream inputStream = zip.getInputStream(entry);
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
