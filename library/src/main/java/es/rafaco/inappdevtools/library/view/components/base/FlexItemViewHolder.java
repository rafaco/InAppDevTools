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

import android.support.v4.content.ContextCompat;
import android.view.View;

import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexibleViewHolder;
import es.rafaco.inappdevtools.library.view.utils.MarginUtils;

public class FlexItemViewHolder extends FlexibleViewHolder {

    public FlexItemViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
    }

    @Override
    public void bindTo(Object abstractData, int position) {

        //TODO: remove this bug out, currently needed for String headers
        if (!(abstractData instanceof FlexItemData)) {
            return;
        }
        FlexItemData data = (FlexItemData) abstractData;

        if (data.isHorizontalMargin()!=null){
            MarginUtils.setHorizontalMargin(itemView, data.isHorizontalMargin());
        }

        if (data.isVerticalMargin()!=null){
            MarginUtils.setVerticalMargin(itemView, data.isVerticalMargin());
        }

        if (data.getBackgroundColor()>0) {
            int contextualizedColor = ContextCompat.getColor(itemView.getContext(), data.getBackgroundColor());
            itemView.setBackgroundColor(contextualizedColor);
        }
    }
}
