package es.rafaco.inappdevtools.library.logic.runnables;

public class RunButton {

    public RunButton(String title, Runnable performer) {}

    public RunButton(String title, int icon, Runnable performer) {}

    public RunButton(String title, int icon, Runnable performer, Runnable callback) {}

    public RunButton(String title, int icon, int color, Runnable performer, Runnable callback) {}

    public String getTitle() {
        return new String();
    }

    public void setTitle(String title) {}

    public Runnable getPerformer() {
        return null;
    }

    public void setPerformer(Runnable performer) {}

    public Runnable getCallback() {
        return null;
}

    public void setCallback(Runnable callback) {}

    public int getIcon() {
        return 0;
    }

    public void setIcon(int icon) {}

    public int getColor() {
        return 0;
    }

    public void setColor(int icon) {}

    public void run(){}
}
