package es.rafaco.inappdevtools.library.logic.info.data;

import android.text.TextUtils;

//#ifdef ANDROIDX
//@import androidx.annotation.StringRes;
//#else
import android.support.annotation.StringRes;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InfoGroupData {

    private String title;
    private int icon;
    private String overview;
    private List<InfoEntryData> entries;
    private List<RunButton> buttons;
    private Boolean isExpanded;

    public InfoGroupData(Builder builder) {
        this.title = builder.name;
        this.icon = builder.icon;
        this.overview = builder.overview;
        this.buttons = builder.buttons;
        this.entries = builder.entries;
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

    public List<RunButton> getButtons() {
        return buttons;
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

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public String entriesToString(){
        String result = "";
        for (InfoEntryData entry : entries){
            result += entry.toString();
        }
        return result;
    }
    @Override
    public String toString(){
        String result = "";
        String formatTitle = "%s:";

        if (!TextUtils.isEmpty(getTitle())) {
            result += Humanizer.newLine();
            result += String.format(formatTitle, getTitle());
            result += Humanizer.newLine();
        }
        result += entriesToString();
        return result;
    }



    public static class Builder {
        private String name;
        private int icon;
        private String overview;
        private List<RunButton> buttons;
        private List<InfoEntryData> entries;

        public Builder() {
            this("");
        }

        public Builder(String name) {
            this.name = name;
            this.entries = new ArrayList<>();
            this.buttons = new ArrayList<>();
        }

        public Builder setIcon(@StringRes int icon) {
            this.icon = icon;
            return this;
        }

        public Builder setOverview(String text) {
            this.overview = text;
            return this;
        }

        public Builder addButton(RunButton button) {
            this.buttons.add(button);
            return this;
        }

        public Builder add(InfoEntryData entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder add(List<InfoEntryData> entries) {
            for (InfoEntryData entry : entries){
                add(entry);
            }
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
