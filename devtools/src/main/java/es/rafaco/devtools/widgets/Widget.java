package es.rafaco.devtools.widgets;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import es.rafaco.devtools.BuildConfig;
import es.rafaco.devtools.R;

public abstract class Widget {

    protected final WidgetsManager manager;

    public enum Type { ICON, REMOVE, FULL }


    protected View view;

    public Widget(WidgetsManager manager) {
        this.manager = manager;
    }

    public void addView() {
        manager.getInflater().getContext().setTheme(R.style.AppCompatAlertDialogStyle);
        //manager.getInflater().cloneInContext(new ContextThemeWrapper(getBaseContext(), R.style.AppCompatAlertDialogStyle));

        view  = manager.getInflater().inflate(getLayoutId(), null);
        WindowManager.LayoutParams paramRemove = getLayoutParams();

        beforeAttachView(view);
        manager.getWindowManager().addView(view, paramRemove);
        afterAttachView(view);
    }

    protected void afterAttachView(View view) { }

    public View getView() {
        return view;
    }

    public void destroy() {
        manager.getWindowManager().removeView(getView());
    }

    protected int getLayoutType(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
        return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }

    public abstract Widget.Type getType();
    protected abstract int getLayoutId();
    protected abstract WindowManager.LayoutParams getLayoutParams();
    protected abstract void beforeAttachView(View view);
}
