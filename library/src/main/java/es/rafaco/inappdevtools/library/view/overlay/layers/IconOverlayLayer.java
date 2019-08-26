package es.rafaco.inappdevtools.library.view.overlay.layers;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;
import es.rafaco.inappdevtools.library.view.overlay.OverlayLayersManager;

public class IconOverlayLayer extends OverlayLayer {

    private View collapsedView;

    public IconOverlayLayer(OverlayLayersManager manager) {
        super(manager);
    }

    @Override
    public Type getType() {
        return Type.ICON;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.overlay_layer_icon;
    }

    @NonNull
    protected WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getLayoutType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        Point szWindow = UiUtils.getDisplaySize(getView().getContext());
        params.x = szWindow.x;
        params.y = 120;

        return params;
    }

    @Override
    protected void beforeAttachView(View view) {
        collapsedView = view.findViewById(R.id.icon_wrapper);
        ImageView collapsedIcon = view.findViewById(R.id.icon);

        UiUtils.setAppIconAsBackground(collapsedIcon);

        collapsedView.setVisibility(View.VISIBLE);

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run() {
                collapsedView.setAlpha((float) 0.65);
            }
        }, 5 * 1000);*/
    }


    @Override
    public void onConfigurationChange(Configuration newConfig) {
        //TODO
        // if half:  top is left and bottom is right
    }
}
