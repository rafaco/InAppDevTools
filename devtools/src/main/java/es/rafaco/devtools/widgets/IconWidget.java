package es.rafaco.devtools.widgets;

import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import es.rafaco.devtools.R;
import es.rafaco.devtools.utils.UiUtils;

public class IconWidget extends Widget {
    private View collapsedView;

    public IconWidget(WidgetsManager manager) {
        super(manager);
    }

    @Override
    public Type getType() {
        return Type.ICON;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.widget_icon;
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
}
