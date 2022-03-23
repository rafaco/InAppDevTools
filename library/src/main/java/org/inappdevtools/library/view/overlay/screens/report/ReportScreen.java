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

package org.inappdevtools.library.view.overlay.screens.report;

import android.content.DialogInterface;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
//#endif

import org.inappdevtools.library.storage.db.IadtDatabase;
import org.inappdevtools.library.view.components.FlexAdapter;
import org.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import org.inappdevtools.library.view.components.items.ButtonFlexData;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.Iadt;
import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.OverlayService;
import org.inappdevtools.library.view.overlay.layers.Layer;
import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.Screen;

public class ReportScreen extends Screen {

    private RecyclerView flexibleContainer;
    private FlexAdapter adapter;

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


    private void initAdapter() {
        adapter = new FlexAdapter(FlexAdapter.Layout.GRID, 1, new ArrayList<>());
        flexibleContainer = getView().findViewById(R.id.flexible_contents);
        flexibleContainer.setAdapter(adapter);
    }

    private void updateAdapter(List<Object> options) {
        adapter.replaceItems(options);
    }


    private List<Object> getIndexData() {
        List<Object> data = new ArrayList<>();
        data.add("");
        data.add("Send reports directly to the development team of this app. You can include logs and other useful information for them.");
        data.add("");
        data.add(new ButtonFlexData("New Report",
                R.drawable.ic_add_circle_outline_white_24dp,
                R.color.rally_green,
                new Runnable() {
                    @Override
                    public void run() {
                        onNewReport();
                    }
                }));

        int reportsCount = IadtDatabase.get().reportDao().getAll().size();
        if (reportsCount>=1){
            data.add(new ButtonFlexData("Previous Reports",
                    R.drawable.ic_format_list_bulleted_white_24dp,
                    new Runnable() {
                        @Override
                        public void run() {
                            onManageReports();
                        }
                    }));
        }
        data.add("");

        data.add("Related features:");
        LinearGroupFlexData linearGroupData = new LinearGroupFlexData();
        linearGroupData.setHorizontal(true);
        linearGroupData.add(new ButtonFlexData("Take Screen",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onTakeScreen();
                    }
                }));
        linearGroupData.add(new ButtonFlexData("Start new session",
                R.drawable.ic_flag_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        onNewSession();
                    }
                }));
        data.add(linearGroupData);
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
                                Iadt.buildMessage("Restart from ReportScreen")
                                        .isInfo().fire();
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
        OverlayService.performNavigation(ReportsScreen.class);
    }

}
