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

package org.inappdevtools.library.view.overlay.screens.sources;

import android.text.TextUtils;

import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.data.DocumentData;
import org.inappdevtools.library.view.components.composers.SecondaryButtonsComposer;
import org.inappdevtools.library.view.components.groups.LinearGroupFlexData;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.AbstractDocumentScreen;
import org.inappdevtools.library.view.overlay.screens.Screen;
import org.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import org.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;

public class RepoInfoScreen extends AbstractDocumentScreen {

    public RepoInfoScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    protected DocumentType getDocumentType() {
        return DocumentType.REPO_INFO;
    }

    @Override
    protected Class<? extends Screen> getMasterScreenClass() {
        return BuildsScreen.class;
    }

    @Override
    protected Object getDocumentParam() {
        if (TextUtils.isEmpty(getParam())){
            return super.getDocumentParam();
        }
        return Long.parseLong(getParam());
    }

    @Override
    protected List<Object> buildDataFromDocument(DocumentData reportData) {
        List<Object> objectList = new ArrayList<Object>(reportData.getSections());
        objectList.add(0, buildOverviewData(reportData));
        if (reportData.hasScreenItems()) {
            objectList.add(1, reportData.getScreenItem());
        }
        objectList.add(getSecondaryButtonsList());
        return objectList;
    }

    private LinearGroupFlexData getSecondaryButtonsList() {
        SecondaryButtonsComposer composer = new SecondaryButtonsComposer("Related");
        composer.add("Build",
                R.string.gmd_build,
                R.color.iadt_text_high,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BuildDetailScreen.class, getDocumentParam() + "");
                    }
                });
        return composer.compose();
    }
}
