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

package org.inappdevtools.library.view.components.items;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.base.FlexItemViewHolder;

public class TextFlexViewHolder extends FlexItemViewHolder {

    TextView textView;

    public TextFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.textView = view.findViewById(R.id.title);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);

        TextFlexData data;
        if (abstractData instanceof String) {
            data = new TextFlexData((String) abstractData);
        }
        else if (abstractData instanceof TextFlexData) {
            data = (TextFlexData) abstractData;
        }
        else
            return;

        textView.setVisibility(TextUtils.isEmpty(data.getText()) ? View.GONE : View.VISIBLE);

        if (data.getText() != null)
            textView.setText(data.getText());

        if (data.getFontColor()>0) {
            int contextualizedColor = ContextCompat.getColor(getContext(), data.getFontColor());
            textView.setTextColor(contextualizedColor);
        }

        Resources resources = getContext().getResources();
        int sizeDimens;
        switch (data.getSize()){
            case SMALL:
                sizeDimens = R.dimen.iadt_text_size_s;
                break;
            case LARGE:
                sizeDimens = R.dimen.iadt_text_size_l;
                break;
            case EXTRA_LARGE:
                sizeDimens = R.dimen.iadt_text_size_xl;
                break;
            case MEDIUM:
            default:
                sizeDimens = R.dimen.iadt_text_size_m;
                break;
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(sizeDimens));

        if (data.getGravity() == Gravity.RIGHT){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
        }
        else if (data.getGravity() == Gravity.CENTER){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }

        if (data.getBold()!=null) {
            if (data.getBold() == true) {
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            } else if (data.getBold() == false) {
                textView.setTypeface(Typeface.create(textView.getTypeface(), Typeface.NORMAL));
            }
        }
    }
}
