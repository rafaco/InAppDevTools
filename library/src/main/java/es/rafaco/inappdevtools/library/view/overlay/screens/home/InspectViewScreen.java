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
import android.widget.TextView;
//#endif

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

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

        data.add("Activity");

        String viewOverview = "";
        viewOverview += "Top activity is " + RunningTasksUtils.getTopActivity();
        viewOverview += Humanizer.newLine();
        viewOverview += RunningTasksUtils.getCount() + " tasks with " + RunningTasksUtils.getActivitiesCount() + " activities";
        viewOverview += Humanizer.newLine();
        viewOverview += "App on " + RunningTasksUtils.getTopActivityStatus();

        data.add(new CardData(RunningTasksUtils.getTopActivity(),
                viewOverview,
                R.string.gmd_view_carousel,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(InfoScreen.class, "0");
                    }
                }));


        String topActivityName = RunningTasksUtils.getTopActivity();
        final String pathToActivitySource = IadtController.get().getSourcesManager()
                .getPathFromClassName(RunningTasksUtils.getTopActivityClassName());
        if (!TextUtils.isEmpty(pathToActivitySource)) {

            data.add(new RunButton("SRC", //topActivityName,
                    R.drawable.ic_code_white_24dp, new Runnable() {
                @Override
                public void run() {
                    OverlayService.performNavigation(SourceDetailScreen.class,
                            SourceDetailScreen.buildParams(pathToActivitySource, -1));
                }
            }));

            String layoutName = getActivityLayoutName(pathToActivitySource);
            final String pathToLayout = getActivityLayoutPath(layoutName);
            data.add(new RunButton("RES", //TextUtils.isEmpty(layoutName) ? "Layout" : layoutName,
                    R.drawable.ic_code_white_24dp, new Runnable() {
                @Override
                public void run() {

                    if (TextUtils.isEmpty(pathToLayout))
                        Iadt.showMessage("Layout xml not found");
                    else
                        OverlayService.performNavigation(SourceDetailScreen.class,
                            SourceDetailScreen.buildParams(pathToLayout, -1));
                }
            }));
        }

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

        data.add(new RunButton("Show gridline",
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
