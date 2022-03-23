/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.base.FlexItemViewHolder;
import org.inappdevtools.library.view.icons.IconUtils;

public class OverviewViewHolder extends FlexItemViewHolder {

    private RelativeLayout overviewView;
    private final LinearLayout overviewIconButtonView;
    private TextView overviewIconView;
    private final TextView overviewIconTextView;
    private TextView overviewTitleView;
    private TextView overviewContentView;

    public OverviewViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        overviewView = view.findViewById(R.id.overview);
        overviewIconButtonView = view.findViewById(R.id.icon_button);
        overviewIconView = view.findViewById(R.id.icon);
        overviewIconTextView = view.findViewById(R.id.icon_text);
        overviewTitleView = view.findViewById(R.id.overview_title);
        overviewContentView = view.findViewById(R.id.overview_content);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);
        
        final OverviewData data = (OverviewData) abstractData;
        if (data!=null){
            itemView.setActivated(true);

            bindContent(data);
            bindIcon(data);
            bindPerformer(data);
            bindFontColor(data);
        }
    }

    private void bindContent(OverviewData data) {
        overviewTitleView.setText(data.getTitle());
        overviewContentView.setText(data.getContent());
    }

    private void bindIcon(OverviewData data) {
        if (data.getIcon()>0){
            IconUtils.set(overviewIconView, data.getIcon());
            overviewIconView.setVisibility(View.VISIBLE);
        }else{
            overviewIconView.setVisibility(View.GONE);
        }
    }

    private void bindPerformer(final OverviewData data) {
        if (data.getPerformerText()!=null){
            overviewIconTextView.setText(data.getPerformerText());
        }
        else{
            overviewIconTextView.setVisibility(View.GONE);
        }

        if (data.getPerformer()!=null){
            overviewIconButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.getPerformer().run();
                }
            });
        }
        else{
            overviewIconButtonView.setClickable(false);
        }
    }

    private void bindFontColor(OverviewData data) {
        if (data.getColor()>0){
            int contextualColor = ContextCompat.getColor(getContext(), data.getColor());
            overviewTitleView.setTextColor(contextualColor);
            overviewIconView.setTextColor(contextualColor);
        }
    }
}
