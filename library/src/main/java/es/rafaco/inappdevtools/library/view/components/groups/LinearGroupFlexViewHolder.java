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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexDescriptor;
import es.rafaco.inappdevtools.library.view.components.FlexLoader;
import es.rafaco.inappdevtools.library.view.components.base.GroupFlexViewHolder;
import es.rafaco.inappdevtools.library.view.components.base.FlexData;

public class LinearGroupFlexViewHolder extends GroupFlexViewHolder {

    private final TextView groupTitle;
    LinearLayout groupContainer;

    public LinearGroupFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        groupTitle = view.findViewById(R.id.group_title);
        groupContainer = view.findViewById(R.id.group_container);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);

        final LinearGroupFlexData data = (LinearGroupFlexData) abstractData;
        if (data==null){
            groupTitle.setVisibility(View.GONE);
            groupContainer.setVisibility(View.GONE);
            return;
        }

        //bindGravity()
        bindOrientationAndLayout(data);
        bindDividers(data);
        bindTitle(data);
    }

    private void bindOrientationAndLayout(LinearGroupFlexData data) {
        int orientation = data.isHorizontal ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL;
        groupContainer.setOrientation(orientation);

        //Set default childLayout for selected orientation
        if (data.getChildLayout()==null){
            FlexData.LayoutType defaultLayout = data.isHorizontal
                    ? FlexData.LayoutType.SAME_WIDTH
                    : FlexData.LayoutType.FULL_WIDTH;
            data.setChildLayout(defaultLayout);
        }
    }

    private void bindDividers(LinearGroupFlexData data) {
        groupContainer.setShowDividers(data.isShowDividers()
                ? LinearLayout.SHOW_DIVIDER_MIDDLE
                : LinearLayout.SHOW_DIVIDER_NONE);
    }

    private void bindTitle(LinearGroupFlexData data) {
        boolean showTitle = !TextUtils.isEmpty(data.getTitle());
        if (showTitle) {
            groupTitle.setText(data.getTitle());
        }
        groupTitle.setVisibility(showTitle ? View.VISIBLE : View.GONE);
    }

    @Override
    public ViewGroup getChildrenContainer() {
        return groupContainer;
    }

    @Override
    protected void beforeAddChildView(FlexDescriptor desc, Object parentData, Object childData) {
        LinearGroupFlexData parent = (LinearGroupFlexData) parentData;
        if (parent.getChildLayout()!=null && childData instanceof FlexData){
            ((FlexData) childData).setLayoutType(parent.getChildLayout());
        }
    }
}
