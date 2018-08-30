package es.rafaco.devtools.view.overlay.screens.info;

import java.util.ArrayList;
import java.util.List;

public class InfoCollection {

    private String name;
    private List<InfoGroup> entries;

    public InfoCollection(Builder builder) {
        this.name = builder.name;
        this.entries = builder.entries;
    }

    public String getName() {
        return name;
    }

    public List<InfoGroup> getEntries() {
        return entries;
    }

    public void setEntries(List<InfoGroup> entries) {
        this.entries = entries;
    }


    public static class Builder {
        private String name;
        private List<InfoGroup> entries;

        public Builder(String name) {
            this.name = name;
            this.entries = new ArrayList<>();
        }

        public Builder add(InfoGroup entry) {
            this.entries.add(entry);
            return this;
        }


        public Builder set(List<InfoGroup> entries) {
            this.entries = entries;
            return this;
        }

        public InfoCollection build() {
            return new InfoCollection(this);
        }
    }
}
