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

package es.rafaco.inappdevtools.library.view.components.cards;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.detail.SessionDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.db.IadtDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummaryDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionAnalysis;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class WidgetChartHelper {

    FrameLayout chartContainer;

    public WidgetChartHelper(FrameLayout chartContainer) {
        this.chartContainer = chartContainer;
    }

    private Context getContext(){
        return chartContainer.getContext();
    }

    public void initLogChart() {
        int[] LOG_COLORS = {
                ContextCompat.getColor(getContext(), R.color.rally_white),
                ContextCompat.getColor(getContext(), R.color.rally_blue),
                ContextCompat.getColor(getContext(), R.color.rally_green),
                ContextCompat.getColor(getContext(), R.color.rally_yellow),
                ContextCompat.getColor(getContext(), R.color.rally_orange),
                ContextCompat.getColor(getContext(), R.color.rally_orange_dark),
        };
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                (int) UiUtils.getPixelsFromDp(getContext(), 20));
        HorizontalBarChart chart = new HorizontalBarChart(getContext());
        chart.setLayoutParams(params);
        chartContainer.addView(chart);
        chartContainer.setVisibility(View.VISIBLE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        chart.setDescription(null);
        chart.setTouchEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        //Following seems not affecting
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        //chart.animateXY(2000, 1000);
        chart.animateXY(0,0);

        Session currentSession = IadtController.get().getSessionManager().getCurrent();
        SessionDocumentGenerator generator = new SessionDocumentGenerator(getContext(), DocumentType.SESSION, currentSession);
        SessionAnalysis analysis = generator.getAnalysis();

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        yVals1.add(new BarEntry(1, new float[]{
                analysis.getLogcatVerbose() + analysis.getEventVerbose(),
                analysis.getLogcatDebug() + analysis.getEventDebug(),
                analysis.getLogcatInfo() + analysis.getEventInfo(),
                analysis.getLogcatWarning() + analysis.getEventWarning(),
                analysis.getLogcatError() + analysis.getEventError(),
                analysis.getLogcatFatal() + analysis.getEventFatal(),
        }));

        BarDataSet set1 = new BarDataSet(yVals1, "Verbosity");
        set1.setColors(LOG_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        chart.setDrawValueAboveBar(false);
        data.setValueTextSize(0f);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.rally_white));
        data.setBarWidth(0.8f);

        chart.setData(data);
    }

    public void initNetworkChart() {
        int[] NET_COLORS = {
                ContextCompat.getColor(getContext(), R.color.rally_green),
                ContextCompat.getColor(getContext(), R.color.rally_gray),
                ContextCompat.getColor(getContext(), R.color.rally_orange),
        };

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                (int) UiUtils.getPixelsFromDp(getContext(), 20));
        HorizontalBarChart chart = new HorizontalBarChart(getContext());
        chart.setLayoutParams(params);
        chartContainer.addView(chart);
        chartContainer.setVisibility(View.VISIBLE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        chart.setDescription(null);
        chart.setTouchEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.animateXY(0,0);
        //chart.animateXY(2000, 1000);

        NetSummaryDao netSummaryDao = IadtDatabase.get().netSummaryDao();
        long currentSession = IadtController.get().getSessionManager().getCurrentUid();
        long errorsCount = netSummaryDao.countBySessionAndStatus(currentSession, NetSummary.Status.ERROR);
        long successCount = netSummaryDao.countBySessionAndStatus(currentSession, NetSummary.Status.COMPLETE);
        long pendingCount = netSummaryDao.countBySessionAndStatus(currentSession, NetSummary.Status.REQUESTING);

        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        yVals1.add(new BarEntry(1, new float[]{
                successCount,
                pendingCount,
                errorsCount,
        }));

        BarDataSet set1 = new BarDataSet(yVals1, "Verbosity");
        set1.setColors(NET_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        chart.setDrawValueAboveBar(false);
        data.setValueTextSize(0f);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.rally_white));
        data.setBarWidth(1f);

        chart.setData(data);
    }
}
