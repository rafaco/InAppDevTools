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

package es.rafaco.inappdevtools.library.view.components.groups;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexibleItemDescriptor;
import es.rafaco.inappdevtools.library.view.components.FlexibleLoader;
import es.rafaco.inappdevtools.library.view.components.base.FlexGroupViewHolder;
import es.rafaco.inappdevtools.library.view.components.base.FlexItemData;

public class LinearGroupViewHolder extends FlexGroupViewHolder {

    private final TextView groupTitle;
    LinearLayout groupContainer;

    public LinearGroupViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        groupTitle = view.findViewById(R.id.group_title);
        groupContainer = view.findViewById(R.id.group_container);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);

        final LinearGroupData data = (LinearGroupData) abstractData;
        if (data==null){
            groupTitle.setVisibility(View.GONE);
            groupContainer.setVisibility(View.GONE);
            return;
        }

        //bindGravity()
        bindOrientationAndLayout(data);
        bindDividers(data);
        bindTitle(data);
        bindItems(data);
    }

    private void bindOrientationAndLayout(LinearGroupData data) {
        int orientation = data.isHorizontal ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL;
        groupContainer.setOrientation(orientation);

        //Set default childLayout for selected orientation
        if (data.getChildLayout()==null){
            FlexItemData.LayoutInParent defaultLayout = data.isHorizontal
                    ? FlexItemData.LayoutInParent.SAME_WIDTH
                    : FlexItemData.LayoutInParent.FULL_WIDTH;
            data.setChildLayout(defaultLayout);
        }
    }

    private void bindDividers(LinearGroupData data) {
        groupContainer.setShowDividers(data.isShowDividers()
                ? LinearLayout.SHOW_DIVIDER_MIDDLE
                : LinearLayout.SHOW_DIVIDER_NONE);
    }

    private void bindTitle(LinearGroupData data) {
        boolean showTitle = !TextUtils.isEmpty(data.getTitle());
        if (showTitle) {
            groupTitle.setText(data.getTitle());
        }
        groupTitle.setVisibility(showTitle ? View.VISIBLE : View.GONE);
    }

    private void bindItems(LinearGroupData data) {
        if (data.getChildren() == null || data.getChildren().isEmpty()) {
            groupContainer.removeAllViews();
            return;
        }

        for (int i = 0; i<data.getChildren().size(); i++){
            Object currentItem = data.getChildren().get(i);
            if (data.getChildLayout()!=null && currentItem instanceof FlexItemData){
                ((FlexItemData) currentItem).setLayoutInParent(data.getChildLayout());
            }

            FlexibleItemDescriptor desc = FlexibleLoader.getDescriptor(currentItem.getClass());
            if (desc != null){
                desc.addToView(currentItem, groupContainer);
            }
        }
    }
}
