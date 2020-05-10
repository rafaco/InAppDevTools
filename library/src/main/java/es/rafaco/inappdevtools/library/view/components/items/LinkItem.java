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

package es.rafaco.inappdevtools.library.view.components.items;

public class LinkItem {

    private final Runnable onClick;
    String title;
    String overview;
    int icon;
    int color;

    public LinkItem(String title, int icon, int color, Runnable onclick) {
        this(title, null, icon, color, onclick);
    }

    public LinkItem(String title, String overview, int icon, int color, Runnable onclick) {
        this.title = title;
        this.overview = overview;
        this.icon = icon;
        this.color = color;
        this.onClick = onclick;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LinkItem setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void onClick(){
        if (onClick != null) onClick.run();
    }
}
