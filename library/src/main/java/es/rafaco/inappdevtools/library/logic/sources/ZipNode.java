package es.rafaco.inappdevtools.library.logic.sources;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * A immutable wrapper around {@link ZipEntry} allowing
 * simple access of the childs of directory entries.
 *
 * Extracted from: https://stackoverflow.com/questions/5158961/unzip-into-treemap-in-java
 */
public class ZipNode {

    public static final char FOLDER_SEP = '/';
    private ZipNode parent;
    private Map<String,ZipNode> children;
    private boolean directory;

    /**
     * the corresponding Zip entry. If null, this is the root entry.
     */
    private ZipEntry entry;

    /** the ZipFile from where the nodes came. */
    private ZipFile file;


    private ZipNode(ZipFile f, ZipEntry entry) {
        this.file = f;
        this.entry = entry;
        if(entry == null || entry.isDirectory()) {
            directory = true;
            children = new LinkedHashMap<String, ZipNode>();
        }
        else {
            directory = false;
            children = Collections.emptyMap();
        }
    }

    /**
     * returns the last component of the name of
     * the entry, i.e. the file name. If this is a directory node,
     * the name ends with '/'. If this is the root node, the name
     * is simply "/".
     */
    public String getName() {
        if(entry == null)
            return "/";
        String longName = entry.getName();
        return longName.substring(longName.lastIndexOf(FOLDER_SEP,
                longName.length()-2)+1);
    }

    /**
     * gets the corresponding ZipEntry to this node.
     * @return {@code null} if this is the root node (which has no
     *    corresponding entry), else the corresponding ZipEntry.
     */
    public ZipEntry getEntry() {
        return entry;
    }

    /**
     * Gets the ZipFile, from where this ZipNode came.
     */
    public ZipFile getZipFile() {
        return file;
    }

    /**
     * returns true if this node is a directory node.
     */
    public boolean isDirectory() {
        return directory;
    }

    /**
     * returns this node's parent node (null, if this is the root node).
     */
    public ZipNode getParent() {
        return parent;
    }

    /**
     * returns an unmodifiable map of the children of this node,
     * mapping their relative names to the ZipNode objects.
     * (Names of subdirectories end with '/'.)
     * The map is empty if this node is not a directory node.
     */
    public Map<String,ZipNode> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    /**
     * opens an InputStream on this ZipNode. This only works when
     * this is not a directory node, and only before the corresponding
     * ZipFile is closed.
     */
    public InputStream openStream()
            throws IOException
    {
        return file.getInputStream(entry);
    }

    /**
     * a string representation of this ZipNode.
     */
    public String toString() {
        String entryName = "root";
        if (entry != null){
            entryName = entry.getName();
        }
        return "ZipNode [" + entryName + "] in [" + file.getName() + "]";
    }

    public String getPath(){

        if (entry == null) {
            return FOLDER_SEP + "";
        }

       return entry.getName();
    }



    /**
     * creates a ZipNode tree from a ZipFile
     * and returns the root node.
     *
     * The nodes' {@link #openStream()} methods are only usable until the
     * ZipFile is closed, but the structure information remains valid.
     */
    public static ZipNode fromZipFile(ZipFile zf) {
        return new ZipFileReader(zf).process();
    }


    /**
     * Helper class for {@link ZipNode#fromZipFile}.
     * It helps creating a tree of ZipNodes from a ZipFile.
     */
    private static class ZipFileReader {
        /**
         * The file to be read.
         */
        private ZipFile file;

        /**
         * The nodes collected so far.
         */
        private Map<String, ZipNode> collected;

        /**
         * our root node.
         */
        private ZipNode root;

        /**
         * creates a new ZipFileReader from a ZipFile.
         */
        ZipFileReader(ZipFile f) {
            this.file = f;
            this.collected = new HashMap<String, ZipNode>();
            collected.put("", root);
            root = new ZipNode(f, null);
        }

        /**
         * reads all entries, creates the corresponding Nodes and
         * returns the root node.
         */
        ZipNode process() {
            for(Enumeration<? extends ZipEntry> entries = file.entries();
                entries.hasMoreElements(); ) {
                this.addEntry(entries.nextElement());
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
        private ZipNode addEntry(ZipEntry entry) {
            String name = entry.getName();
            ZipNode node = collected.get(name);
            if(node != null) {
                // already in the map
                return node;
            }
            node = new ZipNode(file, entry);
            collected.put(name, node);
            this.findParent(node);
            return node;
        }

        /**
         * makes sure that the parent of a
         * node is in the collected-list as well, and this node is
         * registered as a child of it.
         * If necessary, the parent node is first created
         * and added to the tree.
         */
        private void findParent(ZipNode node) {
            String nodeName = node.entry.getName();
            int slashIndex = nodeName.lastIndexOf(FOLDER_SEP, nodeName.length()-2);
            if(slashIndex < 0) {
                // top-level-node
                connectParent(root, node, nodeName);
                return;
            }
            String parentName = nodeName.substring(0, slashIndex+1);
            ZipNode parent = addEntry(file.getEntry(parentName));
            connectParent(parent, node, nodeName.substring(slashIndex+1));
        }

        /**
         * connects a parent node with its child node.
         */
        private void connectParent(ZipNode parent, ZipNode child,
                                   String childName) {
            child.parent = parent;
            parent.children.put(childName, child);
        }


    }  // class ZipFileReader

    /**
     * test method. Give name of zip file as command line argument.
     */
    public static void main(String[] params)
            throws IOException
    {
        if(params.length < 1) {
            System.err.println("Invocation:  java ZipNode zipFile.zip");
            return;
        }
        ZipFile file = new ZipFile(params[0]);
        ZipNode root = ZipNode.fromZipFile(file);
        file.close();
        root.printTree("", " ", "");
    }

    /**
     * prints a simple tree view of this ZipNode.
     */
    protected void printTree(String prefix,
                           String self,
                           String sub) {
        System.out.println(prefix + self + this.getName());
        String subPrefix = prefix + sub;
        // the prefix strings for the next level.
        String nextSelf = " ├─ ";
        String nextSub =  " │ ";
        Iterator<ZipNode> iterator =
                this.getChildren().values().iterator();
        while(iterator.hasNext()) {
            ZipNode child = iterator.next();
            if(!iterator.hasNext() ) {
                // last item, without the "|"
                nextSelf = " ╰─ ";
                nextSub =  "   ";
            }
            child.printTree(subPrefix, nextSelf, nextSub);
        }
    }
}