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

package es.rafaco.inappdevtools.library.view.components.cards;

//#ifdef ANDROIDX
//@import androidx.annotation.StringRes;
//#else
import android.support.annotation.StringRes;
//#endif

public class WidgetData {

    private String title;
    private String mainContent;
    private String secondContent;
    private int icon;
    private int bgColor;
    private Runnable performer;

    public WidgetData(Builder builder) {
        this.title = builder.title;
        this.icon = builder.icon;
        this.mainContent = builder.mainContent;
        this.secondContent = builder.secondContent;
        this.performer = builder.performer;
        this.bgColor = builder.bgColor;

    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public String getMainContent() {
        return mainContent;
    }

    public String getSecondContent() {
        return secondContent;
    }

    public Runnable getPerformer() {
        return performer;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }


    public static class Builder {
        private String title;
        private int icon;
        private int bgColor;
        private String mainContent;
        private String secondContent;
        private Runnable performer;


        public Builder() {
            this("");
        }

        public Builder(String title) {
            this.title = title;
        }

        public Builder setIcon(@StringRes int icon) {
            this.icon = icon;
            return this;
        }

        public Builder setBgColor(int color) {
            this.bgColor = color;
            return this;
        }

        public Builder setMainContent(String text) {
            this.mainContent = text;
            return this;
        }

        public Builder setSecondContent(String text) {
            this.secondContent = text;
            return this;
        }

        public Builder setPerformer(Runnable performer) {
            this.performer = performer;
            return this;
        }

        public WidgetData build() {
            return new WidgetData(this);
        }
    }
}
