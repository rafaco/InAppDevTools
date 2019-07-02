package es.rafaco.inappdevtools.library.storage.db.entities;

public class AnalysisItem {

    String name;
    int count;
    long percentage;

    public AnalysisItem(String name, int count, long percentage) {
        this.name = name;
        this.count = count;
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getPercentage() {
        return percentage;
    }

    public void setPercentage(long percentage) {
        this.percentage = percentage;
    }
}
