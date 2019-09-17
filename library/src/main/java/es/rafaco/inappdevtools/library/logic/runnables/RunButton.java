package es.rafaco.inappdevtools.library.logic.runnables;

public class RunButton {
    String title;
    Runnable performer;
    Runnable callback;
    int icon;
    int color;

    public RunButton(String title, Runnable performer) {
        this.title = title;
        this.performer = performer;
    }

    public RunButton(String title, int icon, Runnable performer) {
        this(title, performer);
        this.icon = icon;
    }

    public RunButton(String title, int icon, int colorResId, Runnable performer) {
        this(title, icon, performer);
        this.color = colorResId;
    }

    public RunButton(String title, int icon, Runnable performer, Runnable callback) {
        this(title, icon, performer);
        this.callback = callback;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void run(){
        getPerformer().run();
        if (getCallback()!= null)
            getCallback().run();
    }
}
