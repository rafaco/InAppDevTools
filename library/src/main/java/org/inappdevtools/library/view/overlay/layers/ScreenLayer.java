/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.overlay.layers;

import android.animation.LayoutTransition;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

//#ifdef ANDROIDX
//@import androidx.appcompat.widget.AppCompatImageView;
//@import androidx.appcompat.widget.Toolbar;
//@import androidx.core.widget.NestedScrollView;
//#else
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
//#endif

import org.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;

import org.inappdevtools.library.R;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.view.dialogs.ToolbarMoreDialog;
import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.utils.UiUtils;
import org.inappdevtools.library.view.overlay.LayerManager;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class ScreenLayer extends Layer {

    private NestedScrollView bodyScroll;
    private FrameLayout bodyContainer;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private LinearLayout fullContainer;

    public enum SizePosition { FULL, HALF_TOP, HALF_BOTTOM, HALF_LEFT, HALF_RIGHT, QUARTER_1, QUARTER_2, QUARTER_3, QUARTER_4 }
    private int currentOrientation;
    private SizePosition currentSizePosition;

    public ScreenLayer(LayerManager manager) {
        super(manager);
    }

    @Override
    public Type getType() {
        return Type.SCREEN;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.overlay_layer_screen;
    }

    @Override
    protected WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getLayoutType(),
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.CENTER;

        initSizePosition(params);

        return params;
    }

    @Override
    protected void beforeAttachView(View view) {
        initScroll();
        initToolbar(view);

        ((FrameLayout)view).setLayoutTransition(new LayoutTransition());
    }

    @Override
    protected void afterAttachView(View view){
        //Hide full view on start
        view.setVisibility(View.GONE);
    }

    //region [ SCROLL ]

    private void initScroll() {
        bodyScroll = getView().findViewById(R.id.scroll_view);
        bodyContainer = getView().findViewById(R.id.tool_body_container);
        progressBar = getView().findViewById(R.id.progressBar);
        fullContainer = getView().findViewById(R.id.full_container);
    }

    public void scrollTop(){
        bodyScroll.post(new Runnable() {
                @Override
                public void run() {
                    bodyScroll.scrollTo(0, 0);
                }
            });
    }

    public void scrollBottom(){
        if (!isScrollAtBottom()){
            bodyScroll.post(new Runnable() {
                @Override
                public void run() {
                    bodyScroll.scrollTo(0, bodyContainer.getHeight());
                }
            });
        }
    }

    public void scrollToView(final View view){
        bodyScroll.post(new Runnable() {
                @Override
                public void run() {
                    final Rect rect = new Rect(0, 0, view.getWidth(), view.getHeight());
                    view.requestRectangleOnScreen(rect, false);
            }
        });
    }

    public void scrollToPixel(final int targetPixel){
        if (bodyContainer.getHeight() < targetPixel){
            bodyScroll.post(new Runnable() {
                @Override
                public void run() {
                    bodyScroll.scrollTo(0, targetPixel);
                }
            });
        }
    }

    public final void focusOnView(final View view){
        bodyScroll.post(new Runnable() {
            @Override
            public void run() {
                bodyScroll.scrollTo(0, view.getBottom());
            }
        });
    }

    public boolean isScrollAtBottom() {
        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
        View lastItem = bodyScroll.getChildAt(bodyScroll.getChildCount()-1);

        // Calculate the scrolldiff
        int diff = (lastItem.getBottom()-(bodyScroll.getHeight()+bodyScroll.getScrollY()));

        // if diff is zero, then the bottom has been reached
        return diff == 0;
    }

    //endregion

    //region [ TOOL BAR ]

    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onToolbarButtonPressed(item);
                return true;
            }
        });

        toggleBackButton(false);
        toolbar.inflateMenu(R.menu.overlay_screen);
    }

    public void setToolbarTitle(String title){
        if (title == null)
            title = "Iadt";

        toolbar.setTitle(title);

        //TODO: subtitle
        //toolbar.setSubtitle("Sample app");
    }

    public void toggleBackButton(boolean showBack){
        if (showBack){
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackButtonPressed();
                }
            });

            toolbar.setLogo(null);
            toolbar.setLogoDescription(null);
        }else{
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);

            addLogoAndResize();
        }
    }

    private void addLogoAndResize() {
        int appIconResourceId = UiUtils.getAppIconResourceId();
        Drawable logo =  IadtController.get().getContext()
                .getResources().getDrawable(appIconResourceId);
        toolbar.setLogo(logo);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child != null && child.getClass() == AppCompatImageView.class) {
                AppCompatImageView iv2 = (AppCompatImageView) child;
                if ( iv2.getDrawable() == logo ) {
                    iv2.setAdjustViewBounds(true);
                    iv2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    int leftMargin = (int)UiUtils.getPixelsFromDp(iv2.getContext(), 16);
                    int otherMargins = iv2.getHeight()/6;
                    Toolbar.LayoutParams layout = (Toolbar.LayoutParams)iv2.getLayoutParams();
                    layout.setMargins(leftMargin, otherMargins, otherMargins, otherMargins);
                    iv2.requestLayout();
                }
            }
        }
    }

    private void onToolbarButtonPressed(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_close) {
            IadtController.get().getOverlayHelper().showIcon();
        }
        else if (selected == R.id.action_more) {
            IadtController.get().getDialogManager().load(new ToolbarMoreDialog());
        }
    }

    private void onBackButtonPressed() {
        OverlayService.performAction(OverlayService.IntentAction.NAVIGATE_BACK);
    }

    public View getFullContainer() {
        return fullContainer;
    }

    public void showProgress(boolean isVisible){
        progressBar.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    //endregion

    //region [ TOGGLE SIZE POSITION ]

    private void initSizePosition(WindowManager.LayoutParams params) {
        currentOrientation = manager.getContext().getResources().getConfiguration().orientation;
        if (!DeviceInfoDocumentGenerator.isBigScreen(manager.getContext())){
            currentSizePosition = SizePosition.FULL;
        }
        else{
            boolean isPortrait = currentOrientation == ORIENTATION_PORTRAIT;
            if (isPortrait) {
                currentSizePosition = SizePosition.HALF_BOTTOM;
                params.height = UiUtils.getDisplaySize(this.view.getContext()).y / 2;
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.gravity = Gravity.BOTTOM;
            }
            else{
                currentSizePosition = SizePosition.HALF_RIGHT;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = UiUtils.getDisplaySize(manager.getContext()).x / 2;
                params.gravity = Gravity.RIGHT | Gravity.TOP;
            }
        }
    }

    public void toggleSizePosition(SizePosition newPosition) {
        WindowManager.LayoutParams viewLayoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
        LinearLayout child = view.findViewById(R.id.main_container);
        FrameLayout.LayoutParams childLayoutParams = (FrameLayout.LayoutParams) child.getLayoutParams();

        switch (newPosition){
            case FULL:
                viewLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                viewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                viewLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
                childLayoutParams.gravity = Gravity.TOP;
                break;
            case HALF_TOP:
                viewLayoutParams.height = UiUtils.getDisplaySize(this.view.getContext()).y / 2;
                viewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                viewLayoutParams.gravity = Gravity.TOP;
                childLayoutParams.gravity = Gravity.TOP;
                break;
            case HALF_BOTTOM:
                viewLayoutParams.height = UiUtils.getDisplaySize(this.view.getContext()).y / 2;
                viewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                viewLayoutParams.gravity = Gravity.BOTTOM;
                childLayoutParams.gravity = Gravity.BOTTOM;
                break;
            case HALF_LEFT:
                viewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                viewLayoutParams.width = UiUtils.getDisplaySize(this.view.getContext()).x / 2;
                viewLayoutParams.gravity = Gravity.LEFT;
                childLayoutParams.gravity = Gravity.TOP;
                break;
            case HALF_RIGHT:
                viewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                viewLayoutParams.width = UiUtils.getDisplaySize(this.view.getContext()).x / 2;
                viewLayoutParams.gravity = Gravity.RIGHT;
                childLayoutParams.gravity = Gravity.TOP;
                break;
            case QUARTER_1:
                viewLayoutParams.height = UiUtils.getDisplaySize(this.view.getContext()).y / 2;
                viewLayoutParams.width = UiUtils.getDisplaySize(this.view.getContext()).x / 2;
                viewLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                childLayoutParams.gravity = Gravity.TOP;
                break;
            case QUARTER_2:
                viewLayoutParams.height = UiUtils.getDisplaySize(this.view.getContext()).y / 2;
                viewLayoutParams.width = UiUtils.getDisplaySize(this.view.getContext()).x / 2;
                viewLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
                childLayoutParams.gravity = Gravity.TOP;
                break;
            case QUARTER_3:
                viewLayoutParams.height = UiUtils.getDisplaySize(this.view.getContext()).y / 2;
                viewLayoutParams.width = UiUtils.getDisplaySize(this.view.getContext()).x / 2;
                viewLayoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
                childLayoutParams.gravity = Gravity.BOTTOM;
                break;
            case QUARTER_4:
                viewLayoutParams.height = UiUtils.getDisplaySize(this.view.getContext()).y / 2;
                viewLayoutParams.width = UiUtils.getDisplaySize(this.view.getContext()).x / 2;
                viewLayoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                childLayoutParams.gravity = Gravity.BOTTOM;
                break;

        }
        currentSizePosition = newPosition;
        child.setLayoutParams(childLayoutParams);
        manager.getWindowManager().updateViewLayout(view, viewLayoutParams);
    }

    //endregion

    @Override
    public void onConfigurationChange(Configuration newConfig) {
        if (currentSizePosition.equals(SizePosition.FULL))
            return;
        
        int newOrientation = newConfig.orientation;
        if (newOrientation == currentOrientation)
            return;

        // On orientation change
        currentOrientation = newOrientation;

        // For small devices, swap half columns to rows
        if (!DeviceInfoDocumentGenerator.isBigScreen(manager.getContext())) {
            if (currentOrientation == ORIENTATION_PORTRAIT){
                switch (currentSizePosition){
                    case HALF_LEFT:
                        currentSizePosition = SizePosition.HALF_TOP;
                        break;
                    case HALF_RIGHT:
                        currentSizePosition = SizePosition.HALF_BOTTOM;
                        break;
                }
            }else{
                switch (currentSizePosition){
                    case HALF_TOP:
                        currentSizePosition = SizePosition.HALF_LEFT;
                        break;
                    case HALF_BOTTOM:
                        currentSizePosition = SizePosition.HALF_RIGHT;
                        break;
                }
            }

        }
        // Update all with new width and height
        toggleSizePosition(currentSizePosition);
    }
}
