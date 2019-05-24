package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef MODERN
public class FloatingActionButton extends com.google.android.material.floatingactionbutton.FloatingActionButton {
//#else
//@public class FloatingActionButton extends android.support.design.widget.FloatingActionButton {
//#endif

    public FloatingActionButton(Context context) {
        super(context);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
