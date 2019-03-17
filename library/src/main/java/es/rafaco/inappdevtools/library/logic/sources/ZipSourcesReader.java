package es.rafaco.inappdevtools.library.logic.sources;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNode;
import es.rafaco.inappdevtools.library.logic.sources.nodes.ZipNode;
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

        ZipNode root = null;
        try {
            root = (ZipNode) ZipNode.fromZipFile(localZip);
            root.printTree();
        } catch (Exception e) {
            FriendlyLog.logException("Generating ZipNode: ", e);
        }


        List<SourceEntry> items = new ArrayList<>();
        Enumeration<? extends ZipEntry> enumeration = localZip.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry entry = enumeration.nextElement();
            //if(!(isExcludedEntry(entry) || isExcludedNode(geNodeEntry(entry, root)))){
            items.add(new SourceEntry(originName, entry.getName(), entry.isDirectory()));
        }
        return  items;
    }

    public List<SourceEntry> getFirstEntries(String originName, ZipFile localZip) {

        ZipNode root = null;
        try {
            root = (ZipNode) ZipNode.fromZipFile(localZip);
            root.printTree();
        } catch (Exception e) {
            FriendlyLog.logException("Generating ZipNode: ", e);
        }

        Map<String, AbstractNode> currentNodes = root.getChildren();
        int currentLevel = 0;
        while (currentNodes.size()==1){
            currentNodes = currentNodes.get(0).getChildren();
            currentLevel++;
        }

        List<SourceEntry> result = new ArrayList<>();
        for (Map.Entry<String, AbstractNode> mapEntry : currentNodes.entrySet()) {
            AbstractNode current = mapEntry.getValue();
            while (current.getChildren() != null && isExcludedNode(current)){
                String firstKey = current.getChildren().keySet().iterator().next();
                current = current.getChildren().get(firstKey);
            }
            result.add(new SourceEntry(originName, current.getPath(), current.isDirectory()));
        }
        return result;
    }

    private AbstractNode geNodeEntry(ZipEntry entry, ZipNode root) {
        String path = entry.getName();
        String[] splits = path.split("/");
        AbstractNode parentNode = root;
        for (String currentSplit : splits) {
            String currentName = currentSplit;
            if (currentSplit.contains(".")){
            }
            AbstractNode currentNode = parentNode.getChildren().get(currentName+"/");
            if (currentNode == null){
                currentNode = parentNode.getChildren().get(currentName);
            }

            if (currentNode == null) {
                Log.w("RAFA", "currentNode == null");
            }

            parentNode = currentNode;
        }

        return parentNode;
    }

    protected boolean isExcludedNode(AbstractNode node) {
        Map<String, AbstractNode> childrens = node.getChildren();
        if (childrens==null){
            return false;
        }
        int folderCount = 0;
        int fileCount = 0;
        for (Map.Entry<String, AbstractNode> mapEntry : childrens.entrySet()) {
            AbstractNode current = mapEntry.getValue();
            if (current.isDirectory())
                folderCount++;
            else
                fileCount++;
        }

        if (fileCount == 0 && folderCount == 1){
            Log.w("TAG","isExcludedNode " + node.getName());
            return true;
        }

        return false;
    }

    protected boolean isExcludedEntry(ZipEntry entry) {
        String name = entry.getName();
        if (name.startsWith("META-INF/"))
            return true;

        return false;
    }
}
