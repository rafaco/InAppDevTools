package es.rafaco.devtools.view.overlay.widgets;

import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import es.rafaco.devtools.R;

public class RemoveWidget extends Widget {

    public ImageView remove_image_view;

    public RemoveWidget(WidgetsManager manager) {
        super(manager);
    }

    @Override
    public Type getType() {
        return Type.REMOVE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.widget_remove;
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

        paramRemove.gravity = Gravity.TOP | Gravity.LEFT;
        return paramRemove;
    }

    @Override
    protected void beforeAttachView(View newView) {
        newView.setVisibility(View.GONE);
        remove_image_view = (ImageView) newView.findViewById(R.id.remove_img);
    }
}
