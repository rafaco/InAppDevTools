package es.rafaco.inappdevtools.library.logic.info.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InfoReportData {

    private String title;
    private int icon;
    private String overview;
    private List<InfoGroupData> entries;

    public InfoReportData(Builder builder) {
        this.title = builder.title;
        this.icon = builder.icon;
        this.entries = builder.entries;
        this.overview = builder.overview;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public String getOverview() {
        return overview;
    }

    public List<InfoGroupData> getEntries() {
        return entries;
    }

    public void setEntries(List<InfoGroupData> entries) {
        this.entries = entries;
    }

    public void removeGroup(int index) {
        if (index > 0 && index < entries.size()){
            entries.remove(index);
        }
    }

    public void removeGroupEntries(int index) {
        if (index > 0 && index < entries.size()){
            entries.get(index).removeEntries();
        }
    }

    @Override
    public String toString(){
        String result = Humanizer.newLine();

        if (!TextUtils.isEmpty(getTitle())){
            result += String.format("[ REPORT %s ]", getTitle());
            result += Humanizer.newLine();
        }

        if (!TextUtils.isEmpty(getOverview())){
            result += getOverview();
            result += Humanizer.newLine();
        }

        if (entries.size()>0){
            for (InfoGroupData entry : entries){
                result += entry.toString();
            }
        }

        return result;
    }

    public static class Builder {
        private String title;
        private int icon;
        private String overview;
        private List<InfoGroupData> entries;

        public Builder(InfoReport report) {
            this.title = report.getTitle();
            this.icon = report.getIcon();
            this.entries = new ArrayList<>();
        }

        public Builder(String title) {
            this.title = title;
            this.entries = new ArrayList<>();
        }

        public Builder setIcon(int icon) {
            this.icon = icon;
            return this;
        }

        public Builder setOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder setInfoGroup(List<InfoGroupData> entries) {
            this.entries = entries;
            return this;
        }
        
        public Builder add(InfoGroupData entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder add(String title, String text) {
            add(new InfoGroupData.Builder(title)
                    .add(text)
                    .build());
            return this;
        }

        public Builder add(String text) {
            add("", text);
            return this;
        }

        public Builder add() {
            return add("");
        }

        public InfoReportData build() {
            return new InfoReportData(this);
        }
    }
}
