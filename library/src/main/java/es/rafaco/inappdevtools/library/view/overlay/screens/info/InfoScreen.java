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

package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.os.Handler;
import android.os.Looper;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.rafaco.compat.AppCompatButton;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.view.components.flex.ComplexCardViewHolder;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.utils.ButtonUtils;

public class InfoScreen extends Screen {

    private Timer updateTimer;
    private TimerTask updateTimerTask;

    private RelativeLayout overviewView;
    private TextView overviewTitleView;
    private TextView overviewIconView;
    private TextView overviewContentView;

    private RecyclerView flexibleContents;
    private FlexibleAdapter adapter;
    private int infoReportIndex;
    private int expandedPosition;
    private AppCompatButton navIndex;
    private AppCompatButton navPrevious;
    private AppCompatButton navNext;

    public InfoScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_info_body; }

    @Override
    protected void onCreate() {
        //Deliberately empty
    }

    @Override
    protected void onStart(ViewGroup view) {
        overviewView = view.findViewById(R.id.overview);
        overviewContentView = view.findViewById(R.id.overview_content);
        overviewIconView = view.findViewById(R.id.overview_icon);
        overviewTitleView = view.findViewById(R.id.overview_title);
        navIndex = view.findViewById(R.id.info_nav_index);
        navPrevious = view.findViewById(R.id.info_nav_previous);
        navNext = view.findViewById(R.id.info_nav_next);
        flexibleContents = view.findViewById(R.id.flexible_contents);

        adapter = new FlexibleAdapter(1, new ArrayList<>());
        adapter.setScreen(this);
        flexibleContents.setAdapter(adapter);

        loadReport();
    }

    @Override
    protected void onPause() {
        cancelTimerTask();
    }

    @Override
    protected void onResume() {
        if (getInitialReportPosition() == 0){
            startUpdateTimer();
        }
    }

    @Override
    protected void onStop() {
        //Deliberately empty
    }

    @Override
    protected void onDestroy() {
        cancelTimerTask();
    }


    //region [ LOAD REPORT ]

    private void loadReport() {
        infoReportIndex = getInitialReportPosition();
        expandedPosition = getInitialExpandedPosition();
        initInfoNavigationButtons(infoReportIndex);

        InfoReportData data = getData(infoReportIndex);
        updateView(data);
    }

    private int getInitialReportPosition() {
        if (TextUtils.isEmpty(getParam())){
            return 0;
        }
        int paramPosition = Integer.parseInt(getParam());
        return paramPosition;
    }

    private InfoReportData getData(int reportPosition) {
        InfoReport report = InfoReport.values()[reportPosition];
        InfoReportData reportData = IadtController.get().getInfoManager().getReportData(report);
        reportData = updateDataWithExpandedState(reportData);
        return reportData;
    }

    public void updateView(InfoReportData reportData) {
        getScreenManager().setTitle(reportData.getTitle() + " Info");
        updateHeader(reportData);
        updateContents(reportData);
    }

    private void updateHeader(InfoReportData data) {
        overviewTitleView.setText(data.getTitle());

        if (data.getIcon()>0){
            IconUtils.markAsIconContainer(overviewIconView, IconUtils.MATERIAL);
            overviewIconView.setText(data.getIcon());
            overviewIconView.setVisibility(View.VISIBLE);
        }else{
            overviewIconView.setVisibility(View.GONE);
        }

        overviewContentView.setText(data.getOverview());
    }

    private void updateContents(InfoReportData data) {
        List<Object> objectList = new ArrayList<Object>(data.getGroups());
        adapter.replaceItems(objectList);
    }

    //endregion

    //region [ EXPAND/COLLAPSE ]

    private int getInitialExpandedPosition() {
        if (false){ //TODO: get expanded from params
            return -1;
        }
        return -1;
    }

    public boolean toggleExpandedPosition(int position){
        int previousPosition = expandedPosition;
        if (previousPosition == position){
            //Collapse currently selected
            expandedPosition = -1;
            return false;
        }
        else{
            if (previousPosition >= 0){
                //Collapse previously selected
                InfoGroupData previousData = (InfoGroupData) adapter.getItems().get(previousPosition);
                previousData.setExpanded(false);
                adapter.notifyItemChanged(previousPosition);
            }
            //Expand current selection
            expandedPosition = position;
            return true;
        }
    }

    private InfoReportData updateDataWithExpandedState(InfoReportData reportData) {
        for (int i = 0; i < reportData.getGroups().size(); i++) {
            boolean isExpanded = (i==expandedPosition);
            reportData.getGroups().get(i).setExpanded(isExpanded);
        }
        return reportData;
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
                        updateView(getData(infoReportIndex));
                        startUpdateTimer();
                    }
                });
            }
        };
        updateTimer = new Timer("Iadt-InfoUpdate-Timer", false);
        updateTimer.schedule(updateTimerTask, 5 * 1000);
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

    //region [ INFO NAVIGATION ]

    private void initInfoNavigationButtons(final int reportIndex) {
        int size = InfoReport.values().length;

        if (reportIndex == 0){
            ButtonUtils.setDisabled(navPrevious);
        }else{
            ButtonUtils.setEnabled(navPrevious, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToReport(reportIndex - 1);
                }
            });
        }

        navIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToIndex();
            }
        });

        if (reportIndex == size - 1){
            ButtonUtils.setDisabled(navNext);
        }else{
            ButtonUtils.setEnabled(navNext, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToReport(reportIndex + 1);
                }
            });
        }
    }

    private void navigateToIndex() {
        boolean isPreviousOverview = IadtController.get().getNavigationManager()
                .isPreviousScreen(InfoOverviewScreen.class);

        if (isPreviousOverview){
            getScreenManager().goBack();
        }
        else{
            OverlayService.performNavigation(InfoOverviewScreen.class);
        }
    }

    private void navigateToReport(int reportIndex) {
        updateParams(reportIndex + "");
        loadReport();
    }

    //endregion
}
