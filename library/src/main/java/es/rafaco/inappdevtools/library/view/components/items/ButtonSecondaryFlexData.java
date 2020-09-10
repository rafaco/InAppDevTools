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

package es.rafaco.inappdevtools.library.view.components.items;

//#ifdef ANDROIDX
//@import androidx.annotation.StringRes;
//@import androidx.annotation.ColorRes;
//#else
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
//#endif

import es.rafaco.inappdevtools.library.view.components.base.FlexData;

public class ButtonSecondaryFlexData extends FlexData {

    int icon;
    String text;
    Runnable performer;

    int bgColor;
    int iconColor;
    int textColor;

    public ButtonSecondaryFlexData(String text, Runnable performer) {
        super();
        this.text = text;
        this.performer = performer;
    }

    public ButtonSecondaryFlexData(String text, int icon, Runnable performer) {
        this(text, performer);
        this.icon = icon;
    }

    public ButtonSecondaryFlexData(String text, @StringRes int icon, @ColorRes int colorRes, Runnable performer){
        this(text, icon, performer);
        this.textColor = colorRes;
        this.iconColor = colorRes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        return bgColor;
    }

    public void setBgColor(int bg_color) {
        this.bgColor = bg_color;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextColor() {
        return textColor;
    }


    public void run() {
        getPerformer().run();
    }
}
