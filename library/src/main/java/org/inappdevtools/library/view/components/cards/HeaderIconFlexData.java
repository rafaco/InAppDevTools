/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.components.cards;

//#ifdef ANDROIDX
//@import androidx.annotation.StringRes;
//#else
import android.support.annotation.StringRes;
//#endif

import org.inappdevtools.library.view.components.items.CollapsibleFlexViewHolder;
import org.inappdevtools.library.view.components.base.FlexData;

public class HeaderIconFlexData extends FlexData implements CollapsibleFlexViewHolder.ICollapsibleData {

    private String title;
    private int icon;
    private String overview;
    private boolean isExpandable;
    private boolean isExpanded;
    private boolean hiddenTitleWhenExpanded;

    public HeaderIconFlexData(HeaderIconFlexData.Builder builder) {
        super();
        this.title = builder.name;
        this.icon = builder.icon;
        this.overview = builder.overview;
        this.isExpandable = builder.isExpandable;
        this.isExpanded = builder.isExpanded;
        this.hiddenTitleWhenExpanded = builder.hiddenTitleWhenExpanded;
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

    public void setHiddenTitleWhenExpanded(boolean hiddenTitleWhenExpanded) {
        this.hiddenTitleWhenExpanded = hiddenTitleWhenExpanded;
    }

    public boolean isHiddenTitleWhenExpanded() {
        return hiddenTitleWhenExpanded;
    }


    public static class Builder {
        private String name;
        private int icon;
        private String overview;
        private boolean isExpandable = true;
        private boolean isExpanded;
        private boolean hiddenTitleWhenExpanded;

        public Builder() {
            this("");
        }

        public Builder(String name) {
            this.name = name;
        }

        public HeaderIconFlexData.Builder setIcon(@StringRes int icon) {
            this.icon = icon;
            return this;
        }

        public HeaderIconFlexData.Builder setOverview(String text) {
            this.overview = text;
            return this;
        }

        public HeaderIconFlexData.Builder setExpandable(boolean isExpandable) {
            this.isExpandable = isExpandable;
            return this;
        }

        public HeaderIconFlexData.Builder setExpanded(boolean isExpanded) {
            this.isExpanded = isExpanded;
            return this;
        }

        public HeaderIconFlexData.Builder setHiddenTitleWhenExpanded(boolean hiddenTitleWhenExpanded) {
            this.hiddenTitleWhenExpanded = hiddenTitleWhenExpanded;
            return this;
        }

        public HeaderIconFlexData build() {
            return new HeaderIconFlexData(this);
        }
    }
}

