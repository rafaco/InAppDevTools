package es.rafaco.inappdevtools.library.view.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

//#ifdef MODERN
//@import androidx.cardview.widget.CardView;
//@import androidx.core.content.ContextCompat;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;


public class UiUtils {

    private UiUtils() { throw new IllegalStateException("Utility class"); }

    public static void setAppIconAsBackground(ImageView imageView){
        Drawable d = getAppIconDrawable();
        imageView.setImageDrawable(d);
    }

    public static Drawable getAppIconDrawable() {
        Context context = Iadt.getAppContext();
        return context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
    }

    public static int getAppIconResourceId(){
        Context context = Iadt.getAppContext();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return applicationInfo.icon;
    }

    public static void setStrokeToDrawable(Context context, int i, int color, Drawable background) {
        GradientDrawable drawable = (GradientDrawable)background;
        drawable.setStroke((int)getPixelsFromDp(context, (float)i), ContextCompat.getColor(context, color));
    }

    private void showKeyboard(View target){
        Context context = Iadt.getAppContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(target.getContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard(View target){
        Context context = Iadt.getAppContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(target.getContext().INPUT_METHOD_SERVICE);
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

    public static float getPixelsFromDp(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    public static void setBackgroundColorToDrawable(Context context, int colorRes, Drawable drawable) {
        if (drawable instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, colorRes));
        } else if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(ContextCompat.getColor(context, colorRes));
        } else if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(ContextCompat.getColor(context, colorRes));
        }
    }

    public static void setCardViewClickable(Context context, CardView cardView, boolean clickable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (clickable){
                int[] attrs = new int[]{R.attr.selectableItemBackground};
                TypedArray typedArray = context.obtainStyledAttributes(attrs);
                int selectableItemBackground = typedArray.getResourceId(0, 0);
                typedArray.recycle();
                cardView.setForeground(context.getDrawable(selectableItemBackground));
            }else{
                cardView.setForeground(null);
            }
            cardView.setClickable(clickable);
        }
    }
}
