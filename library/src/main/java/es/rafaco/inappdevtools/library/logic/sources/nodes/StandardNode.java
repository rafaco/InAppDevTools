package es.rafaco.inappdevtools.library.logic.sources.nodes;

public class StandardNode extends AbstractNode{

    protected StandardNode(String name, String path, boolean isDirectory) {
        super(isDirectory);
        this.name = name;
        this.path = path;
    }

    @Override
    public String getName() {
        if(name == null)
            return "/";
        return name.substring(name.lastIndexOf(FOLDER_SEP,
                name.length()-2)+1);
    }

    public String getEntryName() {
        return name;
    }

    public String getPath(){
        if (name == null) {
            return FOLDER_SEP + "";
        }
        return path;
    }

    /*
    public static StandardNode fromAssets(Context context) {
        return (StandardNode) new AssetsReader(context, "assets").populate();
    }*/
}