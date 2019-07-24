package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.annotation.Nullable;
//@public class RecyclerView extends androidx.recyclerview.widget.RecyclerView {
//#else
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
public class RecyclerView extends android.support.v7.widget.RecyclerView {
//#endif

    public RecyclerView(@NonNull Context context) {
        super(context);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
