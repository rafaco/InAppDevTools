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

package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.DeviceInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.OSInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.RepoInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.external.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.logic.utils.RunningProcessesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningThreadsUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummaryDao;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.cards.WideWidgetData;
import es.rafaco.inappdevtools.library.view.components.cards.WidgetData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.app.AppScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.device.DeviceScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.history.HistoryScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logic.LogicScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceCodeScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.view.ViewScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class HomeScreen extends AbstractFlexibleScreen {

    private TimerTask updateTimerTask;
    private Timer updateTimer;

    public HomeScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.library_name);
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    public FlexAdapter.Layout getLayout(){
        return FlexAdapter.Layout.GRID;
    }

    @Override
    protected void onStart(ViewGroup bodyView) {
        super.onStart(bodyView);

        //TODO: Home icon resize not working on first navigation
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getScreenManager().getScreenLayer().toggleBackButton(false);
            }
        }, 100);
    }

    @Override
    protected void onAdapterStart() {
        setFullWidthSolver(new FlexAdapter.FullWidthSolver() {
            @Override
            public Boolean isFullWidth(int position) {
                return false;//position == 0;
            }
        });
        updateAdapter(getFlexibleData());
    }

    private List<Object> getFlexibleData() {
        List<Object> data = new ArrayList<>();

        AppInfoDocumentGenerator appHelper = ((AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO));
        BuildInfoDocumentGenerator buildReporter = ((BuildInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.BUILD_INFO));
        RepoInfoDocumentGenerator repoReporter = ((RepoInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.REPO_INFO));
        DeviceInfoDocumentGenerator deviceHelper = ((DeviceInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.DEVICE_INFO));
        OSInfoDocumentGenerator osHelper = ((OSInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.OS_INFO));
        ConfigManager configManager = IadtController.get().getConfig();

        String teamName = configManager.getString(BuildConfigField.TEAM_NAME);
        if (TextUtils.isEmpty(teamName))
            teamName = "Resources";
        WideWidgetData teamData = (WideWidgetData) new WideWidgetData.Builder("Team")
                .setIcon(R.string.gmd_people)
                .setMainContent(teamName)
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(TeamScreen.class);
                    }
                })
                .build();
        data.add(teamData);

        int sessionCount = IadtDatabase.get().sessionDao().count();
        int buildCount = IadtDatabase.get().buildDao().count();
        WidgetData historyData = new WidgetData.Builder("History")
                .setIcon(R.string.gmd_history)
                .setMainContent(Humanizer.plural(sessionCount, "Session"))
                .setSecondContent(Humanizer.plural(buildCount, "Build"))
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(HistoryScreen.class);
                    }
                })
                .build();
        data.add(historyData);

        WidgetData appData = new WidgetData.Builder("App")
                .setIcon(R.string.gmd_apps)
                .setMainContent(appHelper.getAppName())
                .setSecondContent(appHelper.getFormattedVersionShort()
                        + " " + buildReporter.getFriendlyBuildType())
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(AppScreen.class);
                    }
                })
                .build();
        data.add(appData);

        WidgetData deviceData = new WidgetData.Builder("Device")
                .setIcon(R.string.gmd_phone_android)
                .setMainContent(osHelper.getOneLineOverview())
                .setSecondContent(deviceHelper.getFormattedDevice())
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(DeviceScreen.class);
                    }
                })
                .build();
        data.add(deviceData);


        AssetManager assets = getContext().getAssets();
        String[] list = new String[0];
        try {
            list = assets.list("");
        } catch (IOException e) {
            FriendlyLog.logException("Error counting assets", e);
        }
        String repoMain, repoSecond;
        boolean isGitInfo = repoReporter.isGitEnabled();
        if (!isGitInfo){
            repoMain = "No Git repo";
            repoSecond = list.length + " assets";
        }
        else{
            repoMain = repoReporter.getWidgetMainText();
            repoSecond = repoReporter.getWidgetSecondText();
        }
        WidgetData sourcesData = new WidgetData.Builder("Sources")
                .setIcon(R.string.gmd_code)
                .setMainContent(repoMain)
                .setSecondContent(repoSecond)
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SourceCodeScreen.class);
                    }
                })
                .build();
        data.add(sourcesData);

        WidgetData storageData = new WidgetData.Builder("Storage")
                .setIcon(R.string.gmd_storage)
                //TODO: it fails on lollipop 1
                //.setMainContent(InternalFileReader.getTotalSizeFormatted())
                .setSecondContent("3 DB, 4 SP & 1234 files")
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        HomeScreen.this.getScreenManager().hide();
                        PandoraBridge.storage();
                    }
                })
                .build();
        data.add(storageData);

        
        WidgetData viewData = new WidgetData.Builder("View")
                .setIcon(R.string.gmd_view_carousel)
                .setMainContent(IadtController.get().getActivityTracker().getCurrentName())
                .setSecondContent("3 fragments")
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(ViewScreen.class);
                    }
                })
                .build();
        data.add(viewData);

        WidgetData jvmData = new WidgetData.Builder("Logic")
                .setIcon(R.string.gmd_location_city)
                .setMainContent(RunningThreadsUtils.getCount() + " threads")
                .setSecondContent(RunningProcessesUtils.getCount() + " process and " + RunningTasksUtils.getCount() + " tasks")
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(LogicScreen.class);
                    }
                })
                .build();
        data.add(jvmData);


        int logsCount = IadtDatabase.get().friendlyDao().count(); //TODO: filter by session
        WidgetData logsData = new WidgetData.Builder("Logs")
                .setIcon(R.string.gmd_sort)
                .setMainContent(Humanizer.plural(logsCount, "log"))
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(LogScreen.class);
                    }
                })
                .build();
        data.add(logsData);

        NetSummaryDao netSummaryDao = IadtDatabase.get().netSummaryDao();
        long currentSession = IadtController.get().getSessionManager().getCurrentUid();
        int netCount = netSummaryDao.countBySession(currentSession);
        long netSize = netSummaryDao.sizeBySession(currentSession);
        int netErrors = netSummaryDao.countBySessionAndStatus(currentSession, NetSummary.Status.ERROR);
        WidgetData networkData = new WidgetData.Builder("Network")
                .setIcon(R.string.gmd_filter_drama)
                .setMainContent(Humanizer.humanReadableByteCount(netSize, false))
                .setSecondContent(netCount +" req. and "
                        + Humanizer.plural(netErrors, "error"))
                .setPerformer(new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(NetScreen.class);
                    }
                })
                .build();
        data.add(networkData);

        
        if (isDebug()){
            data.add(new ButtonFlexData("More",
                    R.drawable.ic_more_vert_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() { OverlayService.performNavigation(MoreScreen.class);
                        }
                    }));
        }

        return data;
    }


    //region [ UPDATER ]

    @Override
    protected void onPause() {
        cancelTimerTask();
    }

    @Override
    protected void onResume() {
        startUpdateTimer();
    }

    @Override
    protected void onStop() {
        //Deliberately empty
    }

    @Override
    protected void onDestroy() {
        cancelTimerTask();
    }

    //endregion

    //region [ UPDATE TIMER ]

    private void startUpdateTimer() {
        if (updateTimerTask!=null){
            destroyTimer();
        }
        updateTimerTask = new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateAdapter(getFlexibleData());
                        //if (isDebug()) Log.v(Iadt.TAG, "Home3Screen updated");
                        startUpdateTimer();
                    }
                });
            }
        };
        updateTimer = new Timer("Iadt-HomeUpdate-Timer", false);
        updateTimer.schedule(updateTimerTask, 5 * 1000L);
    }


    private void cancelTimerTask() {
        if (updateTimerTask!=null){
            updateTimerTask.cancel();
            updateTimerTask = null;
        }
    }

    private void destroyTimer() {
        cancelTimerTask();
        if (updateTimer!=null){
            updateTimer.cancel();
            updateTimer.purge();
            updateTimer = null;
        }
    }

    //endregion
}
