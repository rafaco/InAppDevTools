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

import android.text.TextUtils;
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
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.session.ActivityTrack;
import es.rafaco.inappdevtools.library.logic.session.ActivityTracker;
import es.rafaco.inappdevtools.library.logic.session.FragmentTrack;
import es.rafaco.inappdevtools.library.logic.session.FragmentTracker;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class FragmentsScreen extends Screen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public FragmentsScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
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

        long activityUUid = getActivityUuidFromParam();

        if (activityUUid<1){
            OverviewData noActivityOverview = new OverviewData("Activity param not found",
                    "",
                    R.string.gmd_extension,
                    R.color.rally_white);
            data.add(noActivityOverview);
            return data;
        }

        ActivityTracker activityTracker = IadtController.get().getActivityTracker();
        FragmentTracker fragmentTracker = IadtController.get().getFragmentTracker();
        ActivityTrack activityHistory = activityTracker.getHistory(activityUUid);
        LinkedHashMap<Long, FragmentTrack> fragmentHistory = fragmentTracker.getActivityHistory(activityUUid);

        boolean hasFragments = (fragmentHistory != null && fragmentHistory.size() > 0);
        if (!hasFragments){
            OverviewData noFragmentsOverview = new OverviewData("Activity " + activityHistory.name + " don't have fragments",
                    "",
                    R.string.gmd_extension,
                    R.color.rally_white);
            data.add(noFragmentsOverview);
        }
        else{

            String title = activityHistory.name + " Fragments ";
            OverviewData fragmentsOverview = new OverviewData(title,
                    "Total fragments: " + fragmentHistory.size(),
                    R.string.gmd_extension,
                    R.color.rally_white);
            data.add(fragmentsOverview);

            for (FragmentTrack track : fragmentHistory.values()) {

                //Card
                DocumentSectionData.Builder fragmentDataBuilder = new DocumentSectionData.Builder(track.name)
                        .setIcon(R.string.gmd_extension)
                        .setOverview(track.getFormattedLastEvent())
                        .setExpandable(true);

                //Source button
                final String srcPath = getSourcesManager().getPathFromClassName(track.className);
                String fileName = Humanizer.getLastPart(srcPath, "/");
                if (TextUtils.isEmpty(srcPath)){
                    fragmentDataBuilder.addButton(new RunButton("Unavailable",
                            R.drawable.ic_code_white_24dp,
                            new Runnable() {
                                @Override
                                public void run() {
                                    Iadt.showMessage("Activity source not found");
                                }
                            }));
                }
                else{
                    fragmentDataBuilder.addButton(new RunButton(fileName,
                            R.drawable.ic_code_white_24dp,
                            new Runnable() {
                                @Override
                                public void run() {
                                    OverlayService.performNavigation(SourceDetailScreen.class,
                                            SourceDetailScreen.buildSourceParams(srcPath, -1));
                                }
                            }));
                }

                //Layout button
                String layoutName = getSourcesManager().getLayoutNameFromClassName(srcPath);
                final String layoutPath = getSourcesManager().getLayoutPathFromLayoutName(layoutName);
                String layoutFile = Humanizer.getLastPart(layoutPath, "/");
                if (TextUtils.isEmpty(layoutPath)) {
                    fragmentDataBuilder.addButton(new RunButton("Unavailable",
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
                    fragmentDataBuilder.addButton(new RunButton(layoutFile,
                            R.drawable.ic_code_white_24dp,
                            new Runnable() {
                                @Override
                                public void run() {
                                    OverlayService.performNavigation(SourceDetailScreen.class,
                                            SourceDetailScreen.buildSourceParams(layoutPath, -1));
                                }
                            })
                    );
                }
                data.add(fragmentDataBuilder.build());
            }
        }
        return data;
    }

    private SourcesManager getSourcesManager() {
        return IadtController.get().getSourcesManager();
    }

    private long getActivityUuidFromParam() {
        try{
            return Long.parseLong(getParam());
        }
        catch (Exception e){
            return -1;
        }
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(FlexibleAdapter.Layout.GRID, 1, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
