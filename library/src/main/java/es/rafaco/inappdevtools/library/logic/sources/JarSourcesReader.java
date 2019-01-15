package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;


public class JarSourcesReader {

    private final Context context;

    public JarSourcesReader(Context context) {
        this.context = context;
    }

    public JarFile populateLocalJar(String target) {
        //TODO: validate if override needed
        // Now is overriding for every application start and could check build changes
        File sourcesFile = copyToLocal(target);

        JarFile jar = null;
        try {
            jar = new JarFile(sourcesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jar;
    }

    private File copyToLocal(String target) {
        File file = DevToolsFiles.prepareSource(target, "jar");

        int resId = context.getResources().getIdentifier(
                target,
                "raw",
                context.getPackageName());
        InputStream input = null;
        try {
            input = context.getResources().openRawResource(resId);
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
                if (input!=null) input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public List<SourceEntry> populateItemsFromJar(String originName, ZipFile localZip) {
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
}
