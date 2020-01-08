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

package es.rafaco.inappdevtools.library.view.components.flex;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;

public class OverviewViewHolder extends FlexibleViewHolder {

    private RelativeLayout overviewView;
    private TextView overviewTitleView;
    private TextView overviewIconView;
    private TextView overviewContentView;

    public OverviewViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        overviewView = view.findViewById(R.id.overview);
        overviewContentView = view.findViewById(R.id.overview_content);
        overviewIconView = view.findViewById(R.id.overview_icon);
        overviewTitleView = view.findViewById(R.id.overview_title);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final OverviewData data = (OverviewData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            overviewTitleView.setText(data.getTitle());
            overviewContentView.setText(data.getContent());

            if (data.getIcon()>0){
                IconUtils.markAsIconContainer(overviewIconView, IconUtils.MATERIAL);
                overviewIconView.setText(data.getIcon());
                overviewIconView.setVisibility(View.VISIBLE);
            }else{
                overviewIconView.setVisibility(View.GONE);
            }
        }
    }
}
