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

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.DividerItemDecoration;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class RecyclerListViewHolder extends FlexibleViewHolder {

    private final TextView groupTitle;
    RecyclerView groupContainer;

    public RecyclerListViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        groupTitle = view.findViewById(R.id.group_title);
        groupContainer = view.findViewById(R.id.group_container);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final RecyclerListData data = (RecyclerListData) abstractData;
        if (data==null){
            groupTitle.setVisibility(View.GONE);
            groupContainer.setVisibility(View.GONE);
            return;
        }

        bindMargins(data);
        bindTitle(data);
        bindOrientation(data);
        bindDividers(data);
        groupContainer.setHasFixedSize(false);
        bindAdapter(data);
    }

    private void bindMargins(RecyclerListData data) {
        if (data.hasHorizontalMargin){
            UiUtils.addHorizontalMargin(itemView);
        }
    }

    private void bindOrientation(RecyclerListData data) {
        int orientation = data.isHorizontal
                ? LinearLayout.HORIZONTAL
                : LinearLayout.VERTICAL;
        LinearLayoutManager lm = new LinearLayoutManager(groupContainer.getContext(),
                    orientation, false);
        groupContainer.setLayoutManager(lm);
    }

    private void bindDividers(RecyclerListData data) {
        if (!data.showDividers)
            return;

        int orientation = data.isHorizontal
                ? LinearLayout.HORIZONTAL
                : LinearLayout.VERTICAL;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(groupContainer.getContext(),
                orientation);
        groupContainer.addItemDecoration(dividerItemDecoration);
    }

    private void bindTitle(RecyclerListData data) {
        boolean showTitle = !TextUtils.isEmpty(data.getTitle());
        if (showTitle) {
            groupTitle.setText(data.getTitle());
        }
        groupTitle.setVisibility(showTitle ? View.VISIBLE : View.GONE);
    }

    private void bindAdapter(RecyclerListData data) {
        groupContainer.setAdapter(data.getAdapter());
    }
}
