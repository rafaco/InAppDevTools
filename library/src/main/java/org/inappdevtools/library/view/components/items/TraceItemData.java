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

package org.inappdevtools.library.view.components.items;

import android.text.TextUtils;

import org.inappdevtools.library.storage.db.entities.Sourcetrace;
import org.inappdevtools.library.view.components.base.FlexData;
import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;

public class TraceItemData extends FlexData {

    public enum Position { START, MIDDLE, END;}
    private Position position = Position.MIDDLE;
    private boolean expanded = true;
    private boolean alwaysExpanded = false;
    private boolean isGrouped = false;
    private int color;
    private Runnable performer;

    private long linkedId;
    private String className;
    private String extra;
    private String title;
    private String subtitle;
    private String tag;
    private String link;
    private String linkPath;

    public TraceItemData(){
        super();
    }

    public TraceItemData(Sourcetrace sourcetrace) {
        this();
        this.title = sourcetrace.formatClassAndMethod();
        this.subtitle = sourcetrace.getPackageName();
        this.link = sourcetrace.formatFileAndLine();

        this.linkedId = sourcetrace.getUid();
        this.className = sourcetrace.getClassName();
        this.extra = sourcetrace.getExtra();
        this.performer = new Runnable(){
            @Override
            public void run() {
                OverlayService.performNavigation(SourceDetailScreen.class,
                        SourceDetailScreen.buildTraceParams(linkedId));
            }
        };
    }

    public long getLinkedId() {
        return linkedId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }



    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isAlwaysExpanded() {
        return alwaysExpanded;
    }

    public void setAlwaysExpanded(boolean alwaysExpanded) {
        this.alwaysExpanded = alwaysExpanded;
    }

    public boolean isGrouped() {
        return isGrouped;
    }

    public void setGrouped(boolean grouped) {
        isGrouped = grouped;
    }

    public boolean isOpenable() {
        return !TextUtils.isEmpty(linkPath);
    }

    public void setPerformer(Runnable performer) {
        this.performer = performer;
    }

    public void setLinkedId(long linkedId) {
        this.linkedId = linkedId;
    }

    public Runnable getPerformer() {
        return performer;
    }

    public String getLinkPath() {
        return linkPath;
    }

    public void setLinkPath(String linkPath) {
        this.linkPath = linkPath;
    }
}
