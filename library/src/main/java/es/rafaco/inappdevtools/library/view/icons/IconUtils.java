package es.rafaco.inappdevtools.library.view.icons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class IconUtils {

    public static final String ROOT = "fonts/";
    public static final String FONTAWESOME = ROOT + "fa-solid-900.ttf";
    public static final String MATERIAL = ROOT + "MaterialIcons-Regular.ttf";

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
        markAsIconContainer(textView, IconUtils.MATERIAL);
        textView.setText(stringIcon);
    }

    public static void applyToImageView(ImageView view, int icon, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setImageDrawable(view.getContext().getDrawable(icon));
        } else {
            view.setImageDrawable(view.getContext().getResources().getDrawable(icon));
        }
        if (color>0){
            int contextualizedColor = ContextCompat.getColor(view.getContext(), color);
            view.setColorFilter(contextualizedColor);
        }
    }
}
