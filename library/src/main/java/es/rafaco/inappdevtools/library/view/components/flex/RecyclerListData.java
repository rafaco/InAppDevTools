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

package es.rafaco.inappdevtools.library.view.components.flex;

public class RecyclerListData {

    String title;
    FlexibleAdapter adapter;
    boolean showDividers = false;
    boolean isHorizontal = false;
    boolean hasHorizontalMargin = false;

    public RecyclerListData(FlexibleAdapter adapter) {
        this.adapter = adapter;
    }

    public RecyclerListData(String title, FlexibleAdapter adapter) {
        this(adapter);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FlexibleAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FlexibleAdapter adapter) {
        this.adapter = adapter;
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

    public boolean isHasHorizontalMargin() {
        return hasHorizontalMargin;
    }

    public void setHasHorizontalMargin(boolean hasHorizontalMargin) {
        this.hasHorizontalMargin = hasHorizontalMargin;
    }
}
