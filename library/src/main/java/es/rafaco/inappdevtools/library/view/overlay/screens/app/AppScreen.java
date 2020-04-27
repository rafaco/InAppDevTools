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

package es.rafaco.inappdevtools.library.view.overlay.screens.app;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.RepoInfoScreen;

public class AppScreen extends AbstractFlexibleScreen {

    public AppScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "App";
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    @Override
    protected void onAdapterStart() {
        updateAdapter(getFlexibleData());
    }

    private List<Object> getFlexibleData() {
        List<Object> data = new ArrayList<>();

        long sessionId = IadtController.get().getSessionManager().getCurrentUid();
        data.add(DocumentRepository.getCardDataLink(DocumentType.APP_INFO,
                AppInfoScreen.class, null));
        CardData cardDataLink1 = DocumentRepository.getCardDataLink(DocumentType.BUILD_INFO,
                BuildDetailScreen.class, sessionId + "");
        cardDataLink1.setTitle("Build");
        data.add(cardDataLink1);
        data.add(DocumentRepository.getCardDataLink(DocumentType.REPO_INFO,
                RepoInfoScreen.class, sessionId + ""));
        data.add(DocumentRepository.getCardDataLink(DocumentType.TOOLS_INFO,
                ToolsInfoScreen.class, null));

        data.add("");
        data.add("App shortcuts");
        data.add(new RunButton("Restart app",
                R.drawable.ic_replay_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().restartApp(false);
                    }
                }));

        data.add(new RunButton("Force close app",
                R.drawable.ic_warning_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().forceCloseApp(false);
                    }
                }));

        return data;
    }
}
