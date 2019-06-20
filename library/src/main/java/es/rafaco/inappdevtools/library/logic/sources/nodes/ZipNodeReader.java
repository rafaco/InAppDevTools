package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.text.TextUtils;
import android.util.Log;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static es.rafaco.inappdevtools.library.logic.sources.nodes.AbstractNode.FOLDER_SEP;

/**
 * Helper class for {@link ZipNode#fromZipFile}.
 * It helps creating a tree of ZipNodes from a ZipFile.
 */
public class ZipNodeReader extends AbstractNodeReader {
    /**
     * The file to be read.
     */
    private ZipFile file;

    /**
     * creates a new ZipNodeReader from a ZipFile.
     */
    public ZipNodeReader(ZipFile f) {
        super();
        this.file = f;
        root = new StandardNode("root", "/", true);
    }

    public ZipNodeReader(ZipFile f, AbstractNodeReader previousReader, String prefix) {
        super(previousReader, prefix);
        this.file = f;
    }

    /**
     * reads all entries, creates the corresponding Nodes and
     * returns the root node.
     */
    public AbstractNode populate() {
        for(Enumeration<? extends ZipEntry> entries = file.entries();
            entries.hasMoreElements(); ) {
            addEntry(entries.nextElement());
        }
        return root;
    }

    /**
     * adds an entry to our tree.
     *
     * This may create a new ZipNode and then connects
     * it to its parent node.
     * @returns the ZipNode corresponding to the entry.
     */
    private AbstractNode addEntry(ZipEntry entry) {
        String name = entry.getName();
        AbstractNode node = collected.get(name);
        if(node != null) {
            // already in the map
            return node;
        }
        node = new ZipNode(file, entry, prefix);
        collected.put(name, node);
        findParent(node);
        return node;
    }

    protected AbstractNode createParentNode(String parentName) {
        if (parentName.indexOf(FOLDER_SEP)==parentName.lastIndexOf(FOLDER_SEP)){
            AbstractNode node = collected.get(parentName);
            if(node != null) {
                return node;
            }
        }

        if (!TextUtils.isEmpty(prefix) && parentName.startsWith(prefix)){
            parentName = parentName.substring(parentName.indexOf(FOLDER_SEP)+1);
        }
        ZipEntry entry = file.getEntry(parentName);
        if (entry==null){
            Log.e("TAG", "no entry for " + parentName);
        }
        return addEntry(entry);
    }
}
