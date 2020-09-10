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

import android.app.ActivityManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;

public class TasksScreen extends AbstractFlexibleScreen {

    public TasksScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
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

        data.add(new OverviewData("Tasks",
                "Stacks of activities readable by this app",
                R.string.gmd_layers,
                R.color.rally_white));

        List<ActivityManager.RunningTaskInfo> runningItems = RunningTasksUtils.getList();

        if (runningItems.isEmpty()){
            String title = "No running tasks, that's weird :(";
            DocumentSectionData.Builder noServiceDataBuilder = new DocumentSectionData.Builder(title)
                    //.setIcon(R.string.gmd_view_carousel)
                    .setExpandable(false);
            data.add(noServiceDataBuilder.build());
        }
        else{
            for (ActivityManager.RunningTaskInfo info : runningItems) {
                String title = RunningTasksUtils.getTitle(info);
                String content = RunningTasksUtils.getContent(info);
                DocumentSectionData.Builder taskDataBuilder = new DocumentSectionData.Builder(title)
                        //.setIcon(R.string.gmd_view_carousel)
                        .setExpandable(false)
                        .add(content);

                String topClassName = RunningTasksUtils.getTopClassName(info);
                final String topSrcPath = getSourcesManager()
                        .getPathFromClassName(topClassName);
                if (!TextUtils.isEmpty(topSrcPath)){
                    taskDataBuilder.addButton(new ButtonBorderlessFlexData("Top SRC",
                            R.drawable.ic_code_white_24dp,
                            new Runnable() {
                                @Override
                                public void run() {
                                    OverlayService.performNavigation(SourceDetailScreen.class,
                                            SourceDetailScreen.buildSourceParams(topSrcPath, -1));
                                }
                            }));
                }

                String baseClassName = RunningTasksUtils.getBaseClassName(info);
                final String baseSrcPath = getSourcesManager()
                        .getPathFromClassName(baseClassName);
                if (!TextUtils.isEmpty(baseSrcPath)){
                    taskDataBuilder.addButton(new ButtonBorderlessFlexData("Base SRC",
                            R.drawable.ic_code_white_24dp,
                            new Runnable() {
                                @Override
                                public void run() {
                                    OverlayService.performNavigation(SourceDetailScreen.class,
                                            SourceDetailScreen.buildSourceParams(baseSrcPath, -1));
                                }
                            }));
                }

                data.add(taskDataBuilder.build());
            }
        }

        data.add("");
        data.add("View AndroidManifest");
        data.add(new ButtonFlexData("Original",
                R.drawable.ic_local_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String params = SourceDetailScreen.buildSourceParams(IadtPath.ORIGINAL_MANIFEST);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));
        data.add(new ButtonFlexData("Merged",
                R.drawable.ic_local_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String params = SourceDetailScreen.buildSourceParams(IadtPath.MERGED_MANIFEST);
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));
        return data;
    }

    private SourcesManager getSourcesManager() {
        return IadtController.get().getSourcesManager();
    }
}
