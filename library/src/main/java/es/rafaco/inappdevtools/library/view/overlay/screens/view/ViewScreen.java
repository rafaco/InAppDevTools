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

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
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
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.view.components.composers.SecondaryButtonsComposer;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.logic.session.ActivityTracker;
import es.rafaco.inappdevtools.library.logic.session.FragmentTrack;
import es.rafaco.inappdevtools.library.logic.session.FragmentTracker;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.storage.files.utils.ScreenshotUtils;
import es.rafaco.inappdevtools.library.view.components.items.CollapsibleFlexData;
import es.rafaco.inappdevtools.library.view.components.items.HeaderDoubleFlexData;
import es.rafaco.inappdevtools.library.view.components.items.HeaderFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ImageData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.TasksScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ViewScreen extends AbstractFlexibleScreen {

    public ViewScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getSpanCount(){
        return 2;
    }
    
    @Override
    public boolean hasHorizontalMargin(){
        return false;
    }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onAdapterStart() {
        List<Object> data = initData();
        updateAdapter(data);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    protected void onResume() {

    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();
        ActivityTracker tracker = IadtController.get().getActivityTracker();

        OverviewData overviewData = new OverviewData("Current view",
                tracker.getCurrentName() + " on top",
                R.string.gmd_view_carousel,
                R.color.iadt_text_high);
        overviewData.setHorizontalMargin(true);
        data.add(overviewData);

        HeaderFlexData header = new HeaderFlexData("Layout");
        header.setFullSpan(false);
        header.setGravity(Gravity.CENTER);
        data.add(header);

        HeaderFlexData header2 = new HeaderFlexData("Output");
        header2.setFullSpan(false);
        header2.setGravity(Gravity.CENTER);
        data.add(header2);
        
        addVerticalButtons(data);
        addImage(data);

        HeaderFlexData header3 = new HeaderFlexData("Components");
        header3.setGravity(Gravity.CENTER);
        data.add(header3);
        
        final long currentActivityUuid = tracker.getCurrentHistory().uuid;
        addTasks(data);
        addActivity(data, tracker);
        addFragments(data, currentActivityUuid);
        data.add(getSecondaryButtonsList());

        return data;
    }

    private void addVerticalButtons(List<Object> data) {
        LinearGroupFlexData verticalButtons = new LinearGroupFlexData();
        verticalButtons.setFullSpan(false);
        verticalButtons.setHorizontalMargin(true);

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
        Bitmap bitmap = ScreenshotUtils.getBitmap(false);
        ImageData image = new ImageData(bitmap);
        //image.setHeight((int) UiUtils.getPixelsFromDp(getContext(), 10));
        image.setIcon(R.drawable.ic_zoom_out_map_white_24dp);
        image.setPerformer(new Runnable() {
            @Override
            public void run() {
                OverlayService.performNavigation(ZoomScreen.class);
            }
        });
        data.add(image);
    }

    private void addTasks(List<Object> data) {
        CardGroupFlexData cardData = new CardGroupFlexData();
        cardData.setFullSpan(true);
        cardData.setBgColorResource(R.color.iadt_surface_top);
        cardData.setElevationDp(6);
        cardData.setPerformer(new Runnable() {
            @Override
            public void run() {
                OverlayService.performNavigation(TasksScreen.class);
            }
        });

        HeaderDoubleFlexData headerData = new HeaderDoubleFlexData(
                "Tasks",
                "Stacks of activities",
                R.string.gmd_layers,
                null);
        headerData.setNavCount(RunningTasksUtils.getCount());
        cardData.add(headerData);
        data.add(cardData);
    }

    private void addActivity(List<Object> data, ActivityTracker tracker) {
        String activityOverview = "";
        activityOverview += Humanizer.newLine();
        activityOverview += "Last event: " + tracker.getCurrentActivityLastEvent();
        activityOverview += Humanizer.newLine();
        activityOverview += "Pkg: " + tracker.getCurrentActivityPackage();
        activityOverview += Humanizer.newLine();
        activityOverview += "Instances this session: " + tracker.getCurrentActivityInstanceCount();
        activityOverview += Humanizer.newLine();
        activityOverview += "Time on first creation: " + tracker.getCurrentActivityStartupTime()
                + " ("+ tracker.getCurrentActivityCreationElapsed() + ")";


        CardGroupFlexData cardData = new CardGroupFlexData();
        cardData.setFullSpan(true);
        cardData.setBgColorResource(R.color.iadt_surface_top);
        cardData.setElevationDp(6);

        HeaderDoubleFlexData headerData = new HeaderDoubleFlexData(
                tracker.getCurrentName(),
                "Activity",
                R.string.gmd_view_carousel,
                null);

        LinearGroupFlexData collapsedList = new LinearGroupFlexData();
        TextFlexData contentData = new TextFlexData(activityOverview);
        contentData.setSize(TextFlexData.Size.LARGE);
        contentData.setHorizontalMargin(true);
        collapsedList.add(contentData);

        LinearGroupFlexData buttonList = new LinearGroupFlexData();
        buttonList.setHorizontal(true);

        final String activitySrcPath = getSourcesManager()
                .getPathFromClassName(tracker.getCurrentHistory().className);
        String activitySrcFile = Humanizer.getLastPart(activitySrcPath, "/");
        if (TextUtils.isEmpty(activitySrcFile)){
            buttonList.add(new ButtonBorderlessFlexData("Unavailable",
                    R.drawable.ic_code_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            Iadt.buildMessage("Activity source not found")
                                    .isInfo().fire();
                        }
                    }));
        }
        else{
            buttonList.add(new ButtonBorderlessFlexData(activitySrcFile,
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
            buttonList.add(new ButtonBorderlessFlexData("Unavailable",
                    R.drawable.ic_code_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            Iadt.buildMessage("Layout xml not found")
                                    .isInfo().fire();
                        }
                    })
            );
        }
        else{
            buttonList.add(new ButtonBorderlessFlexData(activityResFile,
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
        collapsedList.add(buttonList);

        CollapsibleFlexData collapsibleData = new CollapsibleFlexData(headerData,
                collapsedList, false);
        cardData.add(collapsibleData);

        data.add(cardData);
    }

    private void addFragments(List<Object> data, final long currentActivityUuuid) {
        //Fragments
        FragmentTracker fragmentTracker = IadtController.get().getFragmentTracker();
        LinkedHashMap<Long, FragmentTrack> fragmentHistory = fragmentTracker.getCurrentActivityHistory();
        boolean hasFragments = (fragmentHistory != null && fragmentHistory.size() > 0);
        if (!hasFragments){
            DocumentSectionData.Builder fragmentsDataBuilder = new DocumentSectionData.Builder("No Fragments")
                    .setExpandable(false)
                    .add("Current activity don't use fragments");
            data.add(fragmentsDataBuilder.build());
        }
        else {
            String fragmentOverview = fragmentHistory.values().iterator().next().name;
            if (fragmentHistory.size() > 1) {
                fragmentOverview += " and " + (fragmentHistory.size()-1) + " more";
            }

            CardGroupFlexData cardData = new CardGroupFlexData();
            cardData.setFullSpan(true);
            cardData.setBgColorResource(R.color.iadt_surface_top);
            cardData.setElevationDp(6);
            cardData.setPerformer(new Runnable() {
                @Override
                public void run() {
                    OverlayService.performNavigation(FragmentsScreen.class,
                            currentActivityUuuid + "");
                }
            });

            HeaderDoubleFlexData headerData = new HeaderDoubleFlexData(
                    fragmentOverview,
                    "Fragments",
                    R.string.gmd_extension,
                    null);
            headerData.setNavCount(fragmentHistory.size());
            cardData.add(headerData);
            data.add(cardData);
        }
    }

    private LinearGroupFlexData getSecondaryButtonsList() {
        final long currentSessionUid = IadtController.get().getSessionManager().getCurrentUid();
        SecondaryButtonsComposer composer = new SecondaryButtonsComposer("Related");
        composer.setHorizontalMargins(true);
        composer.add("Logs: Navigation Events",
                R.string.gmd_location_on,
                R.color.iadt_text_high,
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
                });
        composer.add("Logs: Lifecycle Events",
                R.string.gmd_format_align_left,
                R.color.iadt_text_high,
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
                });
        return composer.compose();
    }

    private SourcesManager getSourcesManager() {
        return IadtController.get().getSourcesManager();
    }
}
