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

package org.inappdevtools.library.view.components.items;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import org.inappdevtools.library.R;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.base.FlexItemViewHolder;
import org.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.AppCompatImageButton;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
//#endif

public class ButtonIconFlexViewHolder extends FlexItemViewHolder {

    AppCompatImageButton button;

    public ButtonIconFlexViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        button = view.findViewById(R.id.image_button);
    }

    @Override
    public void bindTo(Object abstractData, int position) {
        super.bindTo(abstractData, position);

        final ButtonIconFlexData data = (ButtonIconFlexData) abstractData;
        boolean isCustomBackground = data.getColor() > 0;
        int color = isCustomBackground ? data.getColor() : R.color.iadt_surface_top;
        int accentColor = ContextCompat.getColor(getContext(), R.color.iadt_primary);

        int backgroundColor = ContextCompat.getColor(getContext(), color);
        button.getBackground().setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);

        //if (data.isWrapContent())

        if (data.getIcon()>0){
            Drawable icon = getContext().getResources().getDrawable(data.getIcon());
            icon.setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
            button.setImageDrawable(icon);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.run();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setElevation(UiUtils.getPixelsFromDp(getContext(), 6));
        }
    }
}
