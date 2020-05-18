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

package es.rafaco.inappdevtools.library.view.overlay;

import es.rafaco.inappdevtools.library.view.overlay.screens.app.AppInfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.app.AppScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.app.ToolsInfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.device.TerminalScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.crash.CrashesScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.crash.CrashScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.history.HistoryScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.ConfigScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.device.DeviceScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.HomeScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.BroadcastReceiversScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.ContentProvidersScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.LogicScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.ProcessesScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.ServicesScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.TasksScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.ThreadsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.view.FragmentsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.view.ViewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.MoreScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.RunScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.TeamScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.device.DeviceInfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoOverviewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.device.OsInfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.AnalysisScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.NewReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.RepoInfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceCodeScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.view.ZoomScreen;


public class ScreensLoader {

    /**
     * Load screenshots definitions. Should be replaced by dagger
     * @param screenManager
     */
    static void registerAllScreens(ScreenManager screenManager) {
        //HOME
        screenManager.registerScreen(HomeScreen.class);

        //TOPBAR
        screenManager.registerScreen(ConfigScreen.class);

        //TEAM
        screenManager.registerScreen(TeamScreen.class);
        screenManager.registerScreen(ReportScreen.class);
        screenManager.registerScreen(NewReportScreen.class);
        screenManager.registerScreen(ReportsScreen.class);
        screenManager.registerScreen(ScreenshotsScreen.class);

        //DEVICE
        screenManager.registerScreen(DeviceScreen.class);
        screenManager.registerScreen(DeviceInfoScreen.class);
        screenManager.registerScreen(OsInfoScreen.class);
        screenManager.registerScreen(TerminalScreen.class);

        //APP
        screenManager.registerScreen(AppScreen.class);
        screenManager.registerScreen(AppInfoScreen.class);
        //screenManager.registerScreen(BuildDetailScreen.class);
        screenManager.registerScreen(ToolsInfoScreen.class);

        //SOURCE CODE
        screenManager.registerScreen(SourceCodeScreen.class);
        screenManager.registerScreen(RepoInfoScreen.class);
        screenManager.registerScreen(SourcesScreen.class);
        screenManager.registerScreen(SourceDetailScreen.class);

        //VIEW
        screenManager.registerScreen(ViewScreen.class);
        screenManager.registerScreen(FragmentsScreen.class);

        //LOGIC
        screenManager.registerScreen(LogicScreen.class);
        screenManager.registerScreen(ServicesScreen.class);
        screenManager.registerScreen(ContentProvidersScreen.class);
        screenManager.registerScreen(BroadcastReceiversScreen.class);
        //screenManager.registerScreen(JobsScreen.class);
        screenManager.registerScreen(ProcessesScreen.class);
        screenManager.registerScreen(TasksScreen.class);
        screenManager.registerScreen(ThreadsScreen.class);

        //HISTORY
        screenManager.registerScreen(HistoryScreen.class);
        screenManager.registerScreen(SessionsScreen.class);
        screenManager.registerScreen(SessionDetailScreen.class);
        screenManager.registerScreen(BuildsScreen.class);
        screenManager.registerScreen(BuildDetailScreen.class);

        //CRASHES
        screenManager.registerScreen(CrashScreen.class);
        screenManager.registerScreen(CrashesScreen.class);

        //Under classification
        screenManager.registerScreen(ZoomScreen.class);
        screenManager.registerScreen(InfoOverviewScreen.class);
        screenManager.registerScreen(InfoScreen.class);
        screenManager.registerScreen(LogScreen.class);
        screenManager.registerScreen(RunScreen.class);
        screenManager.registerScreen(MoreScreen.class);
        screenManager.registerScreen(AnalysisScreen.class);
        screenManager.registerScreen(NetScreen.class);
        screenManager.registerScreen(NetDetailScreen.class);
    }
}
