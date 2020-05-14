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

package es.rafaco.inappdevtools.library.view.overlay.screens.view;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.external.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.log.filter.LogUiFilter;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.logic.session.ActivityTracker;
import es.rafaco.inappdevtools.library.logic.session.FragmentTrack;
import es.rafaco.inappdevtools.library.logic.session.FragmentTracker;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.storage.files.utils.ScreenshotUtils;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.items.HeaderFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ImageData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class ViewScreen extends Screen {

    private FlexAdapter adapter;
    private RecyclerView recyclerView;

    public ViewScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "View";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {

    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    protected void onResume() {
        List<Object> data = initData();
        initAdapter(data);
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexAdapter(FlexAdapter.Layout.GRID, 2, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        data.add(new OverviewData("Current view",
                null,
                R.string.gmd_visibility,
                R.color.iadt_text_high));

        data.add(new HeaderFlexData("Layout inspector"));
        addVerticalButtons(data);
        addImage(data);

        data.add(new HeaderFlexData("Components"));
        ActivityTracker tracker = IadtController.get().getActivityTracker();
        final long currentActivityUuuid = tracker.getCurrentHistory().uuid;
        addActivity(data, tracker);
        addFragments(data, currentActivityUuuid);
        addLogButtons(data);

        return data;
    }

    private void addVerticalButtons(List<Object> data) {
        LinearGroupFlexData verticalButtons = new LinearGroupFlexData();
        verticalButtons.setFullSpan(false);

        verticalButtons.add(new ButtonFlexData("Inspect by touch",
                R.drawable.ic_touch_app_white_24dp,
                R.color.rally_green_alpha,
                new Runnable() {
                    @Override
                    public void run() {
                        ViewScreen.this.getScreenManager().hide();
                        PandoraBridge.select();
                    }
                }));
        verticalButtons.add(new ButtonFlexData("Browse hierarchy",
                        R.drawable.ic_layers_white_24dp, new Runnable() {
                    @Override
                    public void run() {
                        ViewScreen.this.getScreenManager().hide();
                        PandoraBridge.hierarchy();
                    }
                }));

        LinearGroupFlexData horizontalGroup = new LinearGroupFlexData();
        horizontalGroup.setHorizontal(true);
        horizontalGroup.add(new ButtonFlexData("Rule",
                R.drawable.ic_format_line_spacing_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        ViewScreen.this.getScreenManager().hide();
                        PandoraBridge.measure();
                    }
                }));
        horizontalGroup.add(new ButtonFlexData("Grid",
                R.drawable.ic_grid_on_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        PandoraBridge.grid();
                    }
                }));
        verticalButtons.add(horizontalGroup);

        data.add(verticalButtons);
    }

    private void addImage(List<Object> data) {
        Bitmap bitmap = ScreenshotUtils.getBitmap();
        ImageData image = new ImageData(bitmap);
        //image.setHeight((int) UiUtils.getPixelsFromDp(getContext(), 10));
        image.setIcon(R.drawable.ic_add_a_photo_white_24dp);
        image.setPerformer(new Runnable() {
            @Override
            public void run() {
                IadtController.get().takeScreenshot();
            }
        });
        data.add(image);
    }

    private void addActivity(List<Object> data, ActivityTracker tracker) {
        String activityOverview = "";
        activityOverview += "Creation time: " + tracker.getCurrentActivityStartupTime()
                + " ("+ tracker.getCurrentActivityCreationElapsed() + ")";
        activityOverview += Humanizer.newLine();
        activityOverview += "Last event: " + tracker.getCurrentActivityLastEvent();
        activityOverview += Humanizer.newLine();
        activityOverview += "Pkg: " + tracker.getCurrentActivityPackage();
        activityOverview += Humanizer.newLine();
        activityOverview += "Instances this session: " + tracker.getCurrentActivityInstanceCount();

        DocumentSectionData.Builder activityDataBuilder = new DocumentSectionData.Builder(tracker.getCurrentName())
                .setIcon(R.string.gmd_view_carousel)
                .setOverview(tracker.getCurrentHistory().getFormattedLastEvent())
                .setExpandable(true)
                .setExpanded(false)
                .add(activityOverview);

        final String activitySrcPath = getSourcesManager()
                .getPathFromClassName(tracker.getCurrentHistory().className);
        String activitySrcFile = Humanizer.getLastPart(activitySrcPath, "/");
        if (TextUtils.isEmpty(activitySrcFile)){
            activityDataBuilder.addButton(new ButtonBorderlessFlexData("Unavailable",
                    R.drawable.ic_code_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            Iadt.showMessage("Activity source not found");
                        }
                    }));
        }
        else{
            activityDataBuilder.addButton(new ButtonBorderlessFlexData(activitySrcFile,
                    R.drawable.ic_code_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(SourceDetailScreen.class,
                                    SourceDetailScreen.buildSourceParams(activitySrcPath, -1));
                        }
                    }));
        }

        String activityLayoutName = getSourcesManager().getLayoutNameFromClassName(activitySrcPath);
        final String activityResPath = getSourcesManager().getLayoutPathFromLayoutName(activityLayoutName);
        String activityResFile = Humanizer.getLastPart(activityResPath, "/");
        if (TextUtils.isEmpty(activityResFile)) {
            activityDataBuilder.addButton(new ButtonBorderlessFlexData("Unavailable",
                    R.drawable.ic_code_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            Iadt.showMessage("Layout xml not found");
                        }
                    })
            );
        }
        else{
            activityDataBuilder.addButton(new ButtonBorderlessFlexData(activityResFile,
                    R.drawable.ic_code_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(SourceDetailScreen.class,
                                    SourceDetailScreen.buildSourceParams(activityResPath, -1));
                        }
                    })
            );
        }
        data.add(activityDataBuilder.build());
    }

    private void addFragments(List<Object> data, final long currentActivityUuuid) {
        //Fragments
        FragmentTracker fragmentTracker = IadtController.get().getFragmentTracker();
        LinkedHashMap<Long, FragmentTrack> currentActivityHistory = fragmentTracker.getCurrentActivityHistory();
        boolean hasFragments = (currentActivityHistory != null && currentActivityHistory.size() > 0);
        if (!hasFragments){
            DocumentSectionData.Builder fragmentsDataBuilder = new DocumentSectionData.Builder("No Fragments")
                    .setExpandable(false)
                    .add("Current activity don't use fragments");
            data.add(fragmentsDataBuilder.build());
        }
        else {
            String fragmentOverview = currentActivityHistory.values().iterator().next().name;
            if (currentActivityHistory.size() > 1) {
                fragmentOverview += " and " + (currentActivityHistory.size() - 1) + "more";
            }
            data.add(new CardData("Fragments",
                    fragmentOverview,
                    R.string.gmd_extension,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(FragmentsScreen.class,
                                    currentActivityUuuid + "");
                        }
                    }).setNavCount(currentActivityHistory.size()));
        }
    }

    private void addLogButtons(List<Object> data) {
        //Logs links (TODO)
        final long currentSessionUid = IadtController.get().getSessionManager().getCurrentUid();
        data.add(new ButtonFlexData(
                "Navigation",
                R.drawable.ic_location_on_white_24dp,
                R.color.rally_green_alpha,
                new Runnable() {
                    @Override
                    public void run() {
                        final LogFilterHelper filter = new LogFilterHelper(LogFilterHelper.Preset.REPRO_STEPS);
                        filter.setSessionById(currentSessionUid);
                        LogUiFilter uiFilter = filter.getUiFilter();
                        uiFilter.setTypeInt(1);
                        uiFilter.setCategoryInt(1);
                        uiFilter.setCategoryName("App");
                        uiFilter.setTagInt(1);
                        uiFilter.setTagName("Navigation");

                        OverlayService.performNavigation(LogScreen.class,
                                LogScreen.buildParams(filter.getUiFilter()));
                    }
                }));
        data.add(new ButtonFlexData(
                "Lifecycle",
                R.drawable.ic_format_align_left_white_24dp,
                R.color.rally_blue_med_alpha,
                new Runnable() {
                    @Override
                    public void run() {
                        final LogFilterHelper filter = new LogFilterHelper(LogFilterHelper.Preset.DEBUG);
                        filter.setSessionById(currentSessionUid);
                        LogUiFilter uiFilter = filter.getUiFilter();
                        uiFilter.setTypeInt(1);
                        uiFilter.setCategoryInt(1);
                        uiFilter.setCategoryName("Activity");

                        OverlayService.performNavigation(LogScreen.class,
                                LogScreen.buildParams(uiFilter));
                    }
                }));
    }

    private SourcesManager getSourcesManager() {
        return IadtController.get().getSourcesManager();
    }
}
