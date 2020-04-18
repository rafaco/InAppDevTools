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

package es.rafaco.inappdevtools.library.view.overlay.screens.history;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.activities.IadtDialogActivity;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionsScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

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

        int sessionCount = IadtController.getDatabase().sessionDao().count();
        data.add(new CardData(Humanizer.plural(sessionCount, "Session"),
                "From app open to fully close",
                R.string.gmd_history,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SessionsScreen.class);
                    }
                }));

        int buildCount = IadtController.getDatabase().buildDao().count();
        data.add(new CardData(Humanizer.plural(buildCount, "Build"),
                "Apk modifications by developers",
                R.string.gmd_build,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BuildsScreen.class);
                    }
                }));

        int crashCount = IadtController.getDatabase().crashDao().count();
        data.add(new CardData(crashCount + " Crashes",
                "Unexpected exits caused by an unhandled exception",
                R.string.gmd_bug_report,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ErrorsScreen.class);
                    }
                }));

        int screenshotsCount = IadtController.getDatabase().screenshotDao().count();
        data.add(new CardData(screenshotsCount + " Screenshots",
                "Taken by users or when crash happen",
                R.string.gmd_photo_library,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ScreenshotsScreen.class);
                    }
                }));

        data.add("");
        data.add("Shortcuts");
        data.add(new RunButton("Clean all...",
                R.drawable.ic_delete_forever_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().cleanAll();
                    }
                }));

        data.add(new RunButton("Disable Iadt...",
                R.drawable.ic_power_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().getOverlayHelper().showIcon();
                        IadtDialogActivity.open(IadtDialogActivity.IntentAction.DISABLE,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Iadt.showMessage("Developer tools disabled!");
                                    }
                                },
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Iadt.showMessage("Developer tools NOT disabled");
                                    }
                                });
                    }
                }));
        return data;
    }
}
