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
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ortiz.touchview.TouchImageView;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.storage.files.utils.ScreenshotUtils;
import es.rafaco.inappdevtools.library.view.components.FlexLoader;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonIconFlexData;
import es.rafaco.inappdevtools.library.view.components.items.CollapsibleFlexData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.crash.CrashScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.ImageLoaderAsyncTask;

public class ZoomScreen extends Screen {

    private TouchImageView mImageView;
    private LinearLayout floatingButtons;
    private LinearLayout bottomSheetContainer;
    private Screenshot screen;

    public ZoomScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        //return TextUtils.isEmpty(getParam()) ? "Live Zoom" : "Screenshot";
        return "";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_zoom;
    }

    @Override
    protected void onCreate() {
        if (!TextUtils.isEmpty(getParam())){
            Long screenId = Long.parseLong(getParam());
            screen = IadtController.getDatabase().screenshotDao().findById(screenId);
        }
    }

    @Override
    protected void onStart(ViewGroup bodyView) {
        mImageView = bodyView.findViewById(R.id.image_view);
        floatingButtons = bodyView.findViewById(R.id.floating_buttons);
        bottomSheetContainer = bodyView.findViewById(R.id.internal_bottom_sheet);

        initImage();
        initZoomButtons();
        initBottomSheet();
    }

    private void initImage() {
        if (screen == null){
            Bitmap bitmap = ScreenshotUtils.getBitmap(true);
            mImageView.setImageBitmap(bitmap);
            //mImageView.setMinZoom(0.7f);
            mImageView.setMaxZoom(20f);
            //mImageView.setZoom(0.9f);
        }
        else{
            new ImageLoaderAsyncTask(mImageView,
                    new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setMaxZoom(20f);
                            //mImageView.setMinZoom(0.7f);
                            //mImageView.setZoom(0.9f);
                        }
                    }).execute(screen.getPath());
        }
    }

    private void initZoomButtons() {

        ButtonIconFlexData zoomInButton = new ButtonIconFlexData(R.drawable.ic_add_circle_outline_white_24dp,
                R.color.iadt_surface_top,
                new Runnable() {
                    @Override
                    public void run() {
                        OnZoomClicked(true);
                    }
                });
        FlexLoader.addToView(zoomInButton, floatingButtons);

        ButtonIconFlexData zoomOutButton = new ButtonIconFlexData(R.drawable.ic_remove_circle_outline_white_24dp,
                R.color.iadt_surface_top,
                new Runnable() {
                    @Override
                    public void run() {
                        OnZoomClicked(false);
                    }
                });
        FlexLoader.addToView(zoomOutButton, floatingButtons);
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
        String title, message;
        if (screen == null){
            title = "Current view snapshot";
            message = "Pinch or double tap to zoom" + Humanizer.newLine() + "Drag to move";
        }
        else{
            title = "Screnshot " + screen.getUid();
            if (screen.getCrashId()>0){
                title +=  " (Crash)";
            }

            message = screen.getActivityName()
                    + " " + Humanizer.getElapsedTimeLowered(screen.getDate())
                    + Humanizer.newLine()
                    + "Session " + screen.getSessionId() + Humanizer.newLine();

            if (screen.getCrashId()>0){
                message += "Crash " + screen.getCrashId() + Humanizer.newLine();
            }
        }

        CardGroupFlexData cardData = new CardGroupFlexData();
        cardData.setFullWidth(true);
        cardData.setElevationDp(12);
        cardData.setVerticalMargin(false);
        cardData.setHorizontalMargin(false);
        /*int horizontalMargin = (int) UiUtils.dpToPx(getContext(), 10);
        int topMargin = (int) UiUtils.dpToPx(getContext(), 4);
        int buttonMargin = (int) UiUtils.dpToPx(getContext(), 200);
        cardData.setMargins(horizontalMargin, topMargin, horizontalMargin, buttonMargin);*/
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
        if (screen == null){
            buttonList.add(new ButtonBorderlessFlexData("Refresh",
                    R.drawable.ic_refresh_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            initImage();
                        }
                    }));

            buttonList.add(new ButtonBorderlessFlexData("Take shot",
                    R.drawable.ic_add_a_photo_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            IadtController.get().takeScreenshot(new Runnable() {
                                @Override
                                public void run() {
                                    afterScreenshotTaken();
                                }
                            });
                        }
                    }));
        }
        else{
            buttonList.add(new ButtonBorderlessFlexData("Session",
                    R.drawable.ic_timeline_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(SessionDetailScreen.class,
                                    screen.getSessionId() + "");
                        }
                    }));

            if (screen.getCrashId()>0){
                buttonList.add(new ButtonBorderlessFlexData("Crash",
                        R.drawable.ic_bug_report_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                OverlayService.performNavigation(CrashScreen.class,
                                        screen.getCrashId() + "");
                            }
                        }));
            }

            buttonList.add(new ButtonBorderlessFlexData("Share",
                    R.drawable.ic_share_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            FileProviderUtils.sendExternally("", screen.getPath() );
                        }
                    }));
        }
        collapsedList.add(buttonList);

        CollapsibleFlexData collapsibleData = new CollapsibleFlexData(headerData,
                collapsedList, true);
        cardData.add(collapsibleData);
        
        FlexLoader.addToView(cardData, bottomSheetContainer);
    }

    private void afterScreenshotTaken() {
        long screenshotId = IadtController.getDatabase().screenshotDao()
                .getLast().getUid();
        OverlayService.performNavigation(ZoomScreen.class,
                screenshotId + "");
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
}
