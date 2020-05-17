/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.overlay.screens.view;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ortiz.touchview.TouchImageView;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.files.utils.ScreenshotUtils;
import es.rafaco.inappdevtools.library.view.components.FlexLoader;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import es.rafaco.inappdevtools.library.view.components.items.CollapsibleFlexData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class ZoomScreen extends Screen {

    private TouchImageView mImageView;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private LinearLayout bottomSheetContainer;

    public ZoomScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart(ViewGroup bodyView) {
        mImageView = bodyView.findViewById(R.id.image_view);
        zoomInButton = bodyView.findViewById(R.id.zoom_in);
        zoomOutButton = bodyView.findViewById(R.id.zoom_out);
        bottomSheetContainer = bodyView.findViewById(R.id.internal_bottom_sheet);

        initImage();
        initZoomButtons();
        initBottomSheet();
    }

    private void initImage() {
        Bitmap bitmap = ScreenshotUtils.getBitmap(true);
        mImageView.setImageBitmap(bitmap);
        mImageView.setMaxZoom(20f);
    }

    private void initZoomButtons() {
        UiUtils.setupIconButton(zoomInButton, R.drawable.ic_add_circle_outline_white_24dp,
                new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OnZoomClicked(true);
            }
        });

        UiUtils.setupIconButton(zoomOutButton, R.drawable.ic_remove_circle_outline_white_24dp, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                float scale = Math.max(mImageView.getCurrentZoom() - 10, mImageView.getMinZoom());
                if (scale != mImageView.getCurrentZoom())
                 mImageView.setZoom(scale);
            }
        });
    }

    private void OnZoomClicked(boolean isZoomIn) {
        float currentZoom = mImageView.getCurrentZoom();
        float scale = isZoomIn
                ? Math.min(currentZoom * 2, mImageView.getMaxZoom())
                : Math.max(currentZoom / 2, mImageView.getMinZoom());
        if (scale != currentZoom)
            mImageView.setZoom(scale);
    }

    private void initBottomSheet() {
        String title = "Current view snapshot";
        String message = "Pinch to zoom, double tap to restore";

        CardGroupFlexData cardData = new CardGroupFlexData();
        cardData.setFullWidth(false);
        cardData.setElevationDp(12);
        cardData.setVerticalMargin(true);
        int horizontalMargin = (int) UiUtils.dpToPx(getContext(), 10);
        int topMargin = (int) UiUtils.dpToPx(getContext(), 4);
        int buttonMargin = (int) UiUtils.dpToPx(getContext(), 200);
        cardData.setMargins(horizontalMargin, topMargin, horizontalMargin, buttonMargin);
        cardData.setPerformer(new Runnable() {
            @Override
            public void run() {
                onCardClicked();
            }
        });

        CardHeaderFlexData headerData = new CardHeaderFlexData.Builder(title)
                .setExpandable(true)
                .setExpanded(true)
                .build();

        LinearGroupFlexData collapsedList = new LinearGroupFlexData();
        TextFlexData contentData = new TextFlexData(message);
        contentData.setSize(TextFlexData.Size.LARGE);
        contentData.setHorizontalMargin(true);
        collapsedList.add(contentData);

        LinearGroupFlexData buttonList = new LinearGroupFlexData();
        buttonList.setHorizontal(true);
        buttonList.add(new ButtonBorderlessFlexData("Take shot",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().takeScreenshot();
                    }
                }));

        buttonList.add(new ButtonBorderlessFlexData("Refresh",
                R.drawable.ic_refresh_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        initImage();
                    }
                }));
        collapsedList.add(buttonList);

        CollapsibleFlexData collapsibleData = new CollapsibleFlexData(headerData,
                collapsedList, true);
        cardData.add(collapsibleData);
        
        FlexLoader.addToView(cardData, bottomSheetContainer);
    }

    private void onCardClicked() {

    }

    @Override
    public boolean needNestedScroll() {
        return false;
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }

    @Override
    public String getTitle() {
        return "Zoom";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_zoom;
    }
}
