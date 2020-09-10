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

    public enum LayoutType { FULL_BOTH, WRAP_BOTH, FULL_WIDTH, FULL_HEIGHT, SAME_WIDTH, SAME_HEIGHT }

    String id;
    Class<? extends FlexViewHolder> viewHolderClass;
    int layoutResourceId;
    Boolean fullSpan;

    LayoutType layoutType;
    int gravity;
    Boolean horizontalMargin = null;
    Boolean verticalMargin = null;
    int[] margins;
    int backgroundColor;
    float alpha;


    public FlexData() {
        this.id = null;
        this.viewHolderClass = null;
        this.layoutResourceId = -1;
        this.alpha = -1f;
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

    public LayoutType getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
    }

    /**Get gravity for this item view
     *
     * @return standard gravity int from android.view.Gravity constants
     */
    public int getGravity() {
        return gravity;
    }

    /**Set gravity for this item view
     *
     * @param gravity: standard gravity int from android.view.Gravity constants
     */
    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    /**Check if this item should use full width within its parent. Only used when parent is a
     * RecyclerView with Grid or StaggeredGrid layout.
     *
     * @return true or false if this item should use or not full width. Null for default behaviour.
     */
    public Boolean isFullSpan() {
        return fullSpan;
    }

    /**Set if this item should use full width within its parent. Only used when parent is a
     * RecyclerView with Grid or StaggeredGrid layout.
     *
     * @param fullSpan true or false to use or not full width. Null for default behaviour.
     */
    public void setFullSpan(Boolean fullSpan) {
        this.fullSpan = fullSpan;
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

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
