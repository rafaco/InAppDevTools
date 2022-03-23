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

import android.text.TextUtils;
import android.view.View;
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

public class ButtonSecondaryFlexViewHolder extends FlexItemViewHolder {

    RelativeLayout headerArea;
    TextView icon;
    TextView title;

    public ButtonSecondaryFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        headerArea = view.findViewById(R.id.header_area);
        icon = view.findViewById(R.id.icon);
        title = view.findViewById(R.id.title);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);

        final ButtonSecondaryFlexData data = (ButtonSecondaryFlexData) abstractData;

        if (data.getBgColor() > 0){
            int contextualizedColor = ContextCompat.getColor(title.getContext(), data.getBgColor());
            headerArea.setBackgroundColor(contextualizedColor);
        }

        if (data.getIcon() >0){
            icon.setVisibility(View.VISIBLE);
            IconUtils.applyToTextView(icon, data.getIcon(), data.getIconColor());
        }else{
            icon.setVisibility(View.GONE);
        }

        if (data.getTextColor() > 0){
            int contextualizedColor = ContextCompat.getColor(title.getContext(), data.getTextColor());
            title.setTextColor(contextualizedColor);
        }
        title.setText(data.getText());
        title.setVisibility(TextUtils.isEmpty(data.getText()) ? View.GONE : View.VISIBLE);

        if (data.getPerformer() != null){
            headerArea.setClickable(true);
            headerArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.getPerformer().run();
                }
            });
        }
        else{
            headerArea.setClickable(false);
            headerArea.setFocusable(false);
            headerArea.setFocusableInTouchMode(false);
        }
    }
}
