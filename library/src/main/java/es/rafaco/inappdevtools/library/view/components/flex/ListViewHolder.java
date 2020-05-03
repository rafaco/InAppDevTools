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

import es.rafaco.inappdevtools.library.R;

public class ListViewHolder extends FlexibleViewHolder {

    private final TextView groupTitle;
    LinearLayout groupContainer;

    public ListViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        groupTitle = view.findViewById(R.id.group_title);
        groupContainer = view.findViewById(R.id.group_container);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final ListData data = (ListData) abstractData;
        if (data==null){
            groupTitle.setVisibility(View.GONE);
            groupContainer.setVisibility(View.GONE);
            return;
        }

        bindOrientation(data);
        bindDividers(data);
        bindTitle(data);
        bindItems(data);
    }

    private void bindOrientation(ListData data) {
        //TODO
        groupContainer.setOrientation(LinearLayout.VERTICAL);
    }

    private void bindDividers(ListData data) {
        if (data.isShowDividers()) {
            groupContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        }
        else{
            groupContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        }
    }

    private void bindTitle(ListData data) {
        boolean showTitle = !TextUtils.isEmpty(data.getTitle());
        if (showTitle) {
            groupTitle.setText(data.getTitle());
        }
        groupTitle.setVisibility(showTitle ? View.VISIBLE : View.GONE);
    }

    private void bindItems(ListData data) {
        if (data.getItems() == null && data.getItems().isEmpty()) {
            return;
        }

        for (int i = 0; i<data.getItems().size(); i++){
            Object currentItem = data.getItems().get(i);
            FlexibleItemDescriptor desc = FlexibleLoader.getDescriptor(currentItem.getClass());
            if (desc != null){
                desc.addToView(currentItem, groupContainer);
            }
        }
    }
}
