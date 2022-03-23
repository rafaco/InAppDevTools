/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.components.items;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.appcompat.widget.AppCompatTextView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
//#endif

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.FlexViewHolder;
import org.inappdevtools.library.view.icons.IconUtils;

public class LinkViewHolder extends FlexViewHolder {

    private final LinearLayout wrapper;
    AppCompatTextView icon;
    AppCompatTextView title;
    AppCompatTextView overview;

    public LinkViewHolder(View view) {
        this(view, null);
    }

    public LinkViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        wrapper = view.findViewById(R.id.link_wrapper);
        icon = view.findViewById(R.id.icon);
        title = view.findViewById(R.id.title);
        overview = view.findViewById(R.id.overview);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final LinkItemData data = (LinkItemData) abstractData;
        if (data.getIcon()>0){
            IconUtils.set(icon, data.getIcon());
            if (data.getColor()>0){
                int contextualizedColor = ContextCompat.getColor(getContext(), data.getColor());
                icon.setTextColor(contextualizedColor);
            }
            icon.setVisibility(View.VISIBLE);
        }
        else{
            icon.setVisibility(View.GONE);
        }

        title.setText(data.getTitle());

        if ( TextUtils.isEmpty(data.overview)){
            overview.setVisibility(View.GONE);
        }
        else{
            overview.setText(data.overview);
            if (data.getColor()>0) {
                int contextualizedColor = ContextCompat.getColor(getContext(), data.getColor());
                overview.setTextColor(contextualizedColor);
            }
            overview.setVisibility(View.VISIBLE);
        }

        wrapper.setClickable(true);
        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.onClick();
            }
        });
    }
}
