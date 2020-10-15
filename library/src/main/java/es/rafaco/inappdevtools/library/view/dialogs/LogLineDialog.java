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

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.view.components.items.HeaderFlexData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogLineFormatter;

public class LogLineDialog extends IadtDialogBuilder {

    private final long logId;

    public LogLineDialog(long logId) {
        super();
        this.logId = logId;
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {

        final Friendly logData = IadtDatabase.get().friendlyDao().findById(logId);
        final LogLineFormatter formatter = new LogLineFormatter(logData);

        builder
                .setTitle("Log line")
                /*.setMessage("You can force a crash now to test our library")
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
        HeaderFlexData message = new HeaderFlexData(logData.getMessage());
        message.setBold(true);
        message.setSize(TextFlexData.Size.LARGE);
        data.add(message);

        data.add(new HeaderFlexData(""));
        if (formatter.getLinkStep() != null){
            ButtonFlexData linkedButton = new ButtonFlexData(formatter.getLinkName(),
                    new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                            OverlayService.performNavigationStep(formatter.getLinkStep());
                        }
                    });
            linkedButton.setFullSpan(true);
            linkedButton.setColor(R.color.material_blue_900);
            data.add(linkedButton);
        }
        data.add(new ButtonFlexData("Google",
                R.drawable.ic_search_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        Iadt.buildMessage("Searching for log message")
                                .isDev().fire();
                        ExternalIntentUtils.search(logData.getMessage());
                    }
                }));
        data.add(new ButtonFlexData("Copy",
                R.drawable.ic_content_copy_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        Iadt.buildMessage("Copied log message to clipboard")
                                .isDev().fire();
                        ClipboardUtils.save(IadtController.get().getContext(), logData.getMessage());
                    }
                }));
        data.add(new ButtonFlexData("Share",
                R.drawable.ic_share_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        Iadt.buildMessage("Sharing log overview")
                                .isDev().fire();
                        DocumentRepository.shareDocument(DocumentType.LOG_ITEM, logData.getUid());
                    }
                }));

        if (logData.getExtra()!=null && !logData.getExtra().isEmpty()){
            data.add(new HeaderFlexData(logData.getExtra()));
        }
        data.add(new HeaderFlexData(formatter.getDetails()));

        FlexAdapter presetAdapter = new FlexAdapter(FlexAdapter.Layout.GRID, 3, data);
        RecyclerView recyclerView = dialogView.findViewById(R.id.flexible);
        recyclerView.setAdapter(presetAdapter);
    }

    public void onDisable() {
    }
}
