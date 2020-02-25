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

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.LiveInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.OSInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.external.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.ConsoleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

public class Home2Screen extends Screen {

    public Home2Screen(ScreenManager manager) {
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

        //TODO: Home icon resize not working on first navigation
        /*Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               getScreenManager().getScreenLayer().toggleBackButton(false);
            }
        }, 100);*/
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        AppInfoDocumentGenerator appHelper = ((AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO));
        BuildInfoDocumentGenerator buildReporter = ((BuildInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.BUILD_INFO));

        String appMessage = appHelper.getFormattedVersionLong() + "\n"
                + "Build " + buildReporter.getBuildOverview() + "\n"
                + buildReporter.getRepositoryOverview();


        DocumentSectionData appData = new DocumentSectionData.Builder(appHelper.getAppName())
                .setIcon(R.string.gmd_touch_app)
                .setOverview("App")
                .setExpandable(false)
                .add(appMessage)
                .addButton(new RunButton("Info",
                        R.drawable.ic_info_white_24dp,
                        R.color.iadt_background,
                        new Runnable() {
                            @Override
                            public void run() {
                                OverlayService.performNavigation(InfoScreen.class, null);
                            }
                }))
                .addButton(new RunButton("Sources",
                        R.drawable.ic_local_library_white_24dp,
                        R.color.iadt_background,
                        new Runnable() {
                            @Override
                            public void run() {
                                OverlayService.performNavigation(SourcesScreen.class, null);
                            }
                })
                ).build();
        data.add(appData);


        LiveInfoDocumentGenerator liveHelper = ((LiveInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.LIVE_INFO));
        String liveMessage = liveHelper.getOverview();

        DocumentSectionData runningData = new DocumentSectionData.Builder("Currently running")
                .setIcon(R.string.gmd_live_tv)
                .setOverview("Live")
                .setExpandable(false)
                .add(liveMessage)
                /*.addButton(new RunButton("Info", R.drawable.ic_info_white_24dp, new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(InfoScreen.class, null);
                    }
                }))*/
                .addButton(new RunButton("View",
                        R.drawable.ic_view_carousel_white_24dp,
                        R.color.iadt_background,
                        new Runnable() {
                            @Override
                            public void run() { OverlayService.performNavigation(InspectViewScreen.class); }
                }))
                .addButton(new RunButton("Logs",
                        R.drawable.ic_format_align_left_white_24dp,
                        R.color.iadt_background,
                        new Runnable() {
                            @Override
                            public void run() { OverlayService.performNavigation(LogScreen.class); }
                }))
                .addButton(new RunButton("Storage",
                        R.drawable.ic_storage_white_24dp,
                        R.color.iadt_background,
                        new Runnable() {
                            @Override
                            public void run() {
                            //OverlayService.performNavigation(StorageScreen.class);
                            Home2Screen.this.getScreenManager().hide();
                            PandoraBridge.storage();
                            }
                }))
                .build();
        data.add(runningData);


        DeviceInfoDocumentGenerator deviceHelper = ((DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.DEVICE_INFO));
        OSInfoDocumentGenerator osHelper = ((OSInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.OS_INFO));

        String deviceMessage = deviceHelper.getFormattedDevice()
                + Humanizer.newLine()
                + osHelper.getFirstLineOverview();

        DocumentSectionData deviceData = new DocumentSectionData.Builder(deviceHelper.getFirstLineOverview())
                .setIcon(R.string.gmd_phone_android)
                .setOverview("Device")
                .setExpandable(false)
                .add(deviceMessage)
                .addButton(new RunButton("Info",
                        R.drawable.ic_info_white_24dp,
                        R.color.iadt_background,
                        new Runnable() {
                            @Override
                            public void run() {
                                OverlayService.performNavigation(InfoScreen.class, null);
                            }
                }))
                .addButton(new RunButton("Console",
                        R.drawable.ic_computer_white_24dp,
                        R.color.iadt_background,
                        new Runnable() {
                            @Override
                            public void run() { OverlayService.performNavigation(ConsoleScreen.class);
                            }
                        })
                ).build();
        data.add(deviceData);


/*        data.add(new RunButton("Info",
                R.drawable.ic_info_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(InfoOverviewScreen.class);
                    }
                }));
        
        RunButton sources = new RunButton("Sources",
                R.drawable.ic_code_white_24dp,
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

        */

        data.add(new RunButton("Run",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(RunScreen.class);
                    }
                }));

        data.add(new RunButton("Report",
                R.drawable.ic_send_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ReportScreen.class);
                    }
                }));


        data.add(new RunButton("More",
                R.drawable.ic_more_vert_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(MoreScreen.class);
                    }
                }));

        return data;
    }

    private void initAdapter(List<Object> data) {
        FlexibleAdapter adapter = new FlexibleAdapter(2, data);
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
