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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;

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

        if (data.isWrapContent()){
            //Current implementation only propagate to child buttons
            //groupContainer.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        if (data.getButtons() != null && data.getButtons().size()>0){
            for (int i=0; i<data.getButtons().size(); i++){
                RunButton buttonData = data.getButtons().get(i);
                if (data.isWrapContent()){
                    buttonData.setWrapContent(true);
                }
                int style = data.isBorderless() ?
                        R.layout.flexible_item_button_group_item_borderless :
                        R.layout.flexible_item_button_group_item;
                FlexibleItemDescriptor desc = new FlexibleItemDescriptor(RunButton.class,
                        RunButtonViewHolder.class, style);
                desc.addToView(buttonData, groupContainer);
            }
        }
    }
}
