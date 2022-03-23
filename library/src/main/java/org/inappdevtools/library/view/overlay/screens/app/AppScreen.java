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

package org.inappdevtools.library.view.overlay.screens.app;

import org.inappdevtools.library.logic.documents.DocumentRepository;
import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.view.components.cards.CardData;
import org.inappdevtools.library.view.components.items.ButtonFlexData;
import org.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import org.inappdevtools.library.view.overlay.screens.sources.RepoInfoScreen;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;

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
        data.add("App actions   ");
        data.add(new ButtonFlexData("Restart app",
                R.drawable.ic_replay_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().restartApp(false);
                    }
                }));

        data.add(new ButtonFlexData("Force close app",
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
