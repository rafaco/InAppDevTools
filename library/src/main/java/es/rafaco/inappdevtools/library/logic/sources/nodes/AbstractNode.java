package es.rafaco.inappdevtools.library.logic.sources.nodes;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractNode {

    public static final char FOLDER_SEP = '/';
    protected AbstractNode parent;
    protected Map<String, AbstractNode> children;
    protected boolean directory;

    private String entryName;
    private String entryPath;


    protected AbstractNode(boolean isDirectory) {
        if(isDirectory) {
            directory = true;
            children = new LinkedHashMap<String, AbstractNode>();
        }
        else {
            directory = false;
            children = Collections.emptyMap();
        }
    }

    public boolean isDirectory() {
        return directory;
    }

    public AbstractNode getParent() {
        return parent;
    }

    public Map<String, AbstractNode> getChildren() {
        return Collections.unmodifiableMap(children);
    }



    public abstract String getName();
    public abstract String getPath();
    public abstract String toString();




    /**
     * prints a simple tree view of this ZipNode. Without params.
     */
    public void printTree(){
        printTree("", " ", "");
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
        Iterator<AbstractNode> iterator =
                this.getChildren().values().iterator();
        while(iterator.hasNext()) {
            AbstractNode child = iterator.next();
            if(!iterator.hasNext() ) {
                // last item, without the "|"
                nextSelf = " ╰─ ";
                nextSub =  "   ";
            }
            child.printTree(subPrefix, nextSelf, nextSub);
        }
    }
}