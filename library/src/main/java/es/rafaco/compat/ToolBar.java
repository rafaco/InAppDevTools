package es.rafaco.compat;

import android.content.Context;
import android.util.AttributeSet;

//#ifdef MODERN
//@import androidx.annotation.Nullable;
//@public class ToolBar extends androidx.appcompat.widget.Toolbar {
//#else
import android.support.annotation.Nullable;
public class ToolBar extends android.support.v7.widget.Toolbar {
//#endif
    
    public ToolBar(Context context) {
        super(context);
    }

    public ToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
