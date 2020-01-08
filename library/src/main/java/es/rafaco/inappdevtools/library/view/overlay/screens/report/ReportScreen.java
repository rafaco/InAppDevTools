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

import android.content.DialogInterface;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.reports.ReportHelper;
import es.rafaco.inappdevtools.library.logic.runnables.ButtonGroupData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;

public class ReportScreen extends Screen {

    private RecyclerView flexibleContainer;
    private FlexibleAdapter adapter;
    private ReportHelper.ReportType selectedReport;

    public ReportScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Report";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_report; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        initAdapter();

        //updateAdapter(getTypeSelectorData());
        updateAdapter(getIndexData());
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }


    private void initView() {
        //ICON playground with fonts
        /*TextView icon = getView().findViewById(R.id.test_icon);
        IconUtils.markAsIconContainer(icon, IconUtils.MATERIAL);
        icon.setText(R.string.gmd_3d_rotation);

        AppCompatButton icon2 = getView().findViewById(R.id.test_icon2);
        Drawable drawable = new IconDrawable(getContext(), R.string.gmd_access_alarms,
                IconUtils.MATERIAL).sizeDp(24);
        icon2.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);*/
    }

    private void initAdapter() {
        adapter = new FlexibleAdapter(1, new ArrayList<>());
        flexibleContainer = getView().findViewById(R.id.flexible_contents);
        flexibleContainer.setAdapter(adapter);
    }

    private void updateAdapter(List<Object> options) {
        adapter.replaceItems(options);
    }


    private List<Object> getIndexData() {
        List<Object> data = new ArrayList<>();
        data.add("Send reports directly to the development team, including gathered data to help them.");
        data.add("");
        data.add("");
        data.add("Prepare your reports:");
        List<RunButton> prepareButtons = new ArrayList<>();
        prepareButtons.add(new RunButton("Take Screen",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onTakeScreen();
                    }
                }));
        prepareButtons.add(new RunButton("Start new session",
                R.drawable.ic_flag_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onNewSession();
                    }
                }));
        data.add(new ButtonGroupData(prepareButtons));
        data.add("");
        data.add("Create a report:");
        data.add(new RunButton("New Report",
                R.drawable.ic_add_circle_outline_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onNewReport();
                    }
                }));
        data.add("");
        data.add("Manage your reports:");
        data.add(new RunButton("Reports",
                R.drawable.ic_format_list_bulleted_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onManageReports();
                    }
                }));
        data.add("");

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

    private void onNewReport() {
        OverlayService.performNavigation(NewReportScreen.class);
    }

    private void onManageReports() {
        Iadt.showMessage("//TODO");
        //OverlayService.performNavigation(ReportsScreen.class);
    }


/*    private void manageAttachments() {
        List<RunButton> sessionButtons = new ArrayList<>();
        sessionButtons.add(new RunButton("Current Session",
                R.drawable.ic_send_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onCurrentSessionReport();
                    }
                }));
        sessionButtons.add(new RunButton("View all",
                R.drawable.ic_format_list_bulleted_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onManageSessions();
                    }
                }));
        data.add(new ButtonGroupData("Sessions:", sessionButtons));

        List<RunButton> crashButtons = new ArrayList<>();
        crashButtons.add(new RunButton("Last Crash",
                R.drawable.ic_send_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onLastCrashReport();
                    }
                }));
        crashButtons.add(new RunButton("View all",
                R.drawable.ic_format_list_bulleted_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onManageCrashes();
                    }
                }));
        data.add(new ButtonGroupData("Crashes:", crashButtons));

        List<RunButton> screenButtons = new ArrayList<>();
        screenButtons.add(new RunButton("Take Screen",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onTakeScreen();
                    }
                }));
        screenButtons.add(new RunButton("View all",
                R.drawable.ic_format_list_bulleted_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onManageScreens();
                    }
                }));
        data.add(new ButtonGroupData("Screenshots:", screenButtons));
    }

    private void onManageCrashes() {

    }

    private void onLastCrashReport() {

    }

    private void onManageSessions() {

    }

    private void onCurrentSessionReport() {

    }

    private void onManageScreens() {

    }*/





    private void onScreenshotsContinue() {
        Iadt.showMessage("Reports coming soon!");
    }


    private void onCrashReport() {
        selectedReport = ReportHelper.ReportType.CRASH;

        List<Object> data = new ArrayList<>();
        data.add("Step 1 - Report type: SESSION");
        data.add("");
        data.add("Step 2 - Choose session:");
        data.add(new RunButton("SEND",
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("Reports coming soon!");
                        //TODO:
                        // Iadt.sendReport(selectedReport, selectedSession);
                    }
                }));

        //TODO
        Iadt.sendReport(selectedReport, null);
    }


    private void onCustomReport() {
        selectedReport = ReportHelper.ReportType.CUSTOM;

        //TODO:
        onLevelButton();
    }

    private void onLevelButton() {
        String[] levelsArray = new String[]{ "Stored logs", "Crash", "Screens" };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Select crash")
                .setCancelable(true)
                .setMultiChoiceItems(levelsArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //TODO?
                    }
                })
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
    }


    private void onFullReport() {
        selectedReport = ReportHelper.ReportType.CUSTOM;

        //TODO
        Iadt.sendReport(selectedReport, null);
    }


    //region [ TOOL SPECIFIC ]

    private void onManageScreensPressed() {
        getScreenManager().goTo(ScreenshotsScreen.class, null);
    }

    //endregion
}
