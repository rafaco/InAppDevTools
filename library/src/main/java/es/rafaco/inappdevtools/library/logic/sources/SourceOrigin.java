package es.rafaco.inappdevtools.library.logic.sources;

import java.util.List;
import java.util.zip.ZipFile;


public class SourceOrigin {
    public String name;
    public List<String> namespaces;
    public ZipFile localZip;
    public List<SourceEntry> items;

    public String getContent(String entryName){
        if (localZip != null){
            return SourcesReader.extractContent(localZip, localZip.getEntry(entryName));
        }else{
            return AssetSourcesReader.extractContent(entryName);
        }
    }
}
