package es.rafaco.inappdevtools.library.logic.sources;

import java.util.List;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.DevTools;


public class SourceOrigin {
    public String name;
    public List<String> firstFolders;
    public ZipFile localZip;
    public List<SourceEntry> items;

    public String getContent(String entryName){
        SourcesReader reader;
        if (localZip!=null){
            reader = new JarSourcesReader(DevTools.getAppContext());
        }else{
            reader = new AssetSourcesReader(DevTools.getAppContext());
        }
        return reader.extractContent(localZip, entryName);
    }
}
