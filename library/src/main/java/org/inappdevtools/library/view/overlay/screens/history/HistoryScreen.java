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

package org.inappdevtools.library.view.overlay.screens.history;

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.view.components.cards.CardData;
import org.inappdevtools.library.view.components.items.ButtonFlexData;
import org.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;
import org.inappdevtools.library.view.overlay.screens.session.SessionsScreen;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import org.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;
import org.inappdevtools.library.view.overlay.screens.crash.CrashesScreen;

public class HistoryScreen extends AbstractFlexibleScreen {

    public HistoryScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "History";
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

        int buildCount = IadtDatabase.get().buildDao().count();
        data.add(new CardData("Builds",
                "Compilations used and their changes",
                R.string.gmd_build,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BuildsScreen.class);
                    }
                }).setNavCount(buildCount));

        int sessionCount = IadtDatabase.get().sessionDao().count();
        data.add(new CardData("Sessions",
                "From app open to fully close",
                R.string.gmd_history,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SessionsScreen.class);
                    }
                }).setNavCount(sessionCount));

        int crashCount = IadtDatabase.get().crashDao().count();
        data.add(new CardData("Crashes",
                "Unexpected exits caused by an unhandled exception",
                R.string.gmd_bug_report,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(CrashesScreen.class);
                    }
                }).setNavCount(crashCount));

        int screenshotsCount = IadtDatabase.get().screenshotDao().count();
        data.add(new CardData("Screenshots",
                "Taken by users or when crash happen",
                R.string.gmd_photo_library,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ScreenshotsScreen.class);
                    }
                }).setNavCount(screenshotsCount));

        data.add("");
        data.add("Shortcuts");
        data.add(new ButtonFlexData("Clean all...",
                R.drawable.ic_delete_forever_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().cleanAll();
                    }
                }));

        data.add(new ButtonFlexData("Disable Iadt...",
                R.drawable.ic_power_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().disable();
                    }
                }));
        return data;
    }
}
