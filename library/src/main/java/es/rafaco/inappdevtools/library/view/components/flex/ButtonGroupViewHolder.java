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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter.FlexibleItemDescriptor;

public class ButtonGroupViewHolder extends FlexibleViewHolder {

    private final TextView groupTitle;
    LinearLayout groupContainer;
    private ViewGroup viewGroup;

    public ButtonGroupViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        groupTitle = view.findViewById(R.id.group_title);
        groupContainer = view.findViewById(R.id.group_container);
    }

    @Override
    public void onCreate(ViewGroup viewGroup, int viewType) {
        this.viewGroup = viewGroup;
        super.onCreate(viewGroup, viewType);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final ButtonGroupData data = (ButtonGroupData) abstractData;
        if (data==null) return;

        boolean showTitle = !TextUtils.isEmpty(data.getTitle());
        if (showTitle) {
            groupTitle.setText(data.getTitle());
        }
        groupTitle.setVisibility(showTitle ? View.VISIBLE : View.GONE);

        if (data.getButtons() != null && data.getButtons().size()>0){
            for (int i=0; i<data.getButtons().size(); i++){
                RunButton buttonData = data.getButtons().get(i);
                FlexibleItemDescriptor desc = new FlexibleItemDescriptor(RunButton.class, RunButtonViewHolder.class, R.layout.flexible_item_run_button);

                View buttonView = LayoutInflater.from(groupContainer.getContext()).inflate(desc.layoutResourceId, groupContainer, false);
                FlexibleViewHolder buttonHolder = new RunButtonViewHolder(buttonView);
                buttonHolder.onCreate(viewGroup, -1);
                buttonHolder.bindTo(buttonData, -1);
                groupContainer.addView(buttonView);
            }
        }
    }
}
