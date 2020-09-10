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
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;

public class RecyclerGroupFlexData extends GroupFlexData {

    FlexAdapter adapter;
    FlexAdapter.Layout adapterLayout;
    int adapterSpanCount;

    boolean showDividers = false;
    boolean isHorizontal = false;

    public RecyclerGroupFlexData(){
        this(FlexAdapter.Layout.LINEAR, 1, new ArrayList<Object>());
    }

    public RecyclerGroupFlexData(FlexAdapter adapter) {
        this.adapter = adapter;
    }

    public RecyclerGroupFlexData(FlexAdapter.Layout layout, int spanCount) {
        this(layout, spanCount, new ArrayList<Object>());
    }

    public RecyclerGroupFlexData(FlexAdapter.Layout layout, int spanCount, List<Object> children) {
        super(children);
        this.adapterLayout = layout;
        this.adapterSpanCount = spanCount;
    }

    public FlexAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FlexAdapter adapter) {
        this.adapter = adapter;
    }

    public FlexAdapter.Layout getAdapterLayout() {
        return adapterLayout;
    }

    public void setAdapterLayout(FlexAdapter.Layout adapterLayout) {
        this.adapterLayout = adapterLayout;
    }

    public int getAdapterSpanCount() {
        return adapterSpanCount;
    }

    public void setAdapterSpanCount(int adapterSpanCount) {
        this.adapterSpanCount = adapterSpanCount;
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
}
