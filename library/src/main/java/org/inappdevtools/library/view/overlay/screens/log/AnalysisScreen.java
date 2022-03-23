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

package org.inappdevtools.library.view.overlay.screens.log;

import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import org.inappdevtools.library.logic.log.datasource.LogAnalysisHelper;
import org.inappdevtools.library.view.components.FlexAdapter;

import java.util.ArrayList;
import java.util.List;

import org.inappdevtools.library.R;

import org.inappdevtools.library.view.overlay.ScreenManager;
import org.inappdevtools.library.view.overlay.screens.Screen;

public class AnalysisScreen extends Screen {

    public AnalysisScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Log analysis";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_log_analysis;
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart(ViewGroup toolHead) {
        LogAnalysisHelper analysis = new LogAnalysisHelper();

        List<Object> data1 = new ArrayList<Object>(analysis.getSeverityResult());
        initAdapter(R.id.flexible1, data1);

        List<Object> data2 = new ArrayList<Object>(analysis.getCategoryResult());
        initAdapter(R.id.flexible2, data2);
    }

    private void initAdapter(int resourceId, List<Object> data) {
        FlexAdapter adapter = new FlexAdapter(FlexAdapter.Layout.GRID, 1, data);
        RecyclerView recyclerView = bodyView.findViewById(resourceId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
