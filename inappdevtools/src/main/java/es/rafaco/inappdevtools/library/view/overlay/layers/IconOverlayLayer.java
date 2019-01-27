package es.rafaco.inappdevtools.library.view.overlay.layers;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

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
        params.x = 0;
        params.y = 100;
        return params;
    }

    @Override
    protected void beforeAttachView(View view) {
        collapsedView = view.findViewById(R.id.collapsed_view);
        ImageView collapsedIcon = view.findViewById(R.id.collapsed_icon);

        collapsedView.setVisibility(View.VISIBLE);
        UiUtils.setAppIconAsBackground(collapsedIcon);
    }


    @Override
    public void onConfigurationChange(Configuration newConfig) {
        //TODO
        // if half:  top is left and bottom is right
    }
}
