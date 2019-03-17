package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.content.Context;
import android.text.TextUtils;

import java.util.zip.ZipFile;

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
                ZipFile file = fileReader.getFile(node.getPath());
                nodeReader = new ZipNodeReader(file, nodeReader, prefix);
                root = nodeReader.populate();
            }
        }

        return root;
    }

    public static AbstractNode getNodeByFullPath(AbstractNode root, String fullPath){
        String[] parts = fullPath.split("[/]");
        AbstractNode current = root;
        if (parts.length>0){
            for (String part: parts){
                current = current.getChildren().get(part + "/");
            }
        }
        return current;
    }
}
