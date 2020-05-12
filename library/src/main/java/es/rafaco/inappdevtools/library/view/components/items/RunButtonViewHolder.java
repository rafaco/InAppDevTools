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

package es.rafaco.inappdevtools.library.view.components.items;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.AppCompatButton;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexibleViewHolder;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class RunButtonViewHolder extends FlexibleViewHolder {

    AppCompatButton button;

    public RunButtonViewHolder(View view) {
        super(view);
        button = view.findViewById(R.id.button);
    }

    public RunButtonViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        button = view.findViewById(R.id.button);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        final RunButton data = (RunButton) abstractData;
        boolean isCustomBackground = data.getColor() > 0;
        int color = isCustomBackground ? data.getColor() : R.color.iadt_surface_top;
        int accentColor = ContextCompat.getColor(button.getContext(), R.color.iadt_primary);

        int backgroundColor = ContextCompat.getColor(button.getContext(), color);
        button.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);


        if (data.isWrapContent()){
            //Change width to WRAP_CONTENT and remove WEIGHT
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setAllCaps(false);
            button.setTextColor(accentColor);
        }

        if (data.getIcon()>0){
            Drawable icon = button.getContext().getResources().getDrawable(data.getIcon());
            if (!isCustomBackground || data.isWrapContent())
                icon.setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
            button.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null);
            button.setCompoundDrawablePadding((int) UiUtils.dpToPx(button.getContext(), 5));
        }

        button.setText(data.getTitle());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.run();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 6));
        }
    }
}
