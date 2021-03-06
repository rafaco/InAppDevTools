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

import es.rafaco.inappdevtools.library.view.components.base.GroupFlexData;

public class LinearGroupFlexData extends GroupFlexData {

    String title;
    boolean showDividers = false;
    boolean isHorizontal = false;
    LayoutType childLayout;

    public LinearGroupFlexData() {
        this(new ArrayList<>());
    }

    public LinearGroupFlexData(List<Object> children) {
        this(null, children);
    }

    public LinearGroupFlexData(String title, List<Object> children) {
        super(children);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShowDividers() {
        return showDividers;
    }

    public void setShowDividers(boolean showDividers) {
        this.showDividers = showDividers;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public LayoutType getChildLayout() {
        return childLayout;
    }

    public void setChildLayout(LayoutType childLayout) {
        this.childLayout = childLayout;
    }
}
