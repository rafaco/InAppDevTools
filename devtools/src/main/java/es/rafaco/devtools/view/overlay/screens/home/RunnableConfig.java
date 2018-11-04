package es.rafaco.devtools.view.overlay.screens.home;

public class RunnableConfig {
    String key;
    String title;
    Runnable performer;
    Runnable callback;
    int icon;

    public RunnableConfig(String key, String title, Runnable performer) {
        this.key = key;
        this.title = title;
        this.performer = performer;
    }

    public RunnableConfig(String key, String title, int icon, Runnable performer) {
        this(key, title, performer);
        this.icon = icon;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Runnable getPerformer() {
        return performer;
    }

    public void setPerformer(Runnable performer) {
        this.performer = performer;
    }

    public Runnable getCallback() {
        return callback;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void run(){
        getPerformer().run();
        if (getCallback()!= null)
            getCallback().run();
    }
}