package es.rafaco.inappdevtools.library.view.overlay.screens.storage;

public class DatabaseSelection {
    private int key;
    private String table;
    private boolean mode;

    public DatabaseSelection(int key, String table, boolean mode) {
        this.key = key;
        this.table = table;
        this.mode = mode;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean getMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
