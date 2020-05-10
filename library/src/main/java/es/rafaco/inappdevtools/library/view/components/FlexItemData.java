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

package es.rafaco.inappdevtools.library.view.components;

import java.util.ArrayList;
import java.util.List;

public class FlexItemData {

    String id;
    List<FlexItemData> children;

    //TODO: remove??
    Class<? extends FlexibleViewHolder> viewHolderClass;
    int layoutResourceId;

    boolean fullSpamOnParent = false;
    boolean horizontalMargin = false;
    boolean verticalMargin = false;

    public FlexItemData(String id){
        this(id, new ArrayList<FlexItemData>());
    }

    public FlexItemData(String id, List<FlexItemData> children) {
        this.id = id;
        this.children = children;
        viewHolderClass = null;
        layoutResourceId = -1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FlexItemData> getChildren() {
        return children;
    }

    public void setChildren(List<FlexItemData> children) {
        this.children = children;
    }

    public Class<? extends FlexibleViewHolder> getViewHolderClass() {
        return viewHolderClass;
    }

    public void setViewHolderClass(Class<? extends FlexibleViewHolder> viewHolderClass) {
        this.viewHolderClass = viewHolderClass;
    }

    public int getLayoutResourceId() {
        return layoutResourceId;
    }

    public void setLayoutResourceId(int layoutResourceId) {
        this.layoutResourceId = layoutResourceId;
    }

    public boolean isFullSpamOnParent() {
        return fullSpamOnParent;
    }

    public void setFullSpamOnParent(boolean fullSpamOnParent) {
        this.fullSpamOnParent = fullSpamOnParent;
    }

    public boolean isHorizontalMargin() {
        return horizontalMargin;
    }

    public void setHorizontalMargin(boolean horizontalMargin) {
        this.horizontalMargin = horizontalMargin;
    }

    public boolean isVerticalMargin() {
        return verticalMargin;
    }

    public void setVerticalMargin(boolean verticalMargin) {
        this.verticalMargin = verticalMargin;
    }
}
