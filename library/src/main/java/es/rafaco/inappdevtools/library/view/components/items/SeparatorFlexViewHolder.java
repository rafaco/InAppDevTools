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

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.base.FlexItemViewHolder;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class SeparatorFlexViewHolder extends FlexItemViewHolder {

    LinearLayout groupContainer;

    public SeparatorFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);
        final SeparatorFlexData data = (SeparatorFlexData) abstractData;

        float strokePx = calculateThickness(data);
        bindOrientation(data, (int) strokePx);
        bindMargins(data);
        bindDefaultColor(data);
    }

    private float calculateThickness(SeparatorFlexData data) {
        float valueDp = (data.getThicknessDp()>0) ? data.getThicknessDp() : 1;
        return UiUtils.dpToPx(getContext(), valueDp);
    }

    private void bindOrientation(SeparatorFlexData data, int strokePx) {
        LinearLayout.LayoutParams layoutParams;
        if (data.isHorizontal()){
            layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    strokePx);
        }
        else{
            layoutParams = new LinearLayout.LayoutParams(
                    strokePx,
                    LinearLayout.LayoutParams.MATCH_PARENT);
        }
        itemView.setLayoutParams(layoutParams);
    }

    private void bindDefaultColor(SeparatorFlexData data) {
        if (data.getBackgroundColor()<1){
            int contextualizedColor = ContextCompat.getColor(getContext(), R.color.iadt_text_low);
            itemView.setBackgroundColor(contextualizedColor);
        }
    }
}
