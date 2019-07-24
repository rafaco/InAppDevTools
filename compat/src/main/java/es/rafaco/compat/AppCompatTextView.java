package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef ANDROIDX
//@public class AppCompatTextView extends androidx.appcompat.widget.AppCompatTextView {
//#else
public class AppCompatTextView extends android.support.v7.widget.AppCompatTextView {
//#endif

    public AppCompatTextView(Context context) {
        super(context);
    }

    public AppCompatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppCompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
