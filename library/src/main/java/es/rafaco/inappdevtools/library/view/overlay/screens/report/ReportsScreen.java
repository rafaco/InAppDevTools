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

package es.rafaco.inappdevtools.library.view.overlay.screens.report;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.reports.ReportType;
import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;


public class ReportsScreen extends AbstractFlexibleScreen {

    public ReportsScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Reports";
    }

    @Override
    protected void onAdapterStart() {
        updateAdapter(getReportSelectorData());
    }

    private List<Object> getReportSelectorData() {
        List<Object> data = new ArrayList<>();
        data.add("");
        data.add("Select report to edit:");
        data.add("");
        List<Report> reports = IadtDatabase.get().reportDao().getAll();
        for (int i = 0; i<reports.size(); i++) {
            final Report report = reports.get(i);
            CardData cardData = new CardData(buildTitleString(report),
                    new Runnable() {
                        @Override
                        public void run() {
                            onReportSelected(report);
                        }
                    });
            cardData.setContent(buildContentString(report));
            if (report.isSent()){
                cardData.setBgColor(R.color.rally_green_alpha);
            }

            data.add(cardData);
        }
        return data;
    }

    private String buildTitleString(Report report) {
        String result = "Report " + report.getUid();
        return result;
    }

    private String buildContentString(Report report) {
        String result;
        if (report.getReportType() == ReportType.CRASH){
            result = "Crash " + report.getCrashId() + " report";
        }
        else if (report.getReportType() == ReportType.SESSION){
            result = "Session " + report.getSessionId() + " report";
        }
        else{
            result = "Issue report";
        }

        if (report.isSent()){
            result += ". Sent " + Humanizer.getElapsedTimeLowered(report.getDateSent());
        }
        else {
            result += ". Saved " + Humanizer.getElapsedTimeLowered(report.getDate());
        }

        if (!TextUtils.isEmpty(report.getTitle())){
            result += Humanizer.newLine() + report.getTitle();
        }
        if (!TextUtils.isEmpty(report.getDescription())){
            result += Humanizer.newLine() + Humanizer.truncate(report.getDescription(), 45);
        }

        return result;
    }

    private void onReportSelected(Report report) {
        String params = NewReportScreen.buildParams(report);
        OverlayService.performNavigation(NewReportScreen.class, params);
    }
}
