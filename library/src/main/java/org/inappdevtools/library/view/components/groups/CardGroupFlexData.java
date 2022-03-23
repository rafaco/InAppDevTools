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

package org.inappdevtools.library.view.components.groups;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.view.components.base.GroupFlexData;

public class CardGroupFlexData extends GroupFlexData {

    Runnable performer;
    int bgColorResource;
    float elevationDp;
    boolean noContentPadding;
    boolean isFullWidth;

    public CardGroupFlexData() {
        this(new ArrayList<Object>(), null);
    }

    public CardGroupFlexData(List<Object> children) {
        this(children, null);
    }

    public CardGroupFlexData(List<Object> children, Runnable performer) {
        super(children);
        this.performer = performer;
        setHorizontalMargin(true);
        setVerticalMargin(true);
    }

    public Runnable getPerformer() {
        return performer;
    }

    public CardGroupFlexData setPerformer(Runnable performer) {
        this.performer = performer;
        return this;
    }

    public int getBgColorResource() {
        return bgColorResource;
    }

    public CardGroupFlexData setBgColorResource(int bg_color) {
        this.bgColorResource = bg_color;
        return this;
    }

    public float getElevationDp() {
        return elevationDp;
    }

    public CardGroupFlexData setElevationDp(float elevationDp) {
        this.elevationDp = elevationDp;
        return this;
    }

    public boolean isNoContentPadding() {
        return noContentPadding;
    }

    public void setNoContentPadding(boolean noContentPadding) {
        this.noContentPadding = noContentPadding;
    }

    public boolean isFullWidth() {
        return isFullWidth;
    }

    public void setFullWidth(boolean fullWidth) {
        isFullWidth = fullWidth;
    }

    public void run(){
        getPerformer().run();
    }
}
