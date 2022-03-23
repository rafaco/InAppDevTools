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

package org.inappdevtools.library.view.overlay.screens.session;

import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.data.DocumentData;
import org.inappdevtools.library.logic.log.filter.LogFilterHelper;
import org.inappdevtools.library.storage.db.entities.Session;
import org.inappdevtools.library.view.components.composers.SecondaryButtonsComposer;
import org.inappdevtools.library.view.components.groups.LinearGroupFlexData;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.AbstractDocumentScreen;
import org.inappdevtools.library.view.overlay.screens.Screen;
import org.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import org.inappdevtools.library.view.overlay.screens.crash.CrashScreen;
import org.inappdevtools.library.view.overlay.screens.log.LogScreen;

public class SessionDetailScreen extends AbstractDocumentScreen {

    private Session session;

    public SessionDetailScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    protected DocumentType getDocumentType() {
        return DocumentType.SESSION;
    }

    @Override
    protected Class<? extends Screen> getMasterScreenClass() {
        return SessionsScreen.class;
    }

    @Override
    protected Object getDocumentParam() {
        int sessionUid = Integer.parseInt(getParam());
        session = IadtController.get().getSessionManager().getSessionWithOverview(sessionUid);
        return session;
    }

    @Override
    protected List<Object> buildDataFromDocument(DocumentData reportData) {
        List<Object> objectList = new ArrayList<Object>(reportData.getSections());
        objectList.add(0, buildOverviewData(reportData));
        objectList.add(getSecondaryButtonsList());
        return objectList;
    }

    private LinearGroupFlexData getSecondaryButtonsList() {
        SecondaryButtonsComposer composer = new SecondaryButtonsComposer("Related");
        if (session.getCrashId()>0) {
            composer.add("Crash Details",
                    R.string.gmd_bug_report,
                    R.color.rally_orange,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(CrashScreen.class,
                                    session.getCrashId() + "");
                        }
                    });
        }
        composer.add("Repro Steps",
                R.string.gmd_format_list_numbered,
                R.color.rally_green_alpha,
                new Runnable() {
                    @Override
                    public void run() {
                        final LogFilterHelper stepsFilter = new LogFilterHelper(LogFilterHelper.Preset.REPRO_STEPS);
                        stepsFilter.setSessionById(session.getUid());
                        OverlayService.performNavigation(LogScreen.class,
                                LogScreen.buildParams(stepsFilter.getUiFilter()));
                    }
                });
        composer.add("All Logs",
                R.string.gmd_format_align_left,
                R.color.iadt_primary,
                new Runnable() {
                    @Override
                    public void run() {
                        final LogFilterHelper filter = new LogFilterHelper(LogFilterHelper.Preset.DEBUG);
                        filter.setSessionById(session.getUid());
                        OverlayService.performNavigation(LogScreen.class,
                                LogScreen.buildParams(filter.getUiFilter()));
                    }
                });
        composer.add("Build",
                R.string.gmd_build,
                R.color.iadt_text_high,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BuildDetailScreen.class,
                                session.getBuildId() + "");
                    }
                });

        return composer.compose();
    }
}
