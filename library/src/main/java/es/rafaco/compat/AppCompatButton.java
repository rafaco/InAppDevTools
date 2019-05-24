package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef MODERN
public class AppCompatButton extends androidx.appcompat.widget.AppCompatButton {
//#else
//@public class AppCompatButton extends android.support.v7.widget.AppCompatButton {
//#endif

    public AppCompatButton(Context context) {
        super(context);
    }

    public AppCompatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppCompatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
