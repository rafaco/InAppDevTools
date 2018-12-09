package es.rafaco.inappdevtools.logic.sources;

public class SourceEntry {

    String origin;
    String name;
    boolean isDirectory;

    public SourceEntry(String origin, String name, boolean isDirectory) {
        this.origin = origin;
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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