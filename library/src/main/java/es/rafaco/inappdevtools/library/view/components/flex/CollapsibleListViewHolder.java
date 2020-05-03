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

package es.rafaco.inappdevtools.library.view.components.flex;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class CollapsibleListViewHolder extends ListViewHolder {

    private final RelativeLayout headerContainer;
    private final TextView overviewView;
    private final ImageView navIcon;
    private final LinearLayout collapsibleContainer;
    private final View contentSeparator;
    private final TextView contentView;

    public CollapsibleListViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.headerContainer = view.findViewById(R.id.header_container);
        this.overviewView = view.findViewById(R.id.overview);
        this.navIcon = view.findViewById(R.id.nav_icon);
        this.collapsibleContainer = view.findViewById(R.id.collapsible_container);
        this.contentSeparator = view.findViewById(R.id.content_separator);
        this.contentView = view.findViewById(R.id.content);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);
        final CollapsibleListData data = (CollapsibleListData) abstractData;

        itemView.setClickable(true);
        itemView.setActivated(true);

        overviewView.setText(data.getOverview());

        UiUtils.setBackground(navIcon, null);
        navIcon.setVisibility(View.VISIBLE);

        bindExpandedState(data);
    }

    private void bindExpandedState(final CollapsibleListData data) {
        contentSeparator.setVisibility(View.VISIBLE);
        applyExpandedState(data.isExpanded());
        headerContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newState = !data.isExpanded();
                    data.setExpanded(newState);
                    applyExpandedState(newState);
                }
            });
    }

    private void applyExpandedState(Boolean isExpanded) {
        if (!isExpanded){
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_down_white_24dp, R.color.iadt_primary);
            collapsibleContainer.setVisibility(View.GONE);
        }
        else {
            IconUtils.applyToImageView(navIcon, R.drawable.ic_arrow_up_white_24dp, R.color.iadt_primary);
            collapsibleContainer.setVisibility(View.VISIBLE);
        }
    }
}
