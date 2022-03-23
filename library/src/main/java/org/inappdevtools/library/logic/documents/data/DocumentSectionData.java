/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.logic.documents.data;

import android.text.TextUtils;

//#ifdef ANDROIDX
//@import androidx.annotation.StringRes;
//#else
import android.support.annotation.StringRes;
//#endif

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.view.components.base.FlexData;
import org.inappdevtools.library.logic.utils.DateUtils;
import org.inappdevtools.library.view.utils.Humanizer;

public class DocumentSectionData {

    private String title;
    private int icon;
    private String overview;
    private List<DocumentEntryData> entries;
    private List<Object> internalData;

    //Extract to ComplexCardData
    private List<FlexData> buttons;
    private boolean isExpandable;
    private boolean isExpanded;

    public DocumentSectionData(Builder builder) {
        this.title = builder.name;
        this.icon = builder.icon;
        this.overview = builder.overview;
        this.buttons = builder.buttons;
        this.entries = builder.entries;
        this.isExpandable = builder.isExpandable;
        this.isExpanded = builder.isExpanded;
        this.internalData = builder.internalData;
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

    public List<FlexData> getButtons() {
        return buttons;
    }

    public List<DocumentEntryData> getEntries() {
        return entries;
    }

    public void setEntries(List<DocumentEntryData> entries) {
        this.entries = entries;
    }

    public void add(DocumentEntryData entry) {
        this.entries.add(entry);
    }

    public void removeEntries() {
        entries.clear();
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public List<Object> getInternalData() {
        return internalData;
    }

    public void setInternalData(List<Object> internalData) {
        this.internalData = internalData;
    }

    public String entriesToString(){
        String result = "";
        for (DocumentEntryData entry : entries){
            result += entry.toString();
        }
        return result;
    }
    @Override
    public String toString(){
        String result = "";
        String formatTitle = "## %s:";

        if (!TextUtils.isEmpty(getTitle())) {
            result += Humanizer.newLine();
            result += String.format(formatTitle, getTitle());
            result += Humanizer.fullStop();
        }
        result += entriesToString();
        return result;
    }



    public static class Builder {
        private String name;
        private int icon;
        private String overview;
        private List<FlexData> buttons;
        private List<DocumentEntryData> entries;
        private boolean isExpandable = true;
        private boolean isExpanded;
        private List<Object> internalData;

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

        public Builder addButton(FlexData button) {
            this.buttons.add(button);
            return this;
        }

        public Builder add(DocumentEntryData entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder add(List<DocumentEntryData> entries) {
            for (DocumentEntryData entry : entries){
                add(entry);
            }
            return this;
        }

        public Builder add() {
            add(new DocumentEntryData("", ""));
            return this;
        }

        public Builder add(String text) {
            add(new DocumentEntryData("", text));
            return this;
        }

        public Builder add(String label, List<String> values) {
            add(new DocumentEntryData(label, values));
            return this;
        }

        public Builder add(String label, String value) {
            add(new DocumentEntryData(label, value));
            return this;
        }

        public Builder add(String label, boolean value) {
            add(new DocumentEntryData(label, String.valueOf(value)));
            return this;
        }

        public Builder add(String label, long value) {
            add(new DocumentEntryData(label, String.valueOf(value)));
            return this;
        }

        public Builder addDate(String label, long date) {
            add(new DocumentEntryData(label, DateUtils.format(date)));
            return this;
        }

        public Builder set(List<DocumentEntryData> entries) {
            this.entries = entries;
            return this;
        }

        public Builder setExpandable(boolean isExpandable) {
            this.isExpandable = isExpandable;
            return this;
        }

        public Builder setExpanded(boolean isExpanded) {
            this.isExpanded = isExpanded;
            return this;
        }

        public Builder setInternalData(List<Object> internalData) {
            this.internalData = internalData;
            return this;
        }

        public DocumentSectionData build() {
            return new DocumentSectionData(this);
        }
    }
}
