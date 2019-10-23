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

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.AppCompatButton;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.ViewGroup;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;

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
        int color = (data.getColor() >  0) ? data.getColor() : R.color.iadt_surface_top;
        int contextualizedColor = ContextCompat.getColor(button.getContext(), color);
        button.getBackground().setColorFilter(contextualizedColor, PorterDuff.Mode.MULTIPLY);
        if (data.getIcon()>0){
            Drawable icon = button.getContext().getResources().getDrawable(data.getIcon());
            button.setCompoundDrawablesWithIntrinsicBounds( icon, null, null, null);
        }
        button.setText(data.getTitle());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.run();
            }
        });

        if (position == -1){
            ViewGroup.LayoutParams params = button.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            button.setLayoutParams(params);
        }
    }
}
