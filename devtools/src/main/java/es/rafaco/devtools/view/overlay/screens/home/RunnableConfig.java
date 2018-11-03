package es.rafaco.devtools.view.overlay.screens.home;

public class RunnableConfig {
    String key;
    String title;
    Runnable run;
    Runnable callback;
    int icon;

    public RunnableConfig(String key, String title, Runnable run) {
        this.key = key;
        this.title = title;
        this.run = run;
    }

    public RunnableConfig(String key, String title, int icon, Runnable run) {
        this(key, title, run);
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

    public Runnable getRun() {
        return run;
    }

    public void setRun(Runnable run) {
        this.run = run;
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
}