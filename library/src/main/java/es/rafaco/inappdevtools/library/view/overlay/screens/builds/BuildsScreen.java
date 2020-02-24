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

package es.rafaco.inappdevtools.library.view.overlay.screens.builds;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.BuildInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.db.entities.Build;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

public class BuildsScreen extends Screen {

    public BuildsScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Builds";
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
        List<Build> builds = IadtController.getDatabase().buildDao().getAll();

        List<CardData> cards = new ArrayList<>();
        for (int i = 0; i < builds.size(); i++) {
            final Build build = builds.get(i);
            boolean isCurrent = (i==0);
            BuildInfoDocumentGenerator reporter = ((BuildInfoDocumentGenerator) DocumentRepository
                    .getGenerator(DocumentType.BUILD_INFO, build.getFirstSession()));
            
            CardData cardData = new CardData(reporter.getTitle(),
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(BuildDetailScreen.class,
                                    build.getUid() + "");
                        }
                    });
            cardData.setContent(reporter.getOverview());
            if (isCurrent) {
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
