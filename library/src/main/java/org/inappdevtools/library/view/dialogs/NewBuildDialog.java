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

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AlertDialog;
//#endif

import org.inappdevtools.library.logic.config.BuildConfigField;
import org.inappdevtools.library.logic.documents.DocumentRepository;
import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import org.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import org.inappdevtools.library.storage.db.entities.Session;
import org.inappdevtools.library.storage.prefs.utils.NewBuildPrefs;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.items.TextFlexData;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.utils.Humanizer;
import org.inappdevtools.library.view.utils.UiUtils;

public class NewBuildDialog extends IadtDialogBuilder {

    public NewBuildDialog() {
        super();
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {
        String buildText = ((AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO)).getAppNameAndVersions();
        buildText += Humanizer.newLine();
        buildText += ((BuildInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.BUILD_INFO)).getBuildOverviewForWelcome();
        /*buildText += "." + Humanizer.newLine();
        buildText += ((DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.DEVICE_INFO)).getSecondLineOverview();
        buildText += " ";
        buildText += ((OSInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.OS_INFO)).getOneLineOverview();
        buildText += ".";*/

        String notes = IadtController.get().getConfig().getString(BuildConfigField.NOTES);

        builder
                .setTitle(R.string.library_name)
                .setIcon(UiUtils.getAppIconResourceId())
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onDismiss();
                    }
                });

        Session session = IadtController.get().getSessionManager().getCurrent();
        boolean isNotFirstStart = !session.isFirstStart();
        if (isNotFirstStart){
            builder.setNegativeButton(R.string.button_skip_next, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    NewBuildPrefs.saveBuildInfoSkip();
                    onDismiss();
                }
            });
            builder.setCancelable(true);
        }

        List<Object> data = new ArrayList<>();
        TextFlexData buildInfo = new TextFlexData(buildText);
        buildInfo.setGravity(Gravity.CENTER);
        buildInfo.setBold(true);
        buildInfo.setSize(TextFlexData.Size.EXTRA_LARGE);
        buildInfo.setFontColor(R.color.iadt_text_high);
        data.add(buildInfo);

        if (!TextUtils.isEmpty(notes)) {
            /*HeaderFlexData header = new HeaderFlexData("Build notes:");
            header.setFontColor(R.color.iadt_text_solid);
            data.add(header);*/

            data.add(new TextFlexData(notes));
        }else{
            //data.add(new HeaderFlexData(""));
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.flexible_container, null);
        builder.setView(dialogView);

        FlexAdapter presetAdapter = new FlexAdapter(FlexAdapter.Layout.GRID, 1, data);
        RecyclerView recyclerView = dialogView.findViewById(R.id.flexible);
        recyclerView.setAdapter(presetAdapter);

        NewBuildPrefs.saveBuildInfoShown();
    }

    public void onDismiss() {

    }
}
