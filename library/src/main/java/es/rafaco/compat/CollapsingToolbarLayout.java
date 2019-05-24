package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef MODERN
public class CollapsingToolbarLayout extends com.google.android.material.appbar.CollapsingToolbarLayout {
//#else
//@public class CollapsingToolbarLayout extends android.support.design.widget.CollapsingToolbarLayout {
//#endif

    public CollapsingToolbarLayout(Context context) {
        super(context);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
