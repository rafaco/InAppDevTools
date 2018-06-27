package es.rafaco.devtools.view.overlay.tools.info;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String toString(){
        String result = "";
        String formatTitle = "%s:";
        String lineBreak = "\n";

        result += lineBreak;
        result += String.format(formatTitle, getName());
        result += lineBreak;

        for (InfoEntry entry : entries){
            result += entry.toString();
        }
        return result;
    }




    public static class Builder {
        private String name;
        private List<InfoEntry> entries;

        public Builder(String name) {
            this.name = name;
            this.entries = new ArrayList<>();
        }

        public Builder add(InfoEntry entry) {
            this.entries.add(entry);
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


        public Builder set(List<InfoEntry> entries) {
            this.entries = entries;
            return this;
        }

        public InfoGroup build() {
            return new InfoGroup(this);
        }
    }
}
