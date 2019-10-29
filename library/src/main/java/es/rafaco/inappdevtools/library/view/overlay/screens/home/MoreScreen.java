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

import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;

public class MoreScreen extends Screen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public MoreScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "More";
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

        data.add("Playgrounds and old screens (pending to remove):\n" +
                " - Home 2 is a test\n" +
                " - All other items has been mixed at Log Screen\n");

        data.add(new RunButton("Home 2",
                R.drawable.ic_format_list_bulleted_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(Home2Screen.class);
                    }
                }));

        /*data.add(new RunButton("Analysis",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(AnalysisScreen.class);
                    }
                }));*/


        data.add(new RunButton("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(NetworkScreen.class);
                    }
                }));


        data.add(new RunButton("Screens",
                R.drawable.ic_photo_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ScreenshotsScreen.class);
                    }
                }));

        data.add(new RunButton("Errors",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ErrorsScreen.class);
                    }
                }));

        data.add(new RunButton("Logcat",
                R.drawable.ic_android_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(LogcatScreen.class);
                    }
                }));

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(3, data);
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
