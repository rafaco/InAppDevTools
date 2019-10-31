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
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.view.ViewCompat;
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.widget.AppCompatButton;
//@import androidx.recyclerview.widget.DefaultItemAnimator;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogAnalysisHelper;
import es.rafaco.inappdevtools.library.logic.reports.ReportHelper;
import es.rafaco.inappdevtools.library.storage.db.entities.AnalysisData;
import es.rafaco.inappdevtools.library.view.icons.IconDrawable;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfoAdapter;
import es.rafaco.inappdevtools.library.view.components.deco.DecoratedToolInfo;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;

public class ReportScreen extends Screen {

    private TextView out;
    private Button sendButton;
    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView header;
    private Button manageScreensButton;

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
        initView();
        initAdapter();
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

        //ICONs from font playground
        TextView icon = getView().findViewById(R.id.test_icon);
        IconUtils.markAsIconContainer(icon, IconUtils.MATERIAL);
        icon.setText(R.string.gmd_3d_rotation);

        AppCompatButton icon2 = getView().findViewById(R.id.test_icon2);
        Drawable drawable = new IconDrawable(getContext(), R.string.gmd_access_alarms,
                IconUtils.MATERIAL).sizeDp(24);
        icon2.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        out = getView().findViewById(R.id.out);
        header = getView().findViewById(R.id.report_welcome);
        sendButton = getView().findViewById(R.id.report_button);
        manageScreensButton = getView().findViewById(R.id.manage_screens_button);

        header.setText("This generate a zip report and send it to developers. "
                + "To report an unzipped item, use share feature at other screens.");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendReportPressed();
            }
        });
        manageScreensButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onManageScreensPressed();
            }
        });
    }

    private void initAdapter() {

        adapter = new DecoratedToolInfoAdapter(getContext(), getInitialOptions());
        //adapter.enableSwitchMode();

        recyclerView = getView().findViewById(R.id.report_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<DecoratedToolInfo> getInitialOptions(){
        ArrayList<DecoratedToolInfo> array = new ArrayList<>();

        array.add(new DecoratedToolInfo(
                "Current session",
                "Info and logs from your last restart",
                R.color.rally_blue, 1,
                new Runnable() {
                    @Override
                    public void run() {
                        onSessionReport();
                    }
                }));

        array.add(new DecoratedToolInfo(
                "Last crash report",
                "Crash, info and logs",
                R.color.rally_orange, 1,
                new Runnable() {
                    @Override
                    public void run() {
                        onCrashReport();
                    }
                }));

        /*array.add(new DecoratedToolInfo(
                "Custom report",
                "You choose everything",
                R.color.rally_yellow, 1,
                new Runnable() {
                    @Override
                    public void run() {
                        onCustomReport();
                    }
                }));*/

        array.add(new DecoratedToolInfo(
                "Full report",
                "All from last data cleanup",
                R.color.rally_green, 1,
                new Runnable() {
                    @Override
                    public void run() {
                        onFullReport();
                    }
                }));

        return array;
    }

    private ArrayList<DecoratedToolInfo> getSessionOptions(){
        ArrayList<DecoratedToolInfo> array = new ArrayList<>();

        LogAnalysisHelper helper = new LogAnalysisHelper();
        List<AnalysisData> sessionOptions = helper.getSessionResult();
        for (final AnalysisData item : sessionOptions) {
            array.add(new DecoratedToolInfo(
                    "Session " + item.getName(),
                    item.getCount() + " log items " + item.getPercentage() + " %",
                    R.color.rally_green, 1,
                    new Runnable() {
                        @Override
                        public void run() {
                            onSessionReport(item.getName());
                        }
                    }));
        }

        return array;
    }

    private void onSessionReport() {
        updateAdapter(getSessionOptions());
    }

    private void onSessionReport(String name) {
        if (TextUtils.isEmpty(name)){

        }
        //Iadt.sendReport(ReportHelper.ReportType.SESSION, null);
    }

    private void updateAdapter(ArrayList<DecoratedToolInfo> options) {
        adapter.replaceAll(options);
    }

    private void onCrashReport() {
        Iadt.sendReport(ReportHelper.ReportType.CRASH, null);
    }

    private void onFullReport() {
        Iadt.sendReport(ReportHelper.ReportType.FULL, null);
    }





    private void onCustomReport() {
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


    //region [ TOOL SPECIFIC ]

    private void onSendReportPressed() {
        //TODO:
        Iadt.sendReport(ReportHelper.ReportType.SESSION, null);
    }

    private void onManageScreensPressed() {
        getScreenManager().goTo(ScreenshotsScreen.class, null);
    }

    //endregion
}
