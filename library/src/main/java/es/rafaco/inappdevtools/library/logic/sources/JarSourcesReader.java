package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class JarSourcesReader extends SourcesReader {

    public JarSourcesReader(Context context) {
        super(context);
    }

    public JarFile getFile(String target) {
        JarFile jar = null;
        try {
            jar = new JarFile(getLocalFile(target));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jar;
    }

    public List<SourceEntry> getSourceEntries(String originName, ZipFile localZip) {
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

    public List<String> getNamespaces(ZipFile localZip) {
        List<String> namespaces = new ArrayList<>();

        String firstFolder = null;
        Enumeration<? extends ZipEntry> enumeration = localZip.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            if(entry.isDirectory() && !isExcluded(entry)){
                String current = entry.getName();
                if (firstFolder == null || current.contains(firstFolder)){
                    firstFolder = current;
                }
            }else{
                if (firstFolder!=null){
                    namespaces.add(firstFolder);
                    firstFolder = null;
                }
            }
        }
        return namespaces;
    }
}
