package es.rafaco.inappdevtools.library.view.overlay.screens.info.structs;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.utils.AppInfoUtils;

public class InfoReport {

    private String name;
    private List<InfoGroup> entries;

    public InfoReport(Builder builder) {
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
        String result = "";
        String formatTitle = "%s:";
        String lineBreak = "\n";

        if (!TextUtils.isEmpty(getName())){
            result += lineBreak;
            result += String.format(formatTitle, getName());
            result += lineBreak;
        }

        if (entries.size()>0){
            for (InfoGroup entry : entries){
                result += entry.toString();
            }
        }

        return result;
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

        public Builder add() {
            return add("");
        }

        public Builder add(String text) {
            add("", text);
            return this;
        }

        public Builder add(String title, String text) {
            add(new InfoGroup.Builder(title)
                    .add(text)
                    .build());
            return this;
        }


        public Builder set(List<InfoGroup> entries) {
            this.entries = entries;
            return this;
        }

        public InfoReport build() {
            return new InfoReport(this);
        }
    }
}
