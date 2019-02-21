package es.rafaco.inappdevtools.library.view.components.flex;

public class TraceGroupItem {

    private String tag;
    private int index;
    private int count;
    private boolean expanded;
    private boolean lastOnCollapsed = false;
    private int color;

    public TraceGroupItem(String tag, int index, int count, boolean expanded) {
        this.tag = tag;
        this.index = index;
        this.count = count;
        this.expanded = expanded;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isLastOnCollapsed() {
        return lastOnCollapsed;
    }

    public void setLastOnCollapsed(boolean lastOnCollapsed) {
        this.lastOnCollapsed = lastOnCollapsed;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
