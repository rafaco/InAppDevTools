package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNode;
import es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNodeReader;
import es.rafaco.inappdevtools.library.storage.files.AssetFileReader;
import es.rafaco.inappdevtools.library.logic.sources.nodes.AssetsNodeReader;
import es.rafaco.inappdevtools.library.logic.sources.nodes.RootNodeReader;
import es.rafaco.inappdevtools.library.logic.sources.nodes.ZipNodeReader;

public class NodesHelper {

    private static final String SRC_TAIL = "_sources.jar";
    private static final String RES_TAIL = "_resources.zip";

    public static final String ASSETS = "assets";
    public static final String SOURCES = "src";
    public static final String RESOURCES = "res";


    public static AbstractNode populate(Context context) {

        AssetFileReader fileReader = new AssetFileReader(context);
        AbstractNodeReader nodeReader = null;

        //Prepare first level
        nodeReader = new RootNodeReader();
        ((RootNodeReader) nodeReader).addNode(ASSETS);
        ((RootNodeReader) nodeReader).addNode(SOURCES);
        ((RootNodeReader) nodeReader).addNode(RESOURCES);

        //Populate ASSETS
        nodeReader = new AssetsNodeReader(context, nodeReader, ASSETS);
        AbstractNode root = nodeReader.populate();

        //Populate SRC and RES
        AbstractNode ourAssetsNode = getNodeByFullPath(root,"assets/inappdevtools/");
        for (AbstractNode node: ourAssetsNode.getChildren().values()) {
            String prefix = null;
            if (node.getName().endsWith(RES_TAIL)) {
                prefix = RESOURCES;
            }else if (node.getName().endsWith(SRC_TAIL)) {
                prefix = SOURCES;
            }

            if (!TextUtils.isEmpty(prefix)){
                ZipFile file = fileReader.getZipFile(node.getPath());
                if (file == null){

                    continue;
                }
                nodeReader = new ZipNodeReader(file, nodeReader, prefix);
                root = nodeReader.populate();
            }
        }

        return root;
    }

    public static AbstractNode getNodeByFullPath(AbstractNode root, String fullPath){
        if (TextUtils.isEmpty(fullPath)){
            return root;
        }

        AbstractNode current = root;
        String[] parts = fullPath.split("[/]");
        boolean isFile = !fullPath.endsWith("/");
        String fileName = isFile ? (parts[parts.length-1]) : "";

        if (parts.length>0){
            for (String part: parts){
                Map<String, AbstractNode> children = current.getChildren();
                if (children == null || children.isEmpty()) return null;

                String childrenSelector = (isFile && part.equals(fileName)) ? part : part + "/";
                current = children.get(childrenSelector);
                if (current == null) return null;
            }
        }
        return current;
    }

    public static List<SourceEntry> castToSourceEntry(Collection<AbstractNode> nodes) {
        List<SourceEntry> entries = new ArrayList<>();
        for (AbstractNode node : nodes){
            entries.add(new SourceEntry("TODO", node.getPath(), node.isDirectory()));
        }
        return entries;
    }

    public static String getFileNameAndExtensionFromPath(String path){
        String[] parts = path.split("[/]");
        boolean isFile = !path.endsWith("/");
        return isFile ? (parts[parts.length-1]) : "";
    }

    public static String getFileExtensionFromPath(String path){
        if (path.contains(".")){
            int lastFound = path.lastIndexOf(".");
            String extension = path.substring(lastFound + 1);
            return extension;
        }
        return "";
    }
}
