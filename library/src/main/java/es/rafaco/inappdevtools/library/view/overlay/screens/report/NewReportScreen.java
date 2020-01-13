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

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.reports.ReportHelper;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.session.SessionReporter;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.ScreenshotDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.components.flex.EditTextData;
import es.rafaco.inappdevtools.library.view.components.flex.OverviewData;
import es.rafaco.inappdevtools.library.view.components.flex.SelectorData;
import es.rafaco.inappdevtools.library.view.overlay.FlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;


public class NewReportScreen extends FlexibleScreen {

    private Report report;

    public NewReportScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "New Report";
    }

    @Override
    protected void onAdapterStart() {
        if (getParams()!=null && getParams().report != null){
            report = getParams().report;
            getScreenManager().setTitle("Edit Report");
        }
        else{
            report = new Report();
        }

        loadNextStep();
    }

    private void loadNextStep() {
        if (report.getReportType() == null){
            updateAdapter(getIndexData());
            return;
        }

        if (report.getReportType() == ReportHelper.ReportType.CRASH &&
                report.getCrashId()<1){
            updateAdapter(getCrashSelectorData());
            return;
        }
        else if (report.getReportType() == ReportHelper.ReportType.SESSION &&
                report.getSessionId()<1){
            updateAdapter(getSessionSelectorData());
            return;
        }
        else if (report.getReportType() == ReportHelper.ReportType.CUSTOM &&
                TextUtils.isEmpty(report.getScreenIds())){
            updateAdapter(getScreenSelectorData());
            return;
        }

        updateAdapter(getFormData());
    }



    private OverviewData getOverview() {
        OverviewData report = new OverviewData("Report",
                "Session report\nSelected session 4\nEnter details:",
                R.string.gmd_send, R.color.rally_white);
        return report;
    }



    private List<Object> getIndexData() {
        List<Object> data = new ArrayList<>();
        data.add("");
        data.add("What do you want to report?");
        data.add("");
        CardData crashCard = new CardData("Crash report",
                "Select a crash to include",
                R.string.gmd_bug_report,
                new Runnable() {
                    @Override
                    public void run() {
                        report.setReportType(ReportHelper.ReportType.CRASH);
                        loadNextStep();
                    }
                });
        crashCard.setTitleColor(R.color.rally_orange);
        data.add(crashCard);

        CardData sessionCard = new CardData("Session report",
                "Select a session to include",
                R.string.gmd_history,
                new Runnable() {
                    @Override
                    public void run() {
                        report.setReportType(ReportHelper.ReportType.SESSION);
                        loadNextStep();
                    }
                });
        sessionCard.setTitleColor(R.color.rally_green);
        data.add(sessionCard);

        CardData screenCard = new CardData("Screenshot report",
                "Select screenshots to include",
                R.string.gmd_photo_library,
                new Runnable() {
                    @Override
                    public void run() {
                        report.setReportType(ReportHelper.ReportType.CUSTOM);
                        loadNextStep();
                    }
                });
        screenCard.setTitleColor(R.color.rally_blue);
        data.add(screenCard);

        CardData customCard = new CardData("Plain report",
                "For issues, feature request or questions",
                R.string.gmd_note,
                new Runnable() {
                    @Override
                    public void run() {
                        report.setReportType(ReportHelper.ReportType.ISSUE);
                        loadNextStep();
                    }
                });
        customCard.setTitleColor(R.color.rally_white);
        data.add(customCard);

        data.add("");
        return data;
    }

    private List<Object> getCrashSelectorData() {
        List<Object> data = new ArrayList<>();
        OverviewData overview = getOverview();
        overview.setColor(R.color.rally_orange);
        data.add(overview);
        data.add("Choose a crash:");

        List<Crash> crashes = IadtController.getDatabase().crashDao().getAll();
        if (crashes.size()==0){
            CardData cardData = new CardData("No crash detected",
                    new Runnable() {
                        @Override
                        public void run() {
                            report.setReportType(null);
                            loadNextStep();
                        }
                    });
            cardData.setContent("Nothing to report");
            cardData.setBgColor(R.color.iadt_surface_bottom);
            data.add(cardData);
        }
        else{
            for (int i = 0; i<crashes.size(); i++) {
                final Crash crash = crashes.get(i);
                CardData cardData = new CardData("Crash on session " + crash.getScreenId(),
                        new Runnable() {
                            @Override
                            public void run() {
                                report.setCrashId(crash.getUid());
                                loadNextStep();
                            }
                        });
                cardData.setContent(crash.getMessage());
                cardData.setBgColor(R.color.rally_orange_alpha);
                data.add(cardData);
            }
        }
        return data;
    }

    private List<Object> getSessionSelectorData(){
        List<Object> data = new ArrayList<>();
        data.add(getOverview());

        List<Session> sessions = IadtController.get().getSessionManager().getSessionsWithOverview();
        for (int i = 0; i<sessions.size(); i++) {
            final Session session = sessions.get(i);
            boolean isCurrent = (i==0);
            SessionReporter reporter = new SessionReporter(getContext(), session);

            CardData cardData = new CardData(reporter.getTitle(),
                    new Runnable() {
                        @Override
                        public void run() {
                            report.setSessionId(session.getUid());
                            loadNextStep();
                        }
                    });
            cardData.setContent(reporter.getOverview());
            cardData.setTitleColor(R.color.rally_white);
            if (isCurrent) {
                cardData.setBgColor(R.color.rally_blue_darker_alpha);
            }
            else if (session.getCrashId()>0){
                cardData.setBgColor(R.color.rally_orange_alpha);
            }else {
                cardData.setBgColor(R.color.rally_dark_green_alpha);
            }
            data.add(cardData);
        }
        return data;
    }



    private List<Object> getScreenSelectorData() {

        final List<Long> selectedScreenshots = new ArrayList<>();

        List<Object> data = new ArrayList<>();
        data.add(getOverview());
        data.add("");
        data.add("//TODO: WORK IN PROGRESS");
        data.add("");
        data.add("Choose screenshots and press continue:");
        data.add(new RunButton("Continue",
                new Runnable() {
                    @Override
                    public void run() {
                        report.setScreenIdList(selectedScreenshots);
                        loadNextStep();
                    }
                }));
        data.add("");

        ScreenshotDao screenshotDao = DevToolsDatabase.getInstance().screenshotDao();
        final List<Screenshot> screenshots = screenshotDao.getAll();

        data.addAll(screenshots);
        data.add("");
        for (int i = 0; i<screenshots.size(); i++) {
            final Screenshot screenshot = screenshots.get(i);

            String title = String.format("Screenshot %s", screenshot.getUid());
            String content = "Activity: " + screenshot.getActivityName()
                    + Humanizer.newLine()
                    + "Session: " + screenshot.getSession()
                    + Humanizer.newLine()
                    + "Elapsed: " + Humanizer.getElapsedTime(screenshot.getDate());

            CardData cardData = new CardData(title,
                    new Runnable() {
                        @Override
                        public void run() {
                            selectedScreenshots.add(screenshot.getUid());
                        }
                    });
            cardData.setContent(content);
            cardData.setImagePath(screenshot.getPath());

            if (selectedScreenshots.contains(screenshot.getUid())){
                cardData.setBgColor(R.color.rally_green_alpha);
            }

            data.add(cardData);
        }
        return data;
    }








    private List<Object> getFormData() {

        List<Object> data = new ArrayList<>();
        data.add(getOverview());
        data.add("");

        final List<String> reportCategories = new ArrayList<>(
                Arrays.asList("Report problem",
                        "Suggest improvement",
                        "Request feature",
                        "Send feedback"));

        data.add(new SelectorData("Reason:", reportCategories, report.getReasonInt(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                report.setReason(reportCategories.get(position));
                report.setReasonInt(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }));

        data.add(new EditTextData("Enter a title", report.getTitle(), 1, 80,
                new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                report.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }));

        data.add(new EditTextData("Enter a description", report.getDescription(), 2, 500,
                new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                report.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }));

        data.add(new EditTextData("Include your email (optional)", report.getEmail(), 1, 30,
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        report.setEmail(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }));

        data.add("");

        List<RunButton> reportButtons = new ArrayList<>();
        reportButtons.add(new RunButton("Save for later",
                R.drawable.ic_save_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onSaveReport();
                    }
                }));
        reportButtons.add(new RunButton("Send",
                R.drawable.ic_send_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onSendReport();
                    }
                }));
        data.add(new ButtonGroupData(reportButtons));

        return data;
    }

    private void onSaveReport() {
        if (validateReport()){
            saveReport();
        }
        getScreenManager().goBack();
    }

    private void onSendReport() {
        if (validateReport()){
            report.setDateSent(DateUtils.getLong());
            saveReport();
        }
        getScreenManager().goBack();
    }

    private boolean validateReport() {
        return true;
    }

    private void saveReport() {
        report.setDate(DateUtils.getLong());
        if (report.getUid()>0){
            IadtController.getDatabase().reportDao().update(report);
        }else{
            IadtController.getDatabase().reportDao().insert(report);
        }
    }





    //region [ PARAMS]

    public static String buildParams(Report report){
        Gson gson = new Gson();
        return gson.toJson(new InnerParams(report));
    }

    public InnerParams getParams(){
        Gson gson = new Gson();
        return gson.fromJson(getParam(), InnerParams.class);
    }

    public static class InnerParams {
        Report report;

        public InnerParams(Report report) {
            this.report = report;
        }
    }

    //endregion


    /*data.add("Choose a type of report:");
    CardData issueCard = new CardData("Issue",
            "Basic report with static info (device and compilation)",
            R.string.gmd_note,
            new Runnable() {
                @Override
                public void run() {
                    //onIssueReport();
                }
            });

        data.add(issueCard);

    CardData sessionCard = new CardData("Session",
            "Info and logs",
            R.string.gmd_history,
            new Runnable() {
                @Override
                public void run() {
                    //onSessionReport();
                }
            });
        sessionCard.setTitleColor(R.color.rally_green);
        data.add(sessionCard);

    CardData crashCard = new CardData("Crash report",
            "Crash details, info and logs",
            R.string.gmd_bug_report,
            new Runnable() {
                @Override
                public void run() {
                    //onCrashReport();
                }
            });
        crashCard.setTitleColor(R.color.rally_orange);
        data.add(crashCard);

    CardData customCard = new CardData("Custom report",
            "You choose everything",
            R.string.gmd_settings,
            new Runnable() {
                @Override
                public void run() {
                    //onCustomReport();
                }
            });
        customCard.setTitleColor(R.color.rally_blue);
        data.add(customCard);

    CardData fullCard = new CardData("Full report",
            "All from last data cleanup",
            R.string.gmd_select_all,
            new Runnable() {
                @Override
                public void run() {
                    //onFullReport();
                }
            });
        fullCard.setTitleColor(R.color.rally_yellow);
        data.add(fullCard);*/
}
