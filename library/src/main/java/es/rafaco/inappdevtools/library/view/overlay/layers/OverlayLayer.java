package es.rafaco.inappdevtools.library.view.overlay.layers;

import android.content.res.Configuration;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.OverlayLayersManager;

public abstract class OverlayLayer {

    protected final OverlayLayersManager manager;

    public void onConfigurationChange(Configuration newConfig) {

    }

    public enum Type { ICON, REMOVE, MAIN}


    protected View view;

    public OverlayLayer(OverlayLayersManager manager) {
        this.manager = manager;
    }

    public void addView() {
        manager.getInflater().getContext().setTheme(R.style.LibTheme);
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

    public static int getLayoutType(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_PHONE;
        }
        return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    }

    //TODO: Research https://github.com/Manabu-GT/DebugOverlay-Android
    /*
    public static int getWindowTypeForOverlay(boolean allowSystemLayer) {
        if (allowSystemLayer) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return TYPE_APPLICATION_OVERLAY;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                return TYPE_SYSTEM_ALERT;
            } else {
                return TYPE_TOAST;
            }
        } else {
            // make layout of the window happens as that of a top-level window, not as a child of its container
            return TYPE_APPLICATION_ATTACHED_DIALOG;
        }
    }*/

    public abstract OverlayLayer.Type getType();
    protected abstract int getLayoutId();
    protected abstract WindowManager.LayoutParams getLayoutParams();
    protected abstract void beforeAttachView(View view);
}
