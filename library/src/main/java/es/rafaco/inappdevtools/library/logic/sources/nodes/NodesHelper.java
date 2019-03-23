package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.logic.sources.SourceEntry;

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
        nodeReader = new RootReader();
        ((RootReader) nodeReader).addNode(ASSETS);
        ((RootReader) nodeReader).addNode(SOURCES);
        ((RootReader) nodeReader).addNode(RESOURCES);

        //Populate ASSETS
        nodeReader = new AssetsReader(context, nodeReader, ASSETS);
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
}
