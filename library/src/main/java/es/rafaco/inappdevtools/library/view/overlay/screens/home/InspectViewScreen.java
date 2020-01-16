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

import android.text.TextUtils;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.events.detectors.device.OrientationEventDetector;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentEntryData;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

import static es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils.getTopActivityInfo;

public class InspectViewScreen extends Screen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public InspectViewScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Inspect View";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
    }
    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        String activityOverview = "";
        List<DocumentEntryData> topActivityInfo = getTopActivityInfo();
        for (DocumentEntryData info : topActivityInfo) {
            activityOverview += info.getLabel() + ": " + info.getValues().get(0);
            activityOverview += Humanizer.newLine();
        }
        activityOverview += "App on " + RunningTasksUtils.getTopActivityStatus();
        activityOverview += " in " + OrientationEventDetector.getOrientationString();
        activityOverview += Humanizer.newLine();
        activityOverview += RunningTasksUtils.getCount() + " tasks with " + RunningTasksUtils.getActivitiesCount() + " activities";
        activityOverview += Humanizer.newLine();

        DocumentSectionData.Builder activityDataBuilder = new DocumentSectionData.Builder(RunningTasksUtils.getTopActivity())
                .setIcon(R.string.gmd_view_carousel)
                .setOverview("Activity")
                .setExpandable(false)
                .add(activityOverview);

        final String pathToActivitySource = IadtController.get().getSourcesManager()
                .getPathFromClassName(RunningTasksUtils.getTopActivityClassName());

        activityDataBuilder.addButton(new RunButton("ACTIVITY SRC",
                R.drawable.ic_local_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(pathToActivitySource))
                            Iadt.showMessage("Activity source not found");
                        else
                        OverlayService.performNavigation(SourceDetailScreen.class,
                                SourceDetailScreen.buildParams(pathToActivitySource, -1));
                    }
                }));

        String layoutName = getActivityLayoutName(pathToActivitySource);
        final String pathToLayout = getActivityLayoutPath(layoutName);
        activityDataBuilder.addButton(new RunButton("LAYOUT RES",
                R.drawable.ic_local_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(pathToLayout))
                            Iadt.showMessage("Layout xml not found");
                        else
                            OverlayService.performNavigation(SourceDetailScreen.class,
                                    SourceDetailScreen.buildParams(pathToLayout, -1));
                    }
                })
        );
        data.add(activityDataBuilder.build());

        DocumentSectionData.Builder fragmentsDataBuilder = new DocumentSectionData.Builder("Fragments")
                .setIcon(R.string.gmd_extension)
                .setOverview("Fragments")
                .setExpandable(false)
                .add("Coming soon: list, states and navigation to sources");

        data.add(fragmentsDataBuilder.build());


        data.add("Layout inspector");

        data.add(new RunButton("Select element",
                R.drawable.ic_touch_app_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        InspectViewScreen.this.getScreenManager().hide();
                        PandoraBridge.select();
                    }
                }));

        data.add(new RunButton("Browse hierarchy",
                R.drawable.ic_layers_white_24dp, new Runnable() {
            @Override
            public void run() {
                InspectViewScreen.this.getScreenManager().hide();
                PandoraBridge.hierarchy();
            }
        }));

        data.add(new RunButton("Take Measure",
                R.drawable.ic_format_line_spacing_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        InspectViewScreen.this.getScreenManager().hide();
                        PandoraBridge.measure();
                    }
                }));

        data.add(new RunButton("Show grid",
                R.drawable.ic_grid_on_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        PandoraBridge.grid();
                    }
                }));



        data.add("Others");

        data.add(new RunButton( "Take Screenshot",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().takeScreenshot();
                    }
                }));


        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(2, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    public String getActivityLayoutName(String pathToActivitySource) {
        String content = IadtController.get().getSourcesManager().getContent(pathToActivitySource);
        if (TextUtils.isEmpty(content))
            return "";

        Pattern pattern = Pattern.compile("setContentView\\(R\\.layout\\.(\\w+)\\)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public String getActivityLayoutPath(String layoutName) {
        if (TextUtils.isEmpty(layoutName))
            return "";

        return IadtPath.RESOURCES + "/" + "layout" + "/" + layoutName + ".xml";
    }
}
