/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.logic.documents.data;

import android.text.TextUtils;

//#ifdef ANDROIDX
//@import androidx.annotation.StringRes;
//#else
import android.support.annotation.StringRes;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.view.components.flex.OverviewData;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class DocumentData {

    private String title;
    private int icon;
    private String overview;
    private List<DocumentSectionData> sections;

    public DocumentData(Builder builder) {
        this.title = builder.title;
        this.icon = builder.icon;
        this.sections = builder.sections;
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

    public List<DocumentSectionData> getSections() {
        return sections;
    }

    public void setSections(List<DocumentSectionData> sections) {
        this.sections = sections;
    }

    public void removeGroup(int index) {
        if (index > 0 && index < sections.size()){
            sections.remove(index);
        }
    }

    public void removeGroupEntries(int index) {
        if (index > 0 && index < sections.size()){
            sections.get(index).removeEntries();
        }
    }

    public OverviewData getOverviewData() {
        return new OverviewData(getTitle(), getOverview(),
                getIcon(), R.color.rally_white);
    }


    @Override
    public String toString(){
        String result = Humanizer.newLine();

        if (!TextUtils.isEmpty(getTitle())){
            result += String.format("# %s", getTitle());
            result += Humanizer.fullStop();
        }

        if (!TextUtils.isEmpty(getOverview())){
            result += Humanizer.prependLines(getOverview(), " * ");
            result += Humanizer.newLine();
        }

        if (sections.size()>0){
            for (DocumentSectionData entry : sections){
                result += entry.toString();
            }
        }

        return result;
    }

    public static class Builder {
        private String title;
        private int icon;
        private String overview;
        private List<DocumentSectionData> sections;

        public Builder(Document document) {
            this.title = document.getName();
            this.icon = document.getIcon();
            this.sections = new ArrayList<>();
        }

        public Builder(String title) {
            this.title = title;
            this.sections = new ArrayList<>();
        }

        public Builder setIcon(@StringRes int icon) {
            this.icon = icon;
            return this;
        }

        public Builder setOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder setSections(List<DocumentSectionData> sections) {
            this.sections = sections;
            return this;
        }
        
        public Builder add(DocumentSectionData entry) {
            this.sections.add(entry);
            return this;
        }

        public Builder add(String title, String text) {
            add(new DocumentSectionData.Builder(title)
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

        public DocumentData build() {
            return new DocumentData(this);
        }
    }
}
