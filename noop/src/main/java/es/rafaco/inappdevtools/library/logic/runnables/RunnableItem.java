package es.rafaco.inappdevtools.library.logic.runnables;

public class RunnableItem {

    public RunnableItem(String title, Runnable performer) {}

    public RunnableItem(String title, int icon, Runnable performer) {}

    public RunnableItem(String title, int icon, Runnable performer, Runnable callback) {}

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

    public void run(){}
}
