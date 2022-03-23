/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
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

package org.inappdevtools.library.view.overlay.screens.session;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import org.inappdevtools.library.logic.documents.DocumentRepository;
import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.documents.generators.detail.SessionDocumentGenerator;
import org.inappdevtools.library.storage.db.entities.Session;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.cards.CardData;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.Screen;


public class SessionsScreen extends Screen {

    private long filterBuildId = -1;
    private AsyncTask<Long, String, List<Object>> currentTask;

    public SessionsScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Sessions";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        showProgress(true);
        initParam();

        if (currentTask != null){
            currentTask.cancel(true);
        }
        currentTask = new AsyncTask<Long, String, List<Object>>(){
            @Override
            protected List<Object> doInBackground(Long... objects) {
                List<Session> data = initData(objects[0]);
                List<Object> cardData = prepareData(data);
                return cardData;
            }

            @Override
            protected void onPostExecute(final List<Object> filteredItems) {
                super.onPostExecute(filteredItems);
                if (!currentTask.isCancelled()){
                    initAdapter((List<Object>) filteredItems);
                    showProgress(false);
                }
                currentTask = null;
            }
        };
        currentTask.execute(filterBuildId);
    }

    private void initParam() {
        if (!TextUtils.isEmpty(getParam())){
            filterBuildId = Long.parseLong(getParam());
        }

        if (filterBuildId > -1){
            getScreenManager().setTitle("Build " + filterBuildId + " sessions");
        }
    }

    private List<Session> initData(long filterBuildId) {
        List<Session> sessions;
        if (!TextUtils.isEmpty(getParam())) {
            sessions = IadtController.get().getSessionManager().getSessionsWithOverview(filterBuildId);
        } else {
            sessions = IadtController.get().getSessionManager().getSessionsWithOverview();
        }

        return sessions;
    }

    private List<Object> prepareData(List<Session> sessions) {
        List<Object> cards = new ArrayList<>();
        for (int i = 0; i<sessions.size(); i++) {
            final Session session = sessions.get(i);
            boolean isCurrent = (i==0);
            SessionDocumentGenerator reporter = ((SessionDocumentGenerator) DocumentRepository.getGenerator(DocumentType.SESSION, session));
            
            CardData cardData = new CardData(reporter.getTitle(),
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(SessionDetailScreen.class,
                                    session.getUid() + "");
                        }
                    });
            cardData.setContent(reporter.getOverview());
            if (isCurrent) {
                cardData.setBgColor(R.color.rally_blue_darker_alpha);
            }
            else if (session.getCrashId()>0){
                cardData.setBgColor(R.color.rally_orange_alpha);
            }else {
                cardData.setBgColor(R.color.rally_dark_green_alpha);
            }
            cards.add(cardData);
        }
        return cards;
    }

    private void initAdapter(List<Object> data) {
        FlexAdapter adapter = new FlexAdapter(FlexAdapter.Layout.GRID, 3, data);
        RecyclerView recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        showProgress(false);
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }
}
