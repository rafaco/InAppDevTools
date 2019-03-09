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

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;


public class ZipSourcesReader extends SourcesReader{

    public ZipSourcesReader(Context context) {
        super(context);
    }

    public ZipFile getFile(String target) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(getLocalFile(target));
        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
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

    protected boolean isExcluded(ZipEntry entry) {
        String name = entry.getName();
        if (name.startsWith("META-INF/"))
            return true;

        return false;
    }
}
