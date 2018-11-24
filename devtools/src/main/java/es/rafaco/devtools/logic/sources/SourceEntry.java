package es.rafaco.devtools.logic.sources;

public class SourceEntry {

    String name;
    int position;
    boolean isDirectory;

    public SourceEntry(String name, int position, boolean isDirectory) {
        this.name = name;
        this.position = position;
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getFileName() {
        int lastFound = name.lastIndexOf("/");
        return name.substring(lastFound + 1, name.length());
    }
}
