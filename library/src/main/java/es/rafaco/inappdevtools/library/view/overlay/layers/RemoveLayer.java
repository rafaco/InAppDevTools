package es.rafaco.inappdevtools.library.view.overlay.layers;

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
import es.rafaco.inappdevtools.library.view.overlay.LayerManager;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class RemoveLayer extends Layer {

    public ImageView removeImage;

    public RemoveLayer(LayerManager manager) {
        super(manager);
    }

    @Override
    public Type getType() {
        return Type.REMOVE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.overlay_layer_remove;
    }

    @NonNull
    protected WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams paramRemove = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getLayoutType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        paramRemove.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        return paramRemove;
    }

    @Override
    protected void beforeAttachView(View newView) {
        newView.setVisibility(View.GONE);
        removeImage = newView.findViewById(R.id.remove_img);
    }


    public void initialPosition() {
        Point displaySize = UiUtils.getDisplaySize(manager.getContext());
        WindowManager.LayoutParams removeParams = (WindowManager.LayoutParams) getView().getLayoutParams();
        removeParams.x = (displaySize.x - getView().getWidth()) / 2;
        removeParams.y = displaySize.y - (getView().getHeight() + UiUtils.getStatusBarHeight(manager.getContext()));
        manager.getWindowManager().updateViewLayout(getView(), removeParams);
    }

    public void updateBoundedState(boolean isBounded) {
        int color = isBounded ? R.color.chuck_status_500 : R.color.rally_gray;
        UiUtils.setBackgroundColorToDrawable(manager.getContext(), color, removeImage.getBackground());
    }

    public boolean isOverRemove(int xTouch, int yTouch) {
        Point displaySize = UiUtils.getDisplaySize(manager.getContext());
        double boundMultiplier = 1.5;
        
        int x_bound_left = displaySize.x / 2 - (int) (removeImage.getLayoutParams().width * boundMultiplier);
        int x_bound_right = displaySize.x / 2 + (int) (removeImage.getLayoutParams().width * boundMultiplier);
        int y_bound_top = displaySize.y - (int) (removeImage.getLayoutParams().height * boundMultiplier);

        return (xTouch >= x_bound_left && xTouch <= x_bound_right) && yTouch >= y_bound_top;
    }

    public void show() {
        getView().setVisibility(View.VISIBLE);
    }

    public void hide() {
        getView().setVisibility(View.GONE);
    }

    public ImageView getRemoveImage() {
        return removeImage;
    }
}
