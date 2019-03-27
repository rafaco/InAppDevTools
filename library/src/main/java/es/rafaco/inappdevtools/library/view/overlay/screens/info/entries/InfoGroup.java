package es.rafaco.inappdevtools.library.view.overlay.screens.info.entries;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.utils.DateUtils;

public class InfoGroup {

    private String name;
    private List<InfoEntry> entries;

    public InfoGroup(Builder builder) {
        this.name = builder.name;
        this.entries = builder.entries;
    }

    public String getName() {
        return name;
    }

    public List<InfoEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<InfoEntry> entries) {
        this.entries = entries;
    }

    public void add(InfoEntry entry) {
        this.entries.add(entry);
    }

    public void removeEntries() {
        entries.clear();
    }

    @Override
    public String toString(){
        String result = "";
        String formatTitle = "%s:";
        String lineBreak = "\n";

        if (!TextUtils.isEmpty(getName())) {
            result += lineBreak;
            result += String.format(formatTitle, getName());
            result += lineBreak;
            result += lineBreak;
        }

        for (InfoEntry entry : entries){
            result += entry.toString();
        }
        return result;
    }




    public static class Builder {
        private String name;
        private List<InfoEntry> entries;

        public Builder() {
            this("");
        }

        public Builder(String name) {
            this.name = name;
            this.entries = new ArrayList<>();
        }

        public Builder add(InfoEntry entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder add() {
            add(new InfoEntry("", ""));
            return this;
        }

        public Builder add(String text) {
            add(new InfoEntry("", text));
            return this;
        }

        public Builder add(String label, List<String> values) {
            add(new InfoEntry(label, values));
            return this;
        }

        public Builder add(String label, String value) {
            add(new InfoEntry(label, value));
            return this;
        }

        public Builder add(String label, boolean value) {
            add(new InfoEntry(label, String.valueOf(value)));
            return this;
        }

        public Builder add(String label, long value) {
            add(new InfoEntry(label, String.valueOf(value)));
            return this;
        }

        public Builder addDate(String label, long date) {
            add(new InfoEntry(label, DateUtils.format(date)));
            return this;
        }

        public Builder set(List<InfoEntry> entries) {
            this.entries = entries;
            return this;
        }

        public InfoGroup build() {
            return new InfoGroup(this);
        }
    }
}
