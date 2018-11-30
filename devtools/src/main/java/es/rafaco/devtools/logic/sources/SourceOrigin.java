package es.rafaco.devtools.logic.sources;

import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class SourceOrigin {
    public String name;
    public JarFile localJar;
    public List<SourceEntry> items;

    public String getContent(String entryName){
        if (localJar != null){
            JarEntry jarEntry = localJar.getJarEntry(entryName);
            return JarSourcesReader.extractContent(localJar, jarEntry);
        }else{
            return AssetSourcesReader.extractContent(entryName);
        }
    }
}
