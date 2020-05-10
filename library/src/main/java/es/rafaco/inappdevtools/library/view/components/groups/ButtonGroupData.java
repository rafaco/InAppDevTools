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

package es.rafaco.inappdevtools.library.view.components.groups;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.logic.runnables.RunButton;

public class ButtonGroupData {

    String title;
    List<RunButton> buttons;
    boolean wrapContent = false;
    boolean borderless = false;

    public ButtonGroupData(RunButton button) {
        ArrayList<RunButton> buttons = new ArrayList<>();
        buttons.add(button);
        this.buttons = buttons;
    }

    public ButtonGroupData(List<RunButton> buttons) {
        this.buttons = buttons;
    }

    public ButtonGroupData(String title, List<RunButton> buttons) {
        this(buttons);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<RunButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<RunButton> buttons) {
        this.buttons = buttons;
    }

    public boolean isWrapContent() {
        return wrapContent;
    }

    public void setWrapContent(boolean wrapContent) {
        this.wrapContent = wrapContent;
    }

    public boolean isBorderless() {
        return borderless;
    }

    public void setBorderless(boolean borderless) {
        this.borderless = borderless;
    }
}
