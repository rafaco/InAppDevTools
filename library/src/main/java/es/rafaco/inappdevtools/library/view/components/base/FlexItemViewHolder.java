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

import android.support.annotation.CallSuper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;
import es.rafaco.inappdevtools.library.view.utils.MarginUtils;

public class FlexItemViewHolder extends FlexViewHolder {

    public FlexItemViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
    }

    @CallSuper
    public void bindTo(Object abstractData, int position) {

        //TODO: remove this bug out, currently needed for String headers
        if (!(abstractData instanceof FlexData)) {
            return;
        }
        FlexData data = (FlexData) abstractData;

        bindLayout(data);
        bindGravity(data);
        bindMargins(data);
        bindBackgroundColor(data);
        bindAlpha(data);
    }

    private void bindAlpha(FlexData data) {
        if (data.getAlpha() > 0f && data.getAlpha() < 1f){
            itemView.setAlpha(data.getAlpha());
        }
    }

    private void bindLayout(FlexData data) {
        if (data.getLayoutType() == null)
            return;

        LinearLayout.LayoutParams layoutParams;
        switch (data.getLayoutType()) {
            case FULL_BOTH:
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                break;
            case WRAP_BOTH:
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
            case SAME_WIDTH:
                layoutParams = new LinearLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                break;
            case SAME_HEIGHT:
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        0, 1);
                break;
            case FULL_HEIGHT:
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                break;
            case FULL_WIDTH:
            default:
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
        }
        itemView.setLayoutParams(layoutParams);
    }

    private void bindGravity(FlexData data) {
        if (data.getGravity()>0){  //ViewGroup.MarginLayoutParams  RecyclerView.LayoutParams
            if (itemView.getLayoutParams() instanceof LinearLayout.LayoutParams){
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                layoutParams.gravity = data.getGravity();
                itemView.setLayoutParams(layoutParams);
            }
        }
    }

    protected void bindMargins(FlexData data) {
        if (data.margins!=null && data.margins.length == 4){
            MarginUtils.setMargins(itemView,
                    data.margins[0], data.margins[1], data.margins[2], data.margins[3]);
        }

        if (data.isHorizontalMargin()!=null){
            MarginUtils.setHorizontalMargin(itemView, data.isHorizontalMargin());
        }

        if (data.isVerticalMargin()!=null){
            MarginUtils.setVerticalMargin(itemView, data.isVerticalMargin());
        }
    }

    private void bindBackgroundColor(FlexData data) {
        if (data.getBackgroundColor()>0) {
            int contextualizedColor = ContextCompat.getColor(getContext(), data.getBackgroundColor());
            itemView.setBackgroundColor(contextualizedColor);
        }
    }
}
