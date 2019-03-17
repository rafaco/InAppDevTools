package es.rafaco.inappdevtools.library.logic.sources.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractNode {

    public static final char FOLDER_SEP = '/';
    protected AbstractNode parent;
    protected Map<String, AbstractNode> children;
    protected boolean directory;

    protected String name;
    protected String path;


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

    public abstract String getName();
    public abstract String getPath();

    public boolean isDirectory() {
        return directory;
    }
    public AbstractNode getParent() {
        return parent;
    }
    public Map<String, AbstractNode> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    /**
     * a string representation of this ZipNode.
     */
    public String toString() {
        return String.format("%s [%s] isDir [%s] at [%s]",
                this.getClass().getSimpleName(),
                getName(),
                String.valueOf(isDirectory()),
                getPath());
    }

    /**
     * Easily prints a simple tree view of this ZipNode, without params.
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

    public List<AbstractNode> filter(String keyword) {
        List<AbstractNode> result = new ArrayList<>();
        if (getName().contains(keyword) || getPath().contains(keyword)){
            result.add(this);
        }

        if (isDirectory() && !children.isEmpty()){
            for (AbstractNode child : children.values() ) {
                List<AbstractNode> filter = child.filter(keyword);
                if (!filter.isEmpty()){
                    result.addAll(filter);
                }
            }
        }
        return result;
    }
}