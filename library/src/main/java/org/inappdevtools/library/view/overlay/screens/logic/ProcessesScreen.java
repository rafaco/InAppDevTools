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

package org.inappdevtools.library.view.overlay.screens.logic;

import android.app.ActivityManager;

import org.inappdevtools.library.logic.documents.data.DocumentSectionData;
import org.inappdevtools.library.logic.utils.RunningProcessesUtils;
import org.inappdevtools.library.storage.files.IadtPath;
import org.inappdevtools.library.view.components.items.ButtonFlexData;
import org.inappdevtools.library.view.components.items.OverviewData;
import org.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;

public class ProcessesScreen extends AbstractFlexibleScreen {

    public ProcessesScreen(ScreenManager manager) {
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

        data.add(new OverviewData("Processes",
                "Linux processes created by this application",
                R.string.gmd_developer_board,
                R.color.rally_white));

        List<ActivityManager.RunningAppProcessInfo> runningItems = RunningProcessesUtils.getList();

        if (runningItems.isEmpty()){
            String title = "No running processes, that's weird :(";
            DocumentSectionData.Builder noServiceDataBuilder = new DocumentSectionData.Builder(title)
                    //.setIcon(R.string.gmd_view_carousel)
                    .setExpandable(false);
            data.add(noServiceDataBuilder.build());
        }
        else{
            for (ActivityManager.RunningAppProcessInfo info : runningItems) {

                String title = RunningProcessesUtils.getTitle(info);
                String content = RunningProcessesUtils.getContent(info);
                DocumentSectionData.Builder serviceDataBuilder = new DocumentSectionData.Builder(title)
                        //.setIcon(R.string.gmd_view_carousel)
                        .setExpandable(false)
                        .add(content);

                data.add(serviceDataBuilder.build());
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
}
