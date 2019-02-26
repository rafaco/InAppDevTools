package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ZipSourcesReader extends SourcesReader{

    public ZipSourcesReader(Context context) {
        super(context);
    }

    public ZipFile getFile(String target) {

        ZipFile zip = null;
        try {
            zip = new JarFile(getLocalFile(target));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zip;
    }

    protected InputStream getInputStream(ZipFile zip, String entryName) throws IOException {
        return zip.getInputStream(zip.getEntry(entryName));
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
}
