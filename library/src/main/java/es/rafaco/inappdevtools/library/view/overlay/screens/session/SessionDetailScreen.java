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

package es.rafaco.inappdevtools.library.view.overlay.screens.session;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractDocumentScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;

public class SessionDetailScreen extends AbstractDocumentScreen {

    private Session session;

    public SessionDetailScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Session Detail";
    }

    @Override
    protected DocumentType getDocumentType() {
        return DocumentType.SESSION;
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
        objectList.add(0, reportData.getOverviewData());
        objectList.add(1, getFirstButtonGroupData(session));
        objectList.add(2, getSecondButtonGroupData(session));
        objectList.add(3, "");
        return objectList;
    }

    private ButtonGroupData getFirstButtonGroupData(final Session session) {
        List<RunButton> buttons = new ArrayList<>();
        buttons.add(new RunButton(
                "Steps",
                R.drawable.ic_format_list_numbered_white_24dp,
                R.color.rally_green_alpha,
                new Runnable() {
            @Override
            public void run() {
                final LogFilterHelper stepsFilter = new LogFilterHelper(LogFilterHelper.Preset.REPRO_STEPS);
                stepsFilter.setSessionById(session.getUid());

                OverlayService.performNavigation(LogScreen.class,
                        LogScreen.buildParams(stepsFilter.getUiFilter()));
            }
        }));

        buttons.add(new RunButton(
                "Logs",
                R.drawable.ic_format_align_left_white_24dp,
                R.color.rally_blue_med,
                new Runnable() {
            @Override
            public void run() {
                final LogFilterHelper filter = new LogFilterHelper(LogFilterHelper.Preset.DEBUG);
                filter.setSessionById(session.getUid());

                OverlayService.performNavigation(LogScreen.class,
                        LogScreen.buildParams(filter.getUiFilter()));
            }
        }));

        return new ButtonGroupData(buttons);
    }

    private ButtonGroupData getSecondButtonGroupData(final Session session) {
        List<RunButton> buttons = new ArrayList<>();

        buttons.add(new RunButton(
                "Build Info",
                R.drawable.ic_build_white_24dp,
                R.color.rally_purple,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BuildDetailScreen.class,
                            session.getBuildId() + "");
                    }
                }));

        if (session.getCrashId()>0){
            buttons.add(new RunButton(
                    "Crash Detail",
                    R.drawable.ic_bug_report_white_24dp,
                    R.color.rally_orange,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(CrashDetailScreen.class,
                                    session.getCrashId() + "");
                        }
                    }));
        }

        return new ButtonGroupData(buttons);
    }
}
