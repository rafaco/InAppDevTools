package es.rafaco.inappdevtools.library.logic.sources.nodes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static es.rafaco.inappdevtools.library.logic.sources.nodes.ZipNode.FOLDER_SEP;

/**
 * Helper class for {@link ZipNode#fromZipFile}.
 * It helps creating a tree of AbstractNodes from a source.
 */
public abstract class AbstractNodeReader {

    /**
     * The nodes collected so far.
     */
    protected Map<String, AbstractNode> collected;

    /**
     * our root node.
     */
    protected AbstractNode root;

    /**
     * creates a new NodeReader.
     */
    public AbstractNodeReader() {
        this.collected = new HashMap<String, AbstractNode>();
        collected.put("", root);
    }

    /**
     * reads all entries, creates the corresponding Nodes and
     * returns the root node.
     */
    public abstract AbstractNode populate();


    /**
     * makes sure that the parent of a
     * node is in the collected-list as well, and this node is
     * registered as a child of it.
     * If necessary, the parent node is first created
     * and added to the tree.
     */
    protected void findParent(AbstractNode node) {
        String nodeName = node.getPath();
        int slashIndex = nodeName.lastIndexOf(FOLDER_SEP, nodeName.length()-2);
        if(slashIndex < 0) {
            // top-level-node
            connectParent(root, node, nodeName);
            return;
        }
        String parentName = nodeName.substring(0, slashIndex+1);
        AbstractNode parent = createParentNode(parentName);
        connectParent(parent, node, nodeName.substring(slashIndex+1));
    }

    /**
     * Create the parent node. Defined at implementations.
     */
    protected abstract AbstractNode createParentNode(String parentName);

    /**
     * Connects a parent node with its child node.
     */
    protected void connectParent(AbstractNode parent, AbstractNode child,
                               String childName) {
        child.parent = parent;
        parent.children.put(childName, child);
    }
}