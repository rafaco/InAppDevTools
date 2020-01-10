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

import es.rafaco.inappdevtools.library.view.overlay.screens.console.ConsoleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.AnrDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.ConfigScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.Home2Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.HomeScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.InspectViewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.MoreScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.home.RunScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoOverviewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.AnalysisScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.detail.NetworkDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.NewReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;


public class ScreensLoader {

    static void registerAllScreens(ScreenManager screenManager) {
        //Load screenshots definitions
        screenManager.registerScreen(HomeScreen.class);
        screenManager.registerScreen(Home2Screen.class);
        screenManager.registerScreen(InfoOverviewScreen.class);
        screenManager.registerScreen(InfoScreen.class);
        screenManager.registerScreen(NetworkScreen.class);
        screenManager.registerScreen(ErrorsScreen.class);
        screenManager.registerScreen(LogScreen.class);
        screenManager.registerScreen(LogcatScreen.class);
        screenManager.registerScreen(ConsoleScreen.class);
        screenManager.registerScreen(ScreenshotsScreen.class);
        screenManager.registerScreen(ReportScreen.class);
        screenManager.registerScreen(ReportsScreen.class);
        screenManager.registerScreen(NewReportScreen.class);
        screenManager.registerScreen(CrashDetailScreen.class);
        screenManager.registerScreen(AnrDetailScreen.class);
        screenManager.registerScreen(NetworkDetailScreen.class);
        screenManager.registerScreen(RunScreen.class);
        screenManager.registerScreen(MoreScreen.class);
        screenManager.registerScreen(InspectViewScreen.class);
        screenManager.registerScreen(SourcesScreen.class);
        screenManager.registerScreen(SourceDetailScreen.class);
        screenManager.registerScreen(AnalysisScreen.class);
        screenManager.registerScreen(ConfigScreen.class);
        screenManager.registerScreen(SessionsScreen.class);
        screenManager.registerScreen(SessionDetailScreen.class);
    }
}
