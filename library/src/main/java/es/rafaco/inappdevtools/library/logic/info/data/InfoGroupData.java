package es.rafaco.inappdevtools.library.logic.info.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InfoGroupData {

    private String name;
    private List<InfoEntryData> entries;

    public InfoGroupData(Builder builder) {
        this.name = builder.name;
        this.entries = builder.entries;
    }

    public String getName() {
        return name;
    }

    public List<InfoEntryData> getEntries() {
        return entries;
    }

    public void setEntries(List<InfoEntryData> entries) {
        this.entries = entries;
    }

    public void add(InfoEntryData entry) {
        this.entries.add(entry);
    }

    public void removeEntries() {
        entries.clear();
    }

    @Override
    public String toString(){
        String result = "";
        String formatTitle = "%s:";

        if (!TextUtils.isEmpty(getName())) {
            result += Humanizer.newLine();
            result += String.format(formatTitle, getName());
            result += Humanizer.newLine();
        }

        for (InfoEntryData entry : entries){
            result += entry.toString();
        }
        return result;
    }




    public static class Builder {
        private String name;
        private List<InfoEntryData> entries;

        public Builder() {
            this("");
        }

        public Builder(String name) {
            this.name = name;
            this.entries = new ArrayList<>();
        }

        public Builder add(InfoEntryData entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder add() {
            add(new InfoEntryData("", ""));
            return this;
        }

        public Builder add(String text) {
            add(new InfoEntryData("", text));
            return this;
        }

        public Builder add(String label, List<String> values) {
            add(new InfoEntryData(label, values));
            return this;
        }

        public Builder add(String label, String value) {
            add(new InfoEntryData(label, value));
            return this;
        }

        public Builder add(String label, boolean value) {
            add(new InfoEntryData(label, String.valueOf(value)));
            return this;
        }

        public Builder add(String label, long value) {
            add(new InfoEntryData(label, String.valueOf(value)));
            return this;
        }

        public Builder addDate(String label, long date) {
            add(new InfoEntryData(label, DateUtils.format(date)));
            return this;
        }

        public Builder set(List<InfoEntryData> entries) {
            this.entries = entries;
            return this;
        }

        public InfoGroupData build() {
            return new InfoGroupData(this);
        }
    }
}
