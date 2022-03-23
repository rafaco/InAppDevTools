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

package org.inappdevtools.library.view.components.items;

import org.inappdevtools.library.view.components.base.FlexData;

public class HeaderDoubleFlexData extends FlexData implements CollapsibleFlexViewHolder.ICollapsibleData {

    String title;
    String content;
    Runnable performer;

    String imagePath;
    int icon;
    int navIcon;
    String navCount;
    int bgColor;
    int accentColor;
    int titleColor;
    private int navAddIcon;
    private Runnable navAddRunnable;
    private boolean expanded;

    public HeaderDoubleFlexData(String title, Runnable performer) {
        super();
        this.title = title;
        this.performer = performer;
    }

    public HeaderDoubleFlexData(String title, int icon, Runnable performer) {
        this(title, performer);
        this.icon = icon;
    }

    public HeaderDoubleFlexData(String title, String content, int icon, Runnable performer) {
        this(title, icon, performer);
        this.content = content;
    }

    public HeaderDoubleFlexData(String title, int icon, int bgColorResId, Runnable performer) {
        this(title, icon, performer);
        this.bgColor = bgColorResId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Runnable getPerformer() {
        return performer;
    }

    public void setPerformer(Runnable performer) {
        this.performer = performer;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getNavIcon() {
        return navIcon;
    }

    public void setNavIcon(int navIcon) {
        this.navIcon = navIcon;
    }

    public String getNavCount() {
        return navCount;
    }

    public HeaderDoubleFlexData setNavCount(Integer navCount) {
        this.navCount = (navCount != null) ? navCount.toString() : null;
        return this;
    }

    public HeaderDoubleFlexData setNavCount(String navCount) {
        this.navCount = navCount;
        return this;
    }

    public int getNavAddIcon() {
        return navAddIcon;
    }

    public Runnable getNavAddRunnable() {
        return navAddRunnable;
    }

    public HeaderDoubleFlexData setNavAdd(int addIcon, Runnable addRunnable) {
        this.navAddIcon = addIcon;
        this.navAddRunnable = addRunnable;
        return this;
    }

    public int getBgColor() {
        return bgColor;
    }

    public HeaderDoubleFlexData setBgColor(int bg_color) {
        this.bgColor = bg_color;
        return this;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(int accentColor) {
        this.accentColor = accentColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void run(){
        getPerformer().run();
    }

    @Override
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }
}
