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

package org.inappdevtools.library.view.dialogs;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
//#endif

import org.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import org.inappdevtools.library.logic.utils.ExternalIntentUtils;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.base.FlexData;
import org.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import org.inappdevtools.library.view.components.items.ButtonFlexData;
import org.inappdevtools.library.view.components.items.HeaderFlexData;
import org.inappdevtools.library.view.components.items.TextFlexData;
import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.layers.ScreenLayer;
import org.inappdevtools.library.view.overlay.screens.home.ConfigScreen;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

public class ToolbarMoreDialog extends IadtDialogBuilder {

    public ToolbarMoreDialog() {
        super();
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {
        builder
                .setTitle("Options")
                .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onDisable();
                    }
                })
                .setCancelable(true);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View dialogView = inflater.inflate(R.layout.flexible_container, null);
        builder.setView(dialogView);

        List<Object> data = new ArrayList<>();
        addPositionButtons(data);
        addShortcutButtons(data);
        addAboutButtons(data);

        FlexAdapter presetAdapter = new FlexAdapter(FlexAdapter.Layout.GRID, 3, data);
        RecyclerView recyclerView = dialogView.findViewById(R.id.flexible);
        recyclerView.setAdapter(presetAdapter);
    }

    private void addPositionButtons(List<Object> data) {
        HeaderFlexData visualizationHeader = new HeaderFlexData("UI position");
        visualizationHeader.setBold(false);
        visualizationHeader.setSize(TextFlexData.Size.LARGE);
        data.add(visualizationHeader);

        Boolean isBigScreen = DeviceInfoDocumentGenerator.isBigScreen(context);
        Boolean isLandscape = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (isBigScreen) addPositionFirstLine(data);
        addPositionMiddleLine(data, isBigScreen, isLandscape);
        if (isBigScreen) addPositionLastLine(data);
    }

    private void addPositionFirstLine(List<Object> data) {
        LinearGroupFlexData visualizationButtons1 = new LinearGroupFlexData();
        visualizationButtons1.setHorizontal(true);
        visualizationButtons1.setHorizontalMargin(true);
        visualizationButtons1.setChildLayout(FlexData.LayoutType.SAME_WIDTH);
        visualizationButtons1.add(new ButtonFlexData("TOP LEFT",
                R.drawable.ic_arrow_north_west_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.QUARTER_1);
                        destroy();
                    }
                }));
        visualizationButtons1.add(new ButtonFlexData("Top",
                R.drawable.ic_arrow_up_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.HALF_TOP);
                        destroy();
                    }
                }));
        visualizationButtons1.add(new ButtonFlexData("TOP RIGHT",
                R.drawable.ic_arrow_north_east_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.QUARTER_2);
                        destroy();
                    }
                }));
        data.add(visualizationButtons1);
    }

    private void addPositionMiddleLine(List<Object> data, Boolean isBigScreen, Boolean isLandscape) {
        LinearGroupFlexData visualizationButtons2 = new LinearGroupFlexData();
        visualizationButtons2.setHorizontal(true);
        visualizationButtons2.setHorizontalMargin(true);
        visualizationButtons2.setChildLayout(FlexData.LayoutType.SAME_WIDTH);

        String firstHalfLabel;
        String secondHalfLabel;
        int firstHalfIcon;
        int secondHalfIcon;
        final ScreenLayer.SizePosition firstPosition;
        final ScreenLayer.SizePosition secondPosition;

        // TODO: listen for screen rotation and update values
        if (isBigScreen || isLandscape){
            // Standard middle line
            firstHalfLabel = "Left";
            secondHalfLabel = "Right";
            firstHalfIcon = R.drawable.ic_arrow_left_white_24dp;
            secondHalfIcon = R.drawable.ic_arrow_right_white_24dp;
            firstPosition = ScreenLayer.SizePosition.HALF_LEFT;
            secondPosition = ScreenLayer.SizePosition.HALF_RIGHT;
        }
        else{
            // Landscape phones middle line
            firstHalfLabel = "Top";
            secondHalfLabel = "Bottom";
            firstHalfIcon = R.drawable.ic_arrow_up_white_24dp;
            secondHalfIcon = R.drawable.ic_arrow_down_white_24dp;
            firstPosition = ScreenLayer.SizePosition.HALF_TOP;
            secondPosition = ScreenLayer.SizePosition.HALF_BOTTOM;
        }

        visualizationButtons2.add(new ButtonFlexData(firstHalfLabel,
                firstHalfIcon,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(firstPosition);
                        destroy();
                    }
                }));
        visualizationButtons2.add(new ButtonFlexData("Full",
                R.drawable.ic_unfold_more_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.FULL);
                        destroy();
                    }
                }));
        visualizationButtons2.add(new ButtonFlexData(secondHalfLabel,
                secondHalfIcon,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(secondPosition);
                        destroy();
                    }
                }));
        data.add(visualizationButtons2);
    }

    private void addPositionLastLine(List<Object> data) {
        LinearGroupFlexData visualizationButtons3 = new LinearGroupFlexData();
        visualizationButtons3.setHorizontal(true);
        visualizationButtons3.setHorizontalMargin(true);
        visualizationButtons3.setChildLayout(FlexData.LayoutType.SAME_WIDTH);
        visualizationButtons3.add(new ButtonFlexData("BOTTOM LEFT",
                R.drawable.ic_arrow_south_west_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.QUARTER_3);
                        destroy();
                    }
                }));
        visualizationButtons3.add(new ButtonFlexData("Bottom",
                R.drawable.ic_arrow_down_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.HALF_BOTTOM);
                        destroy();
                    }
                }));
        visualizationButtons3.add(new ButtonFlexData("BOTTOM RIGHT",
                R.drawable.ic_arrow_south_east_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.QUARTER_4);
                        destroy();
                    }
                }));
        data.add(visualizationButtons3);
    }

    private void addShortcutButtons(List<Object> data) {
        HeaderFlexData screenHeader = new HeaderFlexData("Report shortcuts");
        screenHeader.setBold(false);
        screenHeader.setSize(TextFlexData.Size.LARGE);
        data.add(screenHeader);

        LinearGroupFlexData screenButtons = new LinearGroupFlexData();
        screenButtons.setHorizontal(true);
        screenButtons.setHorizontalMargin(true);
        screenButtons.setChildLayout(FlexData.LayoutType.SAME_WIDTH);
        screenButtons.add(new ButtonFlexData("Report",
                R.drawable.ic_send_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        destroy();
                        IadtController.get().startReportWizard();
                    }
                }));
        screenButtons.add(new ButtonFlexData("App",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        destroy();
                        IadtController.get().takeScreenshot();
                    }
                }));
        screenButtons.add(new ButtonFlexData("Library",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        destroy();
                        IadtController.get().takeLibraryScreenshot();
                    }
                }));
        data.add(screenButtons);
    }

    private void addAboutButtons(List<Object> data) {
        HeaderFlexData iadtHeader = new HeaderFlexData("About this library...");
        iadtHeader.setBold(false);
        iadtHeader.setSize(TextFlexData.Size.LARGE);
        data.add(iadtHeader);

        LinearGroupFlexData iadtOptions = new LinearGroupFlexData();
        iadtOptions.setHorizontal(true);
        iadtOptions.setHorizontalMargin(true);
        iadtOptions.setChildLayout(FlexData.LayoutType.SAME_WIDTH);
        iadtOptions.add(new ButtonFlexData("Config",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        destroy();
                        OverlayService.performNavigation(ConfigScreen.class);
                    }
                }));
        iadtOptions.add(new ButtonFlexData("Help",
                R.drawable.ic_help_outline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        destroy();
                        ExternalIntentUtils.viewReadme();
                    }
                }));
        iadtOptions.add(new ButtonFlexData("Share",
                R.drawable.ic_share_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        destroy();
                        ExternalIntentUtils.shareLibrary();
                    }
                }));
        data.add(iadtOptions);
    }

    public void onDisable() {
    }
}
