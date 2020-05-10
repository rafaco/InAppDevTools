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

package es.rafaco.inappdevtools.library.view.overlay.screens.logic;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.RunningBroadcastReceiversUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningProcessesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningContentProvidersUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningServicesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningThreadsUtils;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;

public class LogicScreen extends AbstractFlexibleScreen {

    public LogicScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Logic";
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

        data.add("Components");
        data.add(new CardData("Services",
                "Perform long-running operations in the background",
                R.string.gmd_store,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ServicesScreen.class);
                    }
                }).setNavCount(RunningServicesUtils.getCount()));

        data.add(new CardData("Content Providers",
                "Share content between applications",
                R.string.gmd_local_convenience_store,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ContentProvidersScreen.class);
                    }
                }).setNavCount(RunningContentProvidersUtils.getCount()));

        /*data.add(new CardData("Jobs",
                "Perform long-running operations in the background",
                R.string.gmd_store,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(JobsScreen.class);
                    }
                }).setNavCount(RunningJobsUtils.getCount()));*/

        data.add(new CardData("Broadcast Receivers",
                "Listen messages from other applications",
                R.string.gmd_hearing,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BroadcastReceiversScreen.class);
                    }
                }).setNavCount(RunningBroadcastReceiversUtils.getCount()));

        data.add("");
        data.add(new CardData("Processes",
                "Linux processes created by this application",
                R.string.gmd_developer_board,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ProcessesScreen.class);
                    }
                }).setNavCount(RunningProcessesUtils.getCount()));

        data.add(new CardData("Tasks",
                "Stacks of activities",
                R.string.gmd_layers,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(TasksScreen.class);
                    }
                }).setNavCount(RunningTasksUtils.getCount()));

        data.add(new CardData("Threads",
                "Linux threads of execution",
                R.string.gmd_line_style,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ThreadsScreen.class);
                    }
                }).setNavCount(RunningThreadsUtils.getCount()));

        data.add(new CardData("Java Memory",
                "//TODO",
                R.string.gmd_memory,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BuildsScreen.class);
                    }
                }).setNavCount(512));

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
                        IadtController.get().disable();
                    }
                }));
        return data;
    }
}
