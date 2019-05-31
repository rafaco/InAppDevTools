package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef MODERN
//@import androidx.annotation.NonNull;
//@import androidx.annotation.Nullable;
//@public class CoordinatorLayout extends androidx.coordinatorlayout.widget.CoordinatorLayout {
//#else
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
public class CoordinatorLayout extends android.support.design.widget.CoordinatorLayout {
//#endif

    public CoordinatorLayout(@NonNull Context context) {
        super(context);
    }

    public CoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CoordinatorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
