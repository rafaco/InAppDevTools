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

package es.rafaco.inappdevtools.library.view.overlay.screens.network;

import com.readystatesoftware.chuck.sample.HttpBinService;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import tech.linjiang.pandora.util.SimpleTask;

public class NetScreen extends AbstractFlexibleScreen {

    public NetScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Network3";
    }

    @Override
    protected void onAdapterStart() {
        //TODO: Background work and ANRs are everywhere: example using Pandora SimpleTask
        new SimpleTask<>(new SimpleTask.Callback<Void, List<NetSummary>>() {
            @Override
            public List<NetSummary> doInBackground(Void[] params) {
                long sessionId = IadtController.get().getSessionManager().getCurrentUid();
                return IadtController.getDatabase().netSummaryDao().filterBySessionId(sessionId);
            }

            @Override
            public void onPostExecute(List<NetSummary> result) {
                FriendlyLog.logDebug(result.size() + " network items.");
                updateAdapter(getFlexibleData(result));
            }
        }).execute();
    }

    private List<Object> getFlexibleData(List<NetSummary> summaries) {

        List<Object> data = new ArrayList<>();
        data.add("");
        data.add("Showing current session requests:");
        data.add("");

        List<RunButton> buttons = new ArrayList<>();
        buttons.add(new RunButton("Simulate", new Runnable() {
            @Override
            public void run() {
                HttpBinService.simulation(Iadt.getOkHttpClient());
            }
        }));
        buttons.add(new RunButton("Select Session", new Runnable() {
            @Override
            public void run() {
                //TODO: Session Selector
            }
        }));
        data.add(new ButtonGroupData(buttons));

        for (int i = 0; i<summaries.size(); i++) {
            final NetSummary summary = summaries.get(i);
            NetFormatter formatter = new NetFormatter(summary);

            String title = summary.url;
            String content = summary.host + Humanizer.newLine() + formatter.getComposedLine();
            int color = formatter.getColor();

            CardData cardData = new CardData(title,
                    new Runnable() {
                        @Override
                        public void run() {
                            onItemSelected(summary);
                        }
                    });
            cardData.setContent(content);
            cardData.setTitleColor(color);

            //String status = formatter.getStatusString();
            //cardData.setSubtitle(status);
            data.add(cardData);
        }
        return data;
    }

    private void onItemSelected(NetSummary summary) {
        OverlayService.performNavigation(NetDetailScreen.class, summary.uid + "");
    }
}
