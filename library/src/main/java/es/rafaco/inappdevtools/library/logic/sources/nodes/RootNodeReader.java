package es.rafaco.inappdevtools.library.logic.sources.nodes;

public class RootNodeReader extends AbstractNodeReader {

    public RootNodeReader() {
        super();
        root = new StandardNode("root", "/", true);
    }

    @Override
    public AbstractNode populate() {
        return null;
    }

    public void addNode(String name) {
        addEntry(name, name + "/", true);
    }


    protected AbstractNode addEntry(String entryName, String entryPath, boolean isDirectory) {
        AbstractNode node = collected.get(entryPath);
        if(node != null) {
            // already in the map
            return node;
        }
        node = new StandardNode(entryName, entryPath, isDirectory);
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
