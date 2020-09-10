/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.view.overlay.layers;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
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
import es.rafaco.inappdevtools.library.view.utils.BitmapUtils;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;
import es.rafaco.inappdevtools.library.view.overlay.LayerManager;

public class IconLayer extends Layer {

    private View iconWrapper;
    private Point displaySize;

    public IconLayer(LayerManager manager) {
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
        
        //Initial position is on the right border close to top bar
        Point szWindow = UiUtils.getDisplaySize(getView().getContext());
        int iconSize = manager.getContext().getResources()
                .getDimensionPixelSize(R.dimen.iadt_shape_icon_size);
        params.x = szWindow.x;
        params.y = (szWindow.y - iconSize) / 10;

        return params;
    }

    @Override
    protected void beforeAttachView(View view) {
        ImageView iconView = view.findViewById(R.id.icon);
        iconWrapper = view.findViewById(R.id.icon_wrapper);
        displaySize = UiUtils.getDisplaySize(manager.getContext());

        
        Drawable appIconDrawable = UiUtils.getAppIconDrawable();
        BitmapUtils.setImageWithShadow(iconView, appIconDrawable);
        //UiUtils.setAppIconAsBackground(iconView);

        iconWrapper.setVisibility(View.VISIBLE);
        iconWrapper.setOnTouchListener(new IconTouchListener(this, manager));
    }


    /* Listen icon touch and update remove layer */
    private void implementTouchListener() {

    }


    @Override
    public void onConfigurationChange(Configuration newConfig) {

        if (getView() != null) {
            displaySize = UiUtils.getDisplaySize(manager.getContext());
            int statusBarHeight = UiUtils.getStatusBarHeight(manager.getContext());
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) getView().getLayoutParams();

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (layoutParams.y + (getView().getHeight() + statusBarHeight) > displaySize.y) {
                    layoutParams.y = displaySize.y - (getView().getHeight() + statusBarHeight);
                    manager.getWindowManager().updateViewLayout(getView(), layoutParams);
                }

                if (layoutParams.x != 0 && layoutParams.x < displaySize.x) {
                    updatePositionToBorders(displaySize.x);
                }
            }
            else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

                if (layoutParams.x > displaySize.x) {
                    updatePositionToBorders(displaySize.x);
                }
            }
        }
    }


    //region [ Update ]

    public void updatePosition(int x_cord_destination, int y_cord_destination) {
        WindowManager.LayoutParams iconParams = (WindowManager.LayoutParams) getView().getLayoutParams();
        iconParams.x = x_cord_destination;
        iconParams.y = y_cord_destination;
        manager.getWindowManager().updateViewLayout(getView(), iconParams);
    }

    public void updatePositionOverRemove() {
        ImageView removeImage = manager.getRemoveLayer().getRemoveImage();
        int removePositions[] = new int[2];
        removeImage.getLocationOnScreen(removePositions);
        int statusHeight = UiUtils.getStatusBarHeight(getView().getContext());

        int xCord = removePositions[0] + (Math.abs(removeImage.getWidth() - getView().getWidth())) / 2;
        int yCord = removePositions[1] - statusHeight + (Math.abs(removeImage.getHeight() - getView().getHeight())) / 2;

        updatePosition(xCord, yCord);
    }
    
    public void updatePositionToBorders(int x_cord_now) {
        if (x_cord_now <= displaySize.x / 2) {
            moveToLeftBorder(x_cord_now);
        } else {
            moveToRightBorder(x_cord_now);
        }
    }

    private void moveToLeftBorder(final int current_x_cord) {

        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) getView().getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = 0 - (int) (current_x_cord * current_x_cord * step);
                manager.getWindowManager().updateViewLayout(getView(), mParams);
            }

            public void onFinish() {
                mParams.x = 0;
                manager.getWindowManager().updateViewLayout(getView(), mParams);
            }
        }.start();
    }

    private void moveToRightBorder(final int current_x_cord) {
        new CountDownTimer(500, 5) {
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) getView().getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;
                mParams.x = (int) (displaySize.x + (current_x_cord * current_x_cord * step) - getView().getWidth());
                manager.getWindowManager().updateViewLayout(getView(), mParams);
            }

            public void onFinish() {
                mParams.x = displaySize.x - getView().getWidth();
                manager.getWindowManager().updateViewLayout(getView(), mParams);
            }
        }.start();
    }

    //endregion
}
