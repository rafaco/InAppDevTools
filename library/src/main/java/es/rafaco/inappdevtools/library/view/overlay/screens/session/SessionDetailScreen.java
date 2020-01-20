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

package es.rafaco.inappdevtools.library.view.overlay.screens.session;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DetailDocument;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.log.filter.LogUiFilter;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.documents.detail.SessionDetailGenerator;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleViewHolder;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif


public class SessionDetailScreen extends Screen {

    private FlexibleAdapter adapter;
    private int expandedPosition = -1;

    public SessionDetailScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Session Detail";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        List<?> data = initData();
        initAdapter((List<Object>) data);
    }

    private List<Object> initData() {
        int sessionUid = Integer.parseInt(getParam());
        Session session = IadtController.get().getSessionManager().getSessionWithOverview(sessionUid);
        SessionDetailGenerator sessionHelper = ((SessionDetailGenerator)IadtController.get()
                .getDocumentManager().getDetailGenerator(DetailDocument.SESSION, session));
        DocumentData reportData = sessionHelper.getData();
        getScreenManager().setTitle(reportData.getTitle());

        List<Object> objectList = new ArrayList<Object>(reportData.getSections());
        objectList.add(0, reportData.getOverviewData());
        objectList.add(1, getButtonGroupData(session));
        return objectList;
    }

    private ButtonGroupData getButtonGroupData(final Session session) {
        List<RunButton> buttons = new ArrayList<>();
        buttons.add(new RunButton(
                "Steps",
                R.drawable.ic_history_white_24dp,
                R.color.rally_green,
                new Runnable() {
            @Override
            public void run() {
                final LogFilterHelper stepsFilter = new LogFilterHelper(LogFilterHelper.Preset.REPRO_STEPS);
                stepsFilter.setSessionById(session.getUid());

                OverlayService.performNavigation(LogScreen.class,
                        LogScreen.buildParams(stepsFilter.getUiFilter()));
            }
        }));

        buttons.add(new RunButton(
                "Full Logs",
                R.drawable.ic_format_align_left_white_24dp,
                R.color.rally_blue_med,
                new Runnable() {
            @Override
            public void run() {
                final LogFilterHelper filter = new LogFilterHelper(LogFilterHelper.Preset.DEBUG);
                filter.setSessionById(session.getUid());

                OverlayService.performNavigation(LogScreen.class,
                        LogScreen.buildParams(filter.getUiFilter()));
            }
        }));

        if (session.getCrashId()>0){
            buttons.add(new RunButton(
                    "Crash",
                    R.drawable.ic_bug_report_white_24dp,
                    R.color.rally_orange,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(CrashDetailScreen.class,
                                    session.getCrashId() + "");
                        }
            }));
        }

        return new ButtonGroupData(buttons);
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(3, data);
        adapter.setOnItemActionListener(new FlexibleAdapter.OnItemActionListener() {
            @Override
            public Object onItemAction(FlexibleViewHolder viewHolder, View view, int position, long id) {
                return toggleExpandedPosition(position);
            }
        });
        RecyclerView recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
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
                DocumentSectionData previousData = (DocumentSectionData) adapter.getItems().get(previousPosition);
                previousData.setExpanded(false);
                adapter.notifyItemChanged(previousPosition);
            }
            //Expand current selection
            expandedPosition = position;
            return true;
        }
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
