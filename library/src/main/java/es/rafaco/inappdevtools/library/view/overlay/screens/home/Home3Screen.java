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

package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.LiveInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.OSInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.flex.WideWidgetData;
import es.rafaco.inappdevtools.library.view.components.flex.WidgetData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoOverviewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;

public class Home3Screen extends AbstractFlexibleScreen {


    public Home3Screen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Home3";
    }

    @Override
    public int getSpanCount() {
        return 2;
    }
    
    @Override
    protected void onAdapterStart() {
        updateAdapter(getFlexibleData());
    }

    private List<Object> getFlexibleData() {
        List<Object> data = new ArrayList<>();

        AppInfoDocumentGenerator appHelper = ((AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO));
        BuildInfoDocumentGenerator buildReporter = ((BuildInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.BUILD_INFO));
        DeviceInfoDocumentGenerator deviceHelper = ((DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.DEVICE_INFO));
        OSInfoDocumentGenerator osHelper = ((OSInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.OS_INFO));


        WideWidgetData teamData = (WideWidgetData) new WideWidgetData.Builder("Team Resources")
                //.setIcon(R.string.gmd_people)
                .setMainContent("AwesomeDevs")
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ReportScreen.class);
                    }
                })
                .build();
        data.add(teamData);

        WidgetData appData = new WidgetData.Builder("App")
                //.setIcon(R.string.gmd_apps)
                .setMainContent(appHelper.getAppName())
                .setSecondContent(appHelper.getFormattedVersionShort()
                        + " " + buildReporter.getFriendlyBuildType())
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(InfoOverviewScreen.class);
                    }
                })
                .build();
        data.add(appData);

        WidgetData deviceData = new WidgetData.Builder("Device")
                //.setIcon(R.string.gmd_phone)
                .setMainContent(osHelper.getOneLineOverview())
                .setSecondContent(deviceHelper.getFormattedDevice())
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(InfoOverviewScreen.class);
                    }
                })
                .build();
        data.add(deviceData);

        ActivityEventDetector activityWatcher = IadtController.get().getEventManager()
                .getActivityWatcher();
        WidgetData viewData = new WidgetData.Builder("View")
                //.setIcon(R.string.gmd_view_carousel)
                .setMainContent(activityWatcher.getCurrentActivityName())
                .setSecondContent("+ 3 fragments")
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(InspectViewScreen.class);
                    }
                })
                .build();
        data.add(viewData);


        LiveInfoDocumentGenerator liveHelper = ((LiveInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.LIVE_INFO));
        String liveMessage = liveHelper.getOverview();

        data.add(new RunButton("More",
                R.drawable.ic_more_vert_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(MoreScreen.class);
                    }
                }));

        return data;
    }
}
