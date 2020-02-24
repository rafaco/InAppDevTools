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

package es.rafaco.inappdevtools.library.view.overlay.screens.builds;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.storage.db.entities.Build;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractDocumentScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionsScreen;

public class BuildDetailScreen extends AbstractDocumentScreen {


    private long buildId;

    public BuildDetailScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Build Detail";
    }

    @Override
    protected DocumentType getDocumentType() {
        return DocumentType.BUILD_INFO;
    }

    @Override
    protected Object getDocumentParam() {
        buildId = Integer.parseInt(getParam());
        Build build = IadtController.getDatabase().buildDao().findById(buildId);
        return build.getUid();
    }

    @Override
    protected List<Object> buildDataFromDocument(DocumentData reportData) {
        List<Object> objectList = new ArrayList<Object>(reportData.getSections());
        objectList.add(0, reportData.getOverviewData());
        objectList.add(1, getButtonGroupData(buildId));
        return objectList;
    }


    private ButtonGroupData getButtonGroupData(final long buildId) {
        List<RunButton> buttons = new ArrayList<>();
        buttons.add(new RunButton(
                "Filter sessions",
                R.drawable.ic_history_white_24dp,
                R.color.rally_purple,
                new Runnable() {
                    @Override
                    public void run() {OverlayService.performNavigation(SessionsScreen.class, buildId + "");
                    }
                }));
        return new ButtonGroupData(buttons);
    }
}
