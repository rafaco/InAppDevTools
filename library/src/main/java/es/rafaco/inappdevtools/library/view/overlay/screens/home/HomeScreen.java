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

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.external.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.device.TerminalScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoOverviewScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.view.ViewScreen;

public class HomeScreen extends Screen {

    public HomeScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.library_name);
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        AppInfoDocumentGenerator appHelper = ((AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO));
        DeviceInfoDocumentGenerator deviceHelper = ((DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.DEVICE_INFO));

        String welcome = appHelper.getFormattedAppLong() + "\n"
                + deviceHelper.getFormattedDeviceLong();
        data.add(welcome);

        data.add(new RunButton("Info",
                R.drawable.ic_info_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(InfoOverviewScreen.class);
                    }
                }));

        data.add(new RunButton("Run",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(RunScreen.class);
                    }
                }));

        data.add(new RunButton("Report",
                R.drawable.ic_send_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ReportScreen.class);
                    }
                }));

        data.add("Inspect");
        data.add(new RunButton("Build",
                R.drawable.ic_build_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        long currentSessionId = IadtController.get().getSessionManager()
                                .getCurrentUid();
                        OverlayService.performNavigation(BuildDetailScreen.class,
                                currentSessionId + "");
                    }
                }));

        data.add(new RunButton("Session",
                R.drawable.ic_timeline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        long currentSessionId = IadtController.get().getSessionManager()
                                .getCurrentUid();
                        OverlayService.performNavigation(SessionDetailScreen.class,
                                currentSessionId + "");
                    }
                }));

        RunButton sources = new RunButton("Sources",
                R.drawable.ic_local_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SourcesScreen.class);
                    }
                });
        if (!IadtController.get().getSourcesManager().canSourceInspection()){
            sources.setColor(R.color.rally_gray);
            sources.setPerformer(new Runnable() {
                @Override
                public void run() {
                    Iadt.showMessage("Source inspection is DISABLED");
                }
            });
        }
        data.add(sources);

        
        data.add(new RunButton("View",
                R.drawable.ic_view_carousel_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ViewScreen.class);
                    }
                }));

        data.add(new RunButton("Logs",
                R.drawable.ic_format_align_left_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(LogScreen.class);
                    }
                }));

        data.add(new RunButton("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(NetScreen.class);
                    }
                }));

        data.add(new RunButton("Storage",
                R.drawable.ic_storage_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        //OverlayService.performNavigation(StorageScreen.class);
                        HomeScreen.this.getScreenManager().hide();
                        PandoraBridge.storage();
                    }
                }));

        data.add(new RunButton("Terminal",
                R.drawable.ic_computer_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(TerminalScreen.class);
                    }
                }));



        data.add("");
        data.add(new RunButton("Builds",
                R.drawable.ic_build_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BuildsScreen.class);
                    }
                }));

        data.add(new RunButton("Sessions",
                R.drawable.ic_timeline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SessionsScreen.class);
                    }
                }));

        data.add(new RunButton("More",
                R.drawable.ic_more_vert_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(MoreScreen.class);
                    }
                }));

        return data;
    }

    private void initAdapter(List<Object> data) {
        FlexibleAdapter adapter = new FlexibleAdapter(FlexibleAdapter.Layout.GRID, 3, data);
        RecyclerView recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }
}
