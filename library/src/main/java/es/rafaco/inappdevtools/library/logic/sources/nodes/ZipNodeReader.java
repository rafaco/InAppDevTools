package es.rafaco.inappdevtools.library.logic.sources.nodes;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


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
    ZipNodeReader(ZipFile f) {
        super();
        this.file = f;
        root = new ZipNode(f, null);
    }

    /**
     * reads all entries, creates the corresponding Nodes and
     * returns the root node.
     */
    public ZipNode populate() {
        for(Enumeration<? extends ZipEntry> entries = file.entries();
            entries.hasMoreElements(); ) {
            addEntry(entries.nextElement());
        }
        return (ZipNode) root;
    }

    /**
     * adds an entry to our tree.
     *
     * This may create a new ZipNode and then connects
     * it to its parent node.
     * @returns the ZipNode corresponding to the entry.
     */
    private ZipNode addEntry(ZipEntry entry) {
        String name = entry.getName();
        ZipNode node = (ZipNode) collected.get(name);
        if(node != null) {
            // already in the map
            return node;
        }
        node = new ZipNode(file, entry);
        collected.put(name, node);
        findParent(node);
        return node;
    }

    protected ZipNode createParentNode(String parentName) {
        return addEntry(file.getEntry(parentName));
    }

    /**
     * connects a parent node with its child node.
     */
    private void connectParent(ZipNode parent, ZipNode child,
                               String childName) {
        child.parent = parent;
        parent.children.put(childName, child);
    }
}