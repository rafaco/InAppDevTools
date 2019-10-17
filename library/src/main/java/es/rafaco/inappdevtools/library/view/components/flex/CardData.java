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

public class CardData {
    String title;
    String content;
    Runnable performer;
    int icon;
    int bg_color;

    public CardData(String title, Runnable performer) {
        this.title = title;
        this.performer = performer;
    }

    public CardData(String title, int icon, Runnable performer) {
        this(title, performer);
        this.icon = icon;
    }

    public CardData(String title, String content, int icon, Runnable performer) {
        this(title, icon, performer);
        this.content = content;
    }

    public CardData(String title, int icon, int bgColorResId, Runnable performer) {
        this(title, icon, performer);
        this.bg_color = bgColorResId;
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

    public int getBgColor() {
        return bg_color;
    }

    public void setBgColor(int bg_color) {
        this.bg_color = bg_color;
    }

    public void run(){
        getPerformer().run();
    }
}
