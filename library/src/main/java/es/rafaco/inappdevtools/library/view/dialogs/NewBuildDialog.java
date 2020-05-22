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
import android.text.TextUtils;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.OSInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.prefs.utils.NewBuildPrefs;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

public abstract class NewBuildDialog extends IadtDialogBuilder {

    public NewBuildDialog() {
        super();
    }

    @Override
    public void onBuilderCreated(AlertDialog.Builder builder) {
        String welcomeText = ((AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO)).getAppNameAndVersions();
        welcomeText += "." + Humanizer.newLine();
        welcomeText += ((BuildInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.BUILD_INFO)).getBuildOverviewForWelcome();
        welcomeText += "." + Humanizer.newLine();
        welcomeText += ((DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.DEVICE_INFO)).getSecondLineOverview();
        welcomeText += " ";
        welcomeText += ((OSInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.OS_INFO)).getOneLineOverview();
        welcomeText += ".";

        String notes = IadtController.get().getConfig().getString(BuildConfigField.NOTES);
        if (!TextUtils.isEmpty(notes)){
            welcomeText += Humanizer.fullStop();
            welcomeText += notes;
        }

        builder.setTitle(R.string.welcome_welcome_title)
                .setMessage(welcomeText)
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
        }

        NewBuildPrefs.saveBuildInfoShown();
    }

    public void onDismiss() {

    }
}
