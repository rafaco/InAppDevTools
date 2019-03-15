package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.content.Context;

import java.util.Collections;
import java.util.Map;

public class AssetNode extends AbstractNode{

    private String entryName;
    private String entryPath;


    protected AssetNode(String entryName, String entryPath, boolean isDirectory) {
        super(isDirectory);
        this.entryName = entryName;
        this.entryPath = entryPath;
    }

    @Override
    public String getName() {
        if(entryName == null)
            return "/";
        return entryName.substring(entryName.lastIndexOf(FOLDER_SEP,
                entryName.length()-2)+1);
    }

    public String getEntryName() {
        return entryName;
    }

    public String getPath(){
        if (entryName == null) {
            return FOLDER_SEP + "";
        }
        return entryPath;
    }



    public boolean isDirectory() {
        return directory;
    }

    public AbstractNode getParent() {
        return parent;
    }

    public Map<String, AbstractNode> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    public String toString() {
        String name = "root";
        if (entryName != null){
            name = getName();
        }
        return "ZipNode [" + name + "] in [ASSETS]";
    }

    public static AssetNode fromAssets(Context context) {
        return (AssetNode) new AssetsReader(context).populate();
    }

}