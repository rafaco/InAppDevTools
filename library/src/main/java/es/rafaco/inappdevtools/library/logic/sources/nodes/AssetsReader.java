package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

class AssetsReader extends AbstractNodeReader{

    private final Context context;

    AssetsReader(Context context) {
        super();
        this.context = context;
        root = new AssetNode("assets", "assets/", true);
    }

    @Override
    public AbstractNode populate() {
        AssetManager aMan = context.getApplicationContext().getAssets();

        List<String> categories = new ArrayList<>();
        try {
            categories =  Arrays.asList(aMan.list(""));
        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        }

        for (String category: categories) {
            String categoryPath = "assets/" + category + "/";
            addEntry(category, categoryPath, true);

            List<String> resources = new ArrayList<>();
            try {
                resources =  Arrays.asList(aMan.list(category));
            } catch (IOException e) {
                FriendlyLog.logException("Exception", e);
            }

            for (String resource: resources) {
                addEntry(resource, categoryPath + resource, true);
            }
        }
        return root;
    }


    private AssetNode addEntry(String entryName, String entryPath, boolean isDirectory) {
        AssetNode node = (AssetNode) collected.get(entryPath);
        if(node != null) {
            // already in the map
            return node;
        }
        node = new AssetNode(entryName, entryPath, isDirectory);
        collected.put(entryPath, node);
        findParent(node);
        return node;
    }

    @Override
    protected AbstractNode createParentNode(String parentName) {
        String shortName = parentName.substring(parentName.lastIndexOf("/"),
                parentName.length()-1);
        return addEntry(shortName, parentName, true);
    }
}
