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

package es.rafaco.inappdevtools.library.view.icons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
//#endif

import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class IconUtils {

    public static final String ROOT = "fonts/";
    public static final String MATERIAL = ROOT + "MaterialIcons-Regular.ttf";
    //public static final String FONTAWESOME = ROOT + "fa-solid-900.ttf";

    //Disabled icons at TextViews. A workaround to allow Layout Inspection with AndroidStudio
    private static final boolean DISABLED = false;
    private static final String DISABLED_REPLACEMENT = "@";


    public static Typeface getTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), MATERIAL);
    }

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static Typeface getTypeface(Resources resources, String name) {
        return Typeface.createFromAsset(resources.getAssets(), name);
    }

    public static void markAsIconContainer(View v) {
        markAsIconContainer(v, getTypeface(v.getContext()));
    }

    public static void markAsIconContainer(View v, String font) {
        markAsIconContainer(v, getTypeface(v.getContext(), font));
    }

    public static void markAsIconContainer(View v, Typeface typeface) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                markAsIconContainer(child, typeface);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        }
    }

    public static void set(TextView textView, int stringIcon){
        if (DISABLED){
            textView.setText(DISABLED_REPLACEMENT);
            return;
        }
        markAsIconContainer(textView, IconUtils.MATERIAL);
        textView.setText(stringIcon);
    }

    public static void applyToImageView(ImageView view, int icon, int color) {
        view.setImageDrawable(UiUtils.getDrawable(icon));
        if (color>0){
            int contextualizedColor = ContextCompat.getColor(view.getContext(), color);
            view.setColorFilter(contextualizedColor);
        }
    }
}
