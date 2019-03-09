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


public class JarSourcesReader extends ZipSourcesReader {

    public JarSourcesReader(Context context) {
        super(context);
    }

    public JarFile getFile(String target) {
        JarFile jar = null;
        try {
            jar = new JarFile(getLocalFile(target));
        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        }
        return jar;
    }

    protected boolean isExcluded(ZipEntry entry) {
        if (super.isExcluded(entry))
            return true;

        /*
        //TODO: exclude other variants at plugin
        //TODO: remove head from generated folder paths to match namespace!? at plugin?
        String name = entry.getName();
        String packagePath = context.getPackageName().replace(".", "/");
        if ((name.startsWith("source/") || name.startsWith("not_namespaced_r_class_sources"))
                && !name.contains(packagePath))
            return true;
        */
        return false;
    }

    public List<String> getFirstFolders(ZipFile localZip) {
        List<String> firstFolders = new ArrayList<>();

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
                    firstFolders.add(firstFolder);
                    firstFolder = null;
                }
            }
        }
        return firstFolders;
    }
}
