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

package es.rafaco.inappdevtools.library.view.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.cardview.widget.CardView;
//@import androidx.core.content.ContextCompat;
//@import androidx.annotation.RequiresApi;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.annotation.RequiresApi;
//#endif

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;

public class UiUtils {

    private UiUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void setAppIconAsBackground(ImageView imageView){
        Drawable d = getAppIconDrawable();
        imageView.setImageDrawable(d);
    }

    public static Drawable getAppIconDrawable() {
        return getContext().getPackageManager().getApplicationIcon(getContext().getApplicationInfo());
    }

    public static int getAppIconResourceId(){
        ApplicationInfo applicationInfo = getContext().getApplicationInfo();
        return applicationInfo.icon;
    }

    public static void setBackground(View view, Drawable background) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundLegacy(view, background);
        } else {
            setBackgroundModern(view, background);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private static void setBackgroundModern(View view, Drawable background) {
        view.setBackground(background);
    }

    @SuppressWarnings("deprecation")
    private static void setBackgroundLegacy(View view, Drawable background) {
        view.setBackgroundDrawable(background);
    }

    public static void setStrokeToDrawable(Context context, int i, int color, Drawable background) {
        GradientDrawable drawable = (GradientDrawable)background;
        drawable.setStroke((int)getPixelsFromDp(context, (float)i), ContextCompat.getColor(context, color));
    }

    private void showKeyboard(View target){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(target.getContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard(View target){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(target.getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(target.getWindowToken(),0);
    }

    public static void closeAllSystemWindows(Context context){
        //TODO: will it close all my widgets ?
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public static Point getDisplaySize(Context context) {
        Point sizes = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
            windowManager.getDefaultDisplay().getSize(sizes);
        else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            sizes.set(w, h);
        }
        return sizes;
    }

    public static int getStatusBarHeight(Context context) {
        return (int) Math.ceil(25 * context.getApplicationContext().getResources().getDisplayMetrics().density);
    }

    public static float getPixelsFromDp(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    public static void setBackgroundColorToDrawable(Context context, int colorRes, Drawable drawable) {
        if (drawable instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, colorRes));
        }
        else if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(ContextCompat.getColor(context, colorRes));
        }
        else if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(ContextCompat.getColor(context, colorRes));
        }
    }

    public static void setCardViewClickable(CardView cardView, boolean isClickable){
        Context context = cardView.getContext();
        Drawable drawable;
        if(isClickable) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(
                    android.R.attr.selectableItemBackground, outValue, true);

            drawable = getDrawable(outValue.resourceId);
        }
        else{
            drawable = null;
        }
        cardView.setClickable(isClickable);
        cardView.setFocusable(isClickable);
        cardView.setActivated(isClickable);
        cardView.setForeground(drawable);
    }

    public static void highlightString(Context context, CharSequence text, String keyword, TextView textView) {

        SpannableString spannableString = new SpannableString(text);

        /* Remove previous spans
        BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span: backgroundSpans) {
            spannableString.removeSpan(span);
        }*/

        int indexOfKeyword = spannableString.toString().indexOf(keyword);
        while (indexOfKeyword > 0) {
            int color = ContextCompat.getColor(context, R.color.rally_blue);
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(color);
            spannableString.setSpan(backgroundColorSpan, indexOfKeyword, indexOfKeyword + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.BLUE });
            //TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.BOLD_ITALIC, -1, blueColor, null);
            //spannableString.setSpan(textAppearanceSpan, indexOfKeyword, indexOfKeyword + keyword.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            indexOfKeyword = spannableString.toString().indexOf(keyword, indexOfKeyword + keyword.length());
        }

        textView.setText(spannableString);
    }

    private static Context getContext() {
        return IadtController.get().getContext();
    }

    public View findParentById(View view, int targetId) {
        if (view.getId() == targetId) {
            return (View)view;
        }
        View parent = (View) view.getParent();
        if (parent == null) {
            return null;
        }
        return findParentById(parent, targetId);
    }

    public static float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static Drawable getDrawable(int icon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getContext().getDrawable(icon);
        } else {
            return getContext().getResources().getDrawable(icon);
        }
    }
}
