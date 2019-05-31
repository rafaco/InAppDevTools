package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef MODERN
//@public class AppBarLayout extends com.google.android.material.appbar.AppBarLayout {
//#else
public class AppBarLayout extends android.support.design.widget.AppBarLayout {
//#endif

    public AppBarLayout(Context context) {
        super(context);
    }

    public AppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
