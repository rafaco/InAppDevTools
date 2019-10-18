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

package es.rafaco.inappdevtools.library.view.components.flex;

import android.text.TextUtils;

import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;

public class TraceItemData {

    public enum Position { START, MIDDLE, END}
    private Sourcetrace sourcetrace;
    private String exception;
    private String message;
    private Position position = Position.MIDDLE;
    private String tag;
    private boolean expanded = true;
    private boolean alwaysExpanded = false;
    private boolean isGrouped = false;
    private String fullPath;
    private int color;

    public TraceItemData(Sourcetrace sourcetrace) {
        this.sourcetrace = sourcetrace;
    }

    public Sourcetrace getSourcetrace() {
        return sourcetrace;
    }

    public void setSourcetrace(Sourcetrace stacktrace) {
        this.sourcetrace = stacktrace;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
        return !TextUtils.isEmpty(fullPath);
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
