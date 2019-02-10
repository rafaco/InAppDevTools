package es.rafaco.inappdevtools.library.view.components.flex;

public class TraceGroupItem {

    private String groupKey;
    private int index;
    private int count;
    private boolean expanded;
    private boolean lastOnCollapsed = false;

    public TraceGroupItem(String groupKey, int index, int count, boolean expanded) {
        this.groupKey = groupKey;
        this.index = index;
        this.count = count;
        this.expanded = expanded;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
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

    public void setLastOnCollapsed(boolean lastOnCollapsed) {
        this.lastOnCollapsed = lastOnCollapsed;
    }

    public boolean getLastOnCollapsed() {
        return lastOnCollapsed;
    }
}
