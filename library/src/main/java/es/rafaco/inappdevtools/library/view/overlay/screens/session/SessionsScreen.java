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

import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.generators.detail.SessionDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;


public class SessionsScreen extends Screen {

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
        List<?> data = initData();
        initAdapter((List<Object>) data);
    }

    private List<CardData> initData() {
        List<Session> sessions = IadtController.get().getSessionManager().getSessionsWithOverview();
        List<CardData> cards = new ArrayList<>();
        for (int i = 0; i<sessions.size(); i++) {
            final Session session = sessions.get(i);
            boolean isCurrent = (i==0);
            SessionDocumentGenerator reporter = ((SessionDocumentGenerator) DocumentRepository.getGenerator(Document.SESSION, session));
            
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
        FlexibleAdapter adapter = new FlexibleAdapter(3, data);
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
