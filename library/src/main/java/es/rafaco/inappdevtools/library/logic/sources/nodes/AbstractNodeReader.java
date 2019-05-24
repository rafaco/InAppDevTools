package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

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
     * category node name
     */
    protected String prefix;

    /**
     * creates a new NodeReader.
     */
    public AbstractNodeReader() {
        this.collected = new HashMap<String, AbstractNode>();
        collected.put("", root);
    }

    /**
     * creates a new NodeReader reusing collected map from another NodeReader
     */
    public AbstractNodeReader(AbstractNodeReader previousReader, String prefix) {
        this.collected = previousReader.collected;
        this.root = previousReader.root;
        this.prefix = prefix;
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
        if (!TextUtils.isEmpty(prefix) && !nodeName.startsWith(prefix)){
            nodeName = prefix+ "/" + nodeName;
        }

        int slashIndex = nodeName.lastIndexOf(FOLDER_SEP, nodeName.length()-2);
        if(slashIndex < 0) {
            // top-level-node
            connectParent(root, node, nodeName);
            return;
        }

        String parentName = nodeName.substring(0, slashIndex);
        AbstractNode parent = createParentNode(parentName + FOLDER_SEP);
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
