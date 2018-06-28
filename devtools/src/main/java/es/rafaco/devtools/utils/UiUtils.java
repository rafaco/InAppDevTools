package es.rafaco.devtools.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import es.rafaco.devtools.DevTools;


public class UiUtils {

    public static void setAppIconAsBackground(ImageView imageView){
        Drawable d = getAppIconDrawable();
        imageView.setImageDrawable(d);
    }

    public static Drawable getAppIconDrawable() {
        Context context = DevTools.getAppContext();
        return context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
    }

    public static int getAppIconResourceId(){
        Context context = DevTools.getAppContext();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return applicationInfo.icon;
    }

    private void showKeyboard(View target){
        Context context = DevTools.getAppContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(target.getContext().INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    private void hideKeyboard(View target){
        Context context = DevTools.getAppContext();
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
}
