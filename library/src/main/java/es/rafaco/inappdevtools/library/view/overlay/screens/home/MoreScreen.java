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

package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.external.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.NewCrashScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionsScreen;

public class MoreScreen extends AbstractFlexibleScreen {


    public MoreScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "More";
    }

    @Override
    public int getSpanCount() {
        return 3;
    }

    @Override
    protected void onAdapterStart() {
        updateAdapter(getFlexibleData());
    }

    private List<Object> getFlexibleData() {
        List<Object> data = new ArrayList<>();

        data.add("Playgrounds and old screens (pending to remove):\n" +
                " - Home 2 is a proposal in progress\n" +
                " - All other items has been mixed at Log Screen\n");

        data.add(new RunButton("Original Home",
                R.drawable.ic_format_list_bulleted_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(HomeScreen.class);
                    }
                }));

        data.add(new RunButton("Home 3",
                R.drawable.ic_timeline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(Home3Screen.class);
                    }
                }));


        data.add("");
        data.add(new RunButton("Builds",
                R.drawable.ic_build_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(BuildsScreen.class);
                    }
                }));

        data.add(new RunButton("Sessions",
                R.drawable.ic_history_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(SessionsScreen.class);
                    }
                }));

        data.add(new RunButton("Screens",
                R.drawable.ic_photo_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ScreenshotsScreen.class);
                    }
                }));

        data.add(new RunButton("Errors",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ErrorsScreen.class);
                    }
                }));

        data.add(new RunButton("Crash",
                R.drawable.ic_bug_report_white_24dp,
                R.color.rally_orange,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(NewCrashScreen.class);
                    }
                }));


        data.add("");
        data.add(new RunButton("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(NetScreen.class);
                    }
                }));

        data.add(new RunButton("PND Net",
                R.drawable.ic_extension_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        MoreScreen.this.getScreenManager().hide();
                        PandoraBridge.network();
                    }
                }));

        data.add(new RunButton("Pandora",
                R.drawable.ic_extension_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        MoreScreen.this.getScreenManager().hide();
                        PandoraBridge.open();
                    }
                }));


        /*data.add(new RunButton("Analysis",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(AnalysisScreen.class);
                    }
                }));*/

        return data;
    }
}
