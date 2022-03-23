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

package org.inappdevtools.library.view.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

public class MarginUtils {

    private MarginUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void addAllMargins(View view) {
        setHorizontalMargin(view);
        setVerticalMargin(view);
    }

    public static void removeAllMargins(View view) {
        setMargins(view, 0, 0, 0, 0);
    }

    public static void setHorizontalMargin(View view, boolean enabled) {
        int value = enabled ? getHorizontalMargin() : 0;
        setHorizontalMargin(view, value);
    }

    public static void setVerticalMargin(View view, boolean enabled) {
        int value = enabled ? getVerticalMargin() : 0;
        setVerticalMargin(view, value);
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static void setHorizontalMargin(View view, int marginPx) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(marginPx, p.topMargin, marginPx, p.bottomMargin);
            view.requestLayout();
        }
    }

    public static void setVerticalMargin(View view, int marginPx) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(p.leftMargin, marginPx, p.rightMargin, marginPx);
            view.requestLayout();
        }
    }

    public static void setHorizontalMargin(View view) {
        setHorizontalMargin(view, getHorizontalMargin() );
    }

    public static void setVerticalMargin(View view) {
        setVerticalMargin(view, getVerticalMargin() );
    }

    public static void removeHorizontalMargin(View view) {
        setHorizontalMargin(view, 0);
    }

    public static void removeVerticalMargin(View view) {
        setVerticalMargin(view, 0);
    }

    public static int getHorizontalMargin() {
        Context context = IadtController.get().getContext();
        return (int) context.getResources().getDimension(R.dimen.iadt_horizontal_margin);
    }

    public static int getVerticalMargin() {
        Context context = IadtController.get().getContext();
        return (int) context.getResources().getDimension(R.dimen.iadt_vertical_margin);
    }
}
