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

package org.inappdevtools.library.view.overlay.screens.home;

import org.inappdevtools.library.logic.external.PandoraBridge;
import org.inappdevtools.library.view.components.items.ButtonFlexData;
import org.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;
import org.inappdevtools.library.view.overlay.screens.network.NetScreen;
import org.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;
import org.inappdevtools.library.view.overlay.screens.session.SessionsScreen;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;

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

        data.add("Playgrounds and works in progress:");


        data.add("");
        data.add(new ButtonFlexData("Builds",
                R.drawable.ic_build_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(BuildsScreen.class);
                    }
                }));

        data.add(new ButtonFlexData("Sessions",
                R.drawable.ic_history_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(SessionsScreen.class);
                    }
                }));

        data.add(new ButtonFlexData("Screens",
                R.drawable.ic_photo_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ScreenshotsScreen.class);
                    }
                }));


        data.add("");
        data.add(new ButtonFlexData("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(NetScreen.class);
                    }
                }));

        data.add(new ButtonFlexData("PND Net",
                R.drawable.ic_extension_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        MoreScreen.this.getScreenManager().hide();
                        PandoraBridge.network();
                    }
                }));

        data.add(new ButtonFlexData("Pandora",
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
