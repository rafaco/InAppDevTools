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

package es.rafaco.inappdevtools.library.view.components.base;

import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;

public class FlexData {

    public enum LayoutInParent { FULL_BOTH, WRAP_BOTH, FULL_WIDTH, FULL_HEIGHT, SAME_WIDTH, SAME_HEIGHT }

    String id;
    Class<? extends FlexViewHolder> viewHolderClass;
    int layoutResourceId;

    LayoutInParent layoutInParent;
    int gravity;
    int spamOnParent;
    boolean fullSpamOnParent = false;

    Boolean horizontalMargin = null;
    Boolean verticalMargin = null;
    int[] margins;
    int backgroundColor;

    public FlexData() {
        this.id = null;
        this.viewHolderClass = null;
        this.layoutResourceId = -1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<? extends FlexViewHolder> getViewHolderClass() {
        return viewHolderClass;
    }

    public void setViewHolderClass(Class<? extends FlexViewHolder> viewHolderClass) {
        this.viewHolderClass = viewHolderClass;
    }

    public int getLayoutResourceId() {
        return layoutResourceId;
    }

    public void setLayoutResourceId(int layoutResourceId) {
        this.layoutResourceId = layoutResourceId;
    }

    public LayoutInParent getLayoutInParent() {
        return layoutInParent;
    }

    public void setLayoutInParent(LayoutInParent layoutInParent) {
        this.layoutInParent = layoutInParent;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public boolean isFullSpamOnParent() {
        return fullSpamOnParent;
    }

    public void setFullSpamOnParent(boolean fullSpamOnParent) {
        this.fullSpamOnParent = fullSpamOnParent;
    }

    public Boolean isHorizontalMargin() {
        return horizontalMargin;
    }

    public void setHorizontalMargin(boolean horizontalMargin) {
        this.horizontalMargin = horizontalMargin;
    }

    public Boolean isVerticalMargin() {
        return verticalMargin;
    }

    public void setVerticalMargin(boolean verticalMargin) {
        this.verticalMargin = verticalMargin;
    }

    public int[] getMargins() {
        return margins;
    }

    public void setMargins(int left, int top, int right, int bottom) {
        this.margins = new int[] { left, top, right, bottom };
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
