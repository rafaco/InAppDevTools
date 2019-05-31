package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef MODERN
//@import androidx.annotation.NonNull;
//@import androidx.annotation.Nullable;
//@public class CardView extends androidx.cardview.widget.CardView {
//#else
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
public class CardView extends android.support.v7.widget.CardView {
//#endif

    public CardView(@NonNull Context context) {
        super(context);
    }

    public CardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
