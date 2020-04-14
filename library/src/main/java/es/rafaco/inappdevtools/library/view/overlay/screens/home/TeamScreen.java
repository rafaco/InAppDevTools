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

package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.content.DialogInterface;
import android.text.TextUtils;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//#else
import android.support.v7.app.AlertDialog;
//#endif

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.config.BuildConfigField;
import es.rafaco.inappdevtools.library.logic.config.ConfigManager;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.info.AppInfoDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.components.flex.OverviewData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;

public class TeamScreen extends AbstractFlexibleScreen {

    public TeamScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Team";
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

        ConfigManager configManager = IadtController.get().getConfig();
        String teamName = configManager.getString(BuildConfigField.TEAM_NAME);
        if (TextUtils.isEmpty(teamName)){
            AppInfoDocumentGenerator appHelper = ((AppInfoDocumentGenerator) DocumentRepository.getGenerator(DocumentType.APP_INFO));
            String appName = appHelper.getAppName();
            teamName = appName + "'s team";
        }

        String teamDescription = configManager.getString(BuildConfigField.TEAM_DESC);
        if (TextUtils.isEmpty(teamDescription)){
            teamDescription = "     No description provided.";
        }

        OverviewData overviewData = new OverviewData(teamName,
                teamDescription,
                R.string.gmd_group,
                R.color.rally_white);
        data.add(overviewData);

        data.add(new CardData("Reports",
                "Send report to this team",
                R.string.gmd_send,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ReportScreen.class);
                    }
                }));
        List<RunButton> prepareButtons = new ArrayList<>();
        RunButton take_screen = new RunButton("Take Screen",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onTakeScreen();
                    }
                });
        take_screen.setColor(R.color.iadt_surface_bottom);
        prepareButtons.add(take_screen);
        RunButton start_new_session = new RunButton("Start new session",
                R.drawable.ic_flag_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onNewSession();
                    }
                });
        start_new_session.setColor(R.color.iadt_surface_bottom);
        prepareButtons.add(start_new_session);
        data.add(new ButtonGroupData(prepareButtons));
        
        data.add("");
        data.add("Links");
        final Map<String, String> links = configManager.getMap(BuildConfigField.TEAM_LINKS);
        if (links.isEmpty()) {
            data.add("     No links provided.");
        }
        else{
            for (final String key: links.keySet()) {
                data.add(new RunButton(key,
                        R.drawable.ic_public_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                ExternalIntentUtils.viewUrl(links.get(key));
                            }
                        }));
            }
        }

        data.add("");
        data.add("Actions");
        List<RunButton> buttons = IadtController.get().getRunnableManager().getAll();
        if (buttons.isEmpty()){
            data.add("     No actions provided.");
        }else{
            data.addAll(buttons);
        }

        return data;
    }

    private void onNewSession() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Restart required")
                .setMessage("To start a new session we need to restart your app.")
                .setCancelable(true)
                .setPositiveButton("Restart",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Iadt.showMessage("Restart from ReportScreen");
                                IadtController.get().restartApp(false);
                            }})
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
    }

    private void onTakeScreen() {
        IadtController.get().takeScreenshot();
    }
}
