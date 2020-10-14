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

package es.rafaco.inappdevtools.library.view.dialogs;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.view.components.items.HeaderFlexData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.layers.ScreenLayer;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.ConfigScreen;

public class ToolbarMoreDialog extends IadtDialogBuilder {

    public ToolbarMoreDialog() {
        super();
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {
        builder
                /*.setTitle("More")
                .setMessage("You can force a crash now to test our library")
                .setIcon(R.drawable.ic_more_vert_white_24dp)*/
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
        data.add(new HeaderFlexData(""));

        data.add(new HeaderFlexData("Take Screenshot"));
        data.add(new ButtonFlexData("App",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        IadtController.get().takeScreenshot();
                    }
                }));
        data.add(new ButtonFlexData("Library",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        IadtController.get().takeLibraryScreenshot();
                    }
                }));

        data.add(new HeaderFlexData("Visualization"));
        data.add(new ButtonFlexData("Top",
                R.drawable.ic_arrow_up_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.HALF_FIRST);
                    }
                }));
        data.add(new ButtonFlexData("Bottom",
                R.drawable.ic_arrow_down_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.HALF_SECOND);
                    }
                }));
        data.add(new ButtonFlexData("Full",
                R.drawable.ic_unfold_more_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        IadtController.get().getOverlayHelper()
                                .toggleScreenLayout(ScreenLayer.SizePosition.FULL);
                    }
                }));


        data.add(new HeaderFlexData("Library options"));
        data.add(new ButtonFlexData("Config",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        OverlayService.performNavigation(ConfigScreen.class);
                    }
                }));
        data.add(new ButtonFlexData("Help",
                R.drawable.ic_help_outline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        ExternalIntentUtils.viewReadme();
                    }
                }));
        data.add(new ButtonFlexData("Share",
                R.drawable.ic_share_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        ExternalIntentUtils.shareLibrary();
                    }
                }));

        FlexAdapter presetAdapter = new FlexAdapter(FlexAdapter.Layout.GRID, 3, data);
        RecyclerView recyclerView = dialogView.findViewById(R.id.flexible);
        recyclerView.setAdapter(presetAdapter);
    }

    public void onDisable() {
    }
}
