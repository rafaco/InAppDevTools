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

package es.rafaco.inappdevtools.library.logic.runnables;

public class RunButton {
    String title;
    Runnable performer;
    Runnable callback;
    int icon;
    int color;
    boolean wrapContent = false;

    public RunButton(String title, Runnable performer) {
        this.title = title;
        this.performer = performer;
    }

    public RunButton(String title, int icon, Runnable performer) {
        this(title, performer);
        this.icon = icon;
    }

    public RunButton(String title, int icon, int colorResId, Runnable performer) {
        this(title, icon, performer);
        this.color = colorResId;
    }

    public RunButton(String title, int icon, Runnable performer, Runnable callback) {
        this(title, icon, performer);
        this.callback = callback;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Runnable getPerformer() {
        return performer;
    }

    public void setPerformer(Runnable performer) {
        this.performer = performer;
    }

    public Runnable getCallback() {
        return callback;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
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

    public boolean isWrapContent() {
        return wrapContent;
    }

    public void setWrapContent(boolean wrapContent) {
        this.wrapContent = wrapContent;
    }

    public void run(){
        if (getPerformer() != null)
            getPerformer().run();
        
        if (getCallback()!= null)
            getCallback().run();
    }
}
