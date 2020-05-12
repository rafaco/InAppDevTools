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

package es.rafaco.inappdevtools.library.view.components.cards;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.MarginUtils;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class CardHeaderFlexViewHolder extends FlexViewHolder {

    protected final RelativeLayout headerArea;
    protected final TextView iconView;
    protected final TextView titleView;
    protected final TextView overviewView;
    protected final ImageView navIcon;

    public CardHeaderFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.headerArea = view.findViewById(R.id.header_area);
        this.iconView = view.findViewById(R.id.icon);
        this.titleView = view.findViewById(R.id.title);
        this.overviewView = view.findViewById(R.id.overview);
        this.navIcon = view.findViewById(R.id.nav_icon);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final CardHeaderFlexData data = (CardHeaderFlexData) abstractData;
        if (data!=null){
            MarginUtils.setHorizontalMargin(itemView);

            bindHeader(data);
            bindExpandedState(data);
        }
    }

    private void bindHeader(CardHeaderFlexData data) {
        int icon = data.getIcon();
        if (icon>0){
            IconUtils.set(iconView, icon);
            iconView.setVisibility(View.VISIBLE);
        }else{
            iconView.setVisibility(View.GONE);
        }

        titleView.setText(data.getTitle());
        overviewView.setText(data.getOverview());

        UiUtils.setBackground(navIcon, null);
        navIcon.setVisibility(View.VISIBLE);
    }

    private void bindExpandedState(CardHeaderFlexData data) {
        if (data.isExpandable()) {
            applyExpandedState(data.isExpanded());
        }
    }

    private void applyExpandedState(Boolean isExpanded) {
        if (!isExpanded){
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_down_white_24dp, R.color.iadt_primary);
        }
        else {
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_up_white_24dp, R.color.iadt_primary);
        }
    }
}
