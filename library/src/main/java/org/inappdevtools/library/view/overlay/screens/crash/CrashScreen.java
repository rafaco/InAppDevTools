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

package org.inappdevtools.library.view.overlay.screens.crash;

import android.text.TextUtils;
import android.view.Gravity;

import org.inappdevtools.library.logic.documents.DocumentRepository;
import org.inappdevtools.library.logic.documents.DocumentType;
import org.inappdevtools.library.logic.log.filter.LogFilterHelper;
import org.inappdevtools.library.logic.reports.ReportType;
import org.inappdevtools.library.logic.utils.ClipboardUtils;
import org.inappdevtools.library.logic.utils.ExternalIntentUtils;
import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.storage.db.entities.Crash;
import org.inappdevtools.library.storage.db.entities.CrashDao;
import org.inappdevtools.library.storage.db.entities.Screenshot;
import org.inappdevtools.library.storage.db.entities.Sourcetrace;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.cards.HeaderIconFlexData;
import org.inappdevtools.library.view.components.composers.SecondaryButtonsComposer;
import org.inappdevtools.library.view.components.groups.CardGroupFlexData;
import org.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import org.inappdevtools.library.view.components.groups.RecyclerGroupFlexData;
import org.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import org.inappdevtools.library.view.components.items.ButtonFlexData;
import org.inappdevtools.library.view.components.items.ImageData;
import org.inappdevtools.library.view.components.items.OverviewData;
import org.inappdevtools.library.view.components.items.SeparatorFlexData;
import org.inappdevtools.library.view.components.items.TextFlexData;
import org.inappdevtools.library.view.overlay.screens.builds.BuildDetailScreen;
import org.inappdevtools.library.view.overlay.screens.session.SessionDetailScreen;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import org.inappdevtools.library.view.overlay.screens.log.LogScreen;
import org.inappdevtools.library.view.overlay.screens.view.ZoomScreen;
import org.inappdevtools.library.view.utils.Humanizer;
import org.inappdevtools.library.view.utils.MarginUtils;
import org.inappdevtools.library.view.utils.UiUtils;

public class CrashScreen extends AbstractFlexibleScreen {

    public CrashScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public boolean hasHorizontalMargin(){
        return false;
    }

    @Override
    protected void onAdapterStart() {
        Crash data = getData();
        List<Object> flexData =  getFlexibleData(data);
        updateAdapter(flexData);
    }

    private boolean isJustCrashed() {
        return TextUtils.isEmpty(getParam());
    }

    private long getCrashIdFromParam() {
        if (!TextUtils.isEmpty(getParam()))
            return Long.parseLong(getParam());
        return -1;
    }

    private Crash getData() {
        Crash result = null;
        CrashDao crashDao = IadtDatabase.get().crashDao();
        result = (isJustCrashed()) ? crashDao.getLast() : crashDao.findById(getCrashIdFromParam());
        return result;
    }

    private List<Object> getFlexibleData(Crash crash) {
        List<Object> data = new ArrayList<>();
        if (crash == null){
            addEmptyOverview(data);
            return data;
        }

        addOverview(data, crash);
        addImageAndButtons(data, crash);

        TraceGrouper traceGrouper = initTraceGrouper(crash);
        initExceptionCard(data, crash, traceGrouper, false);
        initExceptionCard(data, crash, traceGrouper, true);

        addSecondaryButtons(data, crash);
        return data;
    }

    private void addEmptyOverview(List<Object> data) {
        data.add(new OverviewData("Crash not found",
                "This is weird :(",
                R.string.gmd_bug_report,
                R.color.iadt_text_low));
    }

    private void addOverview(List<Object> data, Crash crash) {
        String title = isJustCrashed()
                ? "Your app just crashed"
                : "Session " + crash.getSessionId() + " crashed ";

        StringBuilder content = new StringBuilder();
        content.append("When: " + Humanizer.getElapsedTimeLowered(crash.getDate()));
        content.append(Humanizer.newLine());
        content.append("App status: " + (crash.isForeground() ? "Foreground" : "Background"));
        content.append(Humanizer.newLine());
        content.append("Last activity: " + crash.getLastActivity());
        content.append(Humanizer.newLine());
        content.append("Thread: " + crash.getThreadName());

        data.add(new OverviewData(title,
                content.toString(),
                R.string.gmd_bug_report,
                R.color.rally_orange));
    }

    private void addImageAndButtons(List<Object> data, final Crash crash) {
        LinearGroupFlexData horizontalContainer = new LinearGroupFlexData();
        horizontalContainer.setHorizontal(true);
        horizontalContainer.setHorizontalMargin(true);
        addVerticalButtons(horizontalContainer, crash);
        addImage(horizontalContainer, crash);
        data.add(horizontalContainer);
    }

    private void addImage(LinearGroupFlexData data, Crash crash) {
        long screenId = crash.getScreenId();
        final Screenshot screenshot = IadtDatabase.get().screenshotDao().findById(screenId);
        if (screenshot !=null && !TextUtils.isEmpty(screenshot.getPath())){
            ImageData image = new ImageData(screenshot.getPath());
            image.setIcon(R.drawable.ic_zoom_out_map_white_24dp);
            image.setPerformer(new Runnable() {
                @Override
                public void run() {
                    OverlayService.performNavigation(ZoomScreen.class,
                            screenshot.getUid() + "");
                }
            });
            data.add(image);
        }
    }

    private void addVerticalButtons(LinearGroupFlexData data, final Crash crash) {
        LinearGroupFlexData verticalButtons = new LinearGroupFlexData();
        verticalButtons.setFullSpan(false);

        verticalButtons.add(new ButtonFlexData("Report now!",
                R.drawable.ic_send_white_24dp,
                R.color.rally_green_alpha,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().startReportWizard(ReportType.CRASH, crash.getUid());
                    }
                }));
        data.add(verticalButtons);
    }

    private TraceGrouper initTraceGrouper(Crash crash) {
        List<Sourcetrace> traces = IadtDatabase.get().sourcetraceDao().filterCrash(crash.getUid());
        TraceGrouper grouper = new TraceGrouper();
        grouper.process(traces);
        return grouper;
    }

    private void initExceptionCard(List<Object> data, Crash crash, TraceGrouper traceGrouper, boolean isCause) {

        String title, message;
        final String composedMessage;
        final FlexAdapter adapter;

        TextFlexData overCard = new TextFlexData("");
        overCard.setGravity(Gravity.LEFT);
        int horizontalMargin = (int) UiUtils.dpToPx(getContext(), 14); //standard+innerCard
        int topMargin = (int) UiUtils.dpToPx(getContext(), 20);
        int buttonMargin = (int) UiUtils.dpToPx(getContext(), 0);
        overCard.setMargins(horizontalMargin, topMargin, horizontalMargin, buttonMargin);

        if (!isCause){
            title = crash.getException();
            message = crash.getMessage();
            composedMessage = title + ". " + message;
            adapter = traceGrouper.getExceptionAdapter();
            overCard.setText("Exception");
            data.add(overCard);
        }
        else{
            title = crash.getCauseException();
            message = crash.getCauseMessage();
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(message)){
                return;
            }
            composedMessage = title + ". " + message;
            adapter = traceGrouper.getCauseAdapter();
            overCard.setText("Cause");
            data.add(overCard);
        }
        
        CardGroupFlexData cardGroup = new CardGroupFlexData();
        cardGroup.setFullWidth(true);
        cardGroup.setVerticalMargin(true);
        cardGroup.setBgColorResource(R.color.rally_orange_alpha);
        LinearGroupFlexData cardData = new LinearGroupFlexData();

        HeaderIconFlexData headerData = new HeaderIconFlexData.Builder(title)
                .setExpandable(false)
                .setExpanded(true)
                //.setOverview("Exception")
                .build();
        headerData.setMargins(MarginUtils.getHorizontalMargin(), 2*MarginUtils.getVerticalMargin(),
                MarginUtils.getHorizontalMargin(), MarginUtils.getVerticalMargin());
        cardData.add(headerData);

        TextFlexData messageData = new TextFlexData(message);
        messageData.setSize(TextFlexData.Size.LARGE);
        messageData.setHorizontalMargin(true);
        cardData.add(messageData);

        List<Object> groupLinear = new ArrayList<>();
        groupLinear.add(new ButtonBorderlessFlexData("Copy",
                R.drawable.ic_content_copy_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        ClipboardUtils.save(getContext(), composedMessage);
                        Iadt.buildMessage("Exception copied to clipboard")
                                .isInfo().fire();
                    }
                }));

        groupLinear.add(new ButtonBorderlessFlexData("Google it",
                R.drawable.ic_google_brands,
                new Runnable() {
                    @Override
                    public void run() {
                        String url = "https://www.google.com/search?q=" + composedMessage;
                        ExternalIntentUtils.viewUrl(url);
                    }
                }));
        LinearGroupFlexData buttonList = new LinearGroupFlexData(groupLinear);
        buttonList.setHorizontal(true);
        cardData.add(buttonList);

        SeparatorFlexData separator = new SeparatorFlexData(true);
        separator.setVerticalMargin(true);
        cardData.add(separator);

        if (adapter != null && adapter.getItemCount()>0){
            RecyclerGroupFlexData tracesContentData = new RecyclerGroupFlexData(adapter);
            tracesContentData.setHorizontalMargin(true);
            cardData.add(tracesContentData);
        }else{
            TextFlexData emptyTraces = new TextFlexData("No stacktrace available :(");
            emptyTraces.setSize(TextFlexData.Size.LARGE);
            emptyTraces.setHorizontalMargin(true);
            cardData.add(emptyTraces);
        }

        cardGroup.add(cardData);
        data.add(cardGroup);
    }

    private void addSecondaryButtons(List<Object> data, final Crash crash) {
        final long logId = IadtDatabase.get().friendlyDao()
                .findLogIdByCrashId(crash.getUid());

        SecondaryButtonsComposer composer = new SecondaryButtonsComposer("More");

        if (crash.getScreenId()>0){
            composer.add("Screenshot",
                    R.string.gmd_photo,
                    R.color.iadt_text_high,
                    new Runnable() {
                        @Override
                        public void run() {
                            OverlayService.performNavigation(ZoomScreen.class, crash.getScreenId() + "");
                        }
                    });
        }
        composer.add("Reproduction Steps",
                R.string.gmd_format_list_numbered,
                R.color.iadt_text_high,
                new Runnable() {
                    @Override
                    public void run() {
                        LogFilterHelper stepsFilter = new LogFilterHelper(LogFilterHelper.Preset.REPRO_STEPS);
                        stepsFilter.setSessionById(crash.getSessionId());
                        OverlayService.performNavigation(LogScreen.class,
                                LogScreen.buildParams(stepsFilter.getUiFilter(), logId));
                    }
                });
        composer.add("All logs",
                R.string.gmd_format_align_left,
                R.color.iadt_text_high,
                new Runnable() {
                    @Override
                    public void run() {
                        LogFilterHelper logsFilter = new LogFilterHelper(LogFilterHelper.Preset.DEBUG);
                        logsFilter.setSessionById(crash.getSessionId());
                        OverlayService.performNavigation(LogScreen.class,
                                LogScreen.buildParams(logsFilter.getUiFilter(), logId));
                    }
                });
        composer.add("Session",
                R.string.gmd_history,
                R.color.iadt_text_high,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SessionDetailScreen.class, crash.getSessionId() + "");
                    }
                });
        composer.add("Build",
                R.string.gmd_build,
                R.color.iadt_text_high,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(BuildDetailScreen.class, crash.getSessionId() + "");
                    }
                });
        composer.getContainer().setHorizontalMargin(true);
        composer.getContainer().setVerticalMargin(true);
        data.add(composer.compose());
    }
    
    //TODO: restore share crash?
    private void share(Crash crash){
        Iadt.buildMessage("Sharing crash document")
                .isInfo().fire();
        DocumentRepository.shareDocument(DocumentType.CRASH, crash);
    }
}
