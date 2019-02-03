package es.rafaco.inappdevtools.library.view.components;

public class TraceGroupItem {

    private boolean expanded;
    private int count;
    private String title;
    private String subtitle;
    private Runnable runnable;

    public TraceGroupItem(boolean expanded, int count, String title, String subtitle, Runnable runnable) {
        this.expanded = expanded;
        this.count = count;
        this.title = title;
        this.subtitle = subtitle;
        this.runnable = runnable;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
