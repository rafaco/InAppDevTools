package es.rafaco.devtools.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import es.rafaco.devtools.DevTools;

public class UiUtils {

    public static void setAppIconAsBackground(ImageView imageView){
        Context context = DevTools.getAppContext();
        Drawable d = context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
        imageView.setImageDrawable(d);
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
}
