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
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.utils.RunningThreadGroupsUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningThreadsUtils;
import es.rafaco.inappdevtools.library.view.components.cards.HeaderIconFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.RecyclerGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.CollapsibleFlexData;
import es.rafaco.inappdevtools.library.view.components.items.LinkItemData;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.components.listener.OnlyOneExpandedListener;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ThreadsScreen extends AbstractFlexibleScreen {

    private OnlyOneExpandedListener helper;

    public ThreadsScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getSpanCount() {
        return 1;
    }

    @Override
    protected void onAdapterStart() {
        helper = new OnlyOneExpandedListener(flexAdapter);
        helper.setExpandedPosition(3);
        List<Object> flexibleData = getFlexibleData();
        helper.updateDataWithExpandedState(flexibleData);
        updateAdapter(flexibleData);
    }

    private List<Object> getFlexibleData() {
        List<Object> data = new ArrayList<>();

        data.add(new OverviewData("Threads",
                "Linux threads of execution",
                R.string.gmd_line_style,
                R.color.rally_white));

        List<ThreadGroup> runningGroupsItems = RunningThreadGroupsUtils.getList();
        if (runningGroupsItems.isEmpty()){
            String title = "No running thread groups, that's weird :(";
            DocumentSectionData.Builder noServiceDataBuilder = new DocumentSectionData.Builder(title)
                    .setExpandable(false);
            data.add(noServiceDataBuilder.build());
            return data;
        }


        for (ThreadGroup info : runningGroupsItems) {
            String title = RunningThreadGroupsUtils.getTitle(info);
            title = Humanizer.toCapitalCase(title) + " group";
            String content = RunningThreadGroupsUtils.getContent(info);
            DocumentSectionData.Builder groupDataBuilder = new DocumentSectionData.Builder(title)
                    .setIcon(R.string.gmd_developer_board)
                    .setOverview("Info")
                    .add(content)
                    .setExpanded(true);

            List<Object> internalData = new ArrayList<>();
            List<Thread> runningItems = RunningThreadsUtils.getThreadsFromGroup(info);
            for (Thread thread : runningItems) {
                internalData.add(new LinkItemData(
                        RunningThreadsUtils.formatOneLine(thread),
                        RunningThreadsUtils.formatOneLineOverview(thread),
                        -1,
                        RunningThreadsUtils.getColor(thread),
                        new Runnable() {
                            @Override
                            public void run() {
                                //TODO
                            }
                        }
                ));
            }

            HeaderIconFlexData collapsibleHeader = new HeaderIconFlexData.Builder("")
                    .setExpandable(true)
                    .setExpanded(false)
                    .setOverview(runningItems.size() + " threads")
                    .build();
            RecyclerGroupFlexData collapsibleContent = new RecyclerGroupFlexData();
            collapsibleContent.setChildren(internalData);
            collapsibleContent.setHorizontalMargin(true);

            CollapsibleFlexData collapsibleData = new CollapsibleFlexData(collapsibleHeader,
                    collapsibleContent, false);
            collapsibleContent.setHorizontalMargin(false);
            List<Object> internalDataList = new ArrayList<>();
            internalDataList.add(collapsibleData);
            groupDataBuilder.setInternalData(internalDataList);
            
            data.add(groupDataBuilder.build());
        }

        return data;
    }

    private SourcesManager getSourcesManager() {
        return IadtController.get().getSourcesManager();
    }
}
