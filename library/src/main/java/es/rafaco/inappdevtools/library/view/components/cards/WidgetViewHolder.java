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

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.core.content.ContextCompat;
//@import androidx.cardview.widget.CardView;
//#else
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
//#endif

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
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.FlexViewHolder;
import es.rafaco.inappdevtools.library.view.icons.IconUtils;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class WidgetViewHolder extends FlexViewHolder {

    private final int[] NET_COLORS = {
            ContextCompat.getColor(getContext(), R.color.rally_green),
            ContextCompat.getColor(getContext(), R.color.rally_gray),
            ContextCompat.getColor(getContext(), R.color.rally_orange),
    };

    private final int[] LOG_COLORS = {
            ContextCompat.getColor(getContext(), R.color.rally_white),
            ContextCompat.getColor(getContext(), R.color.rally_blue),
            ContextCompat.getColor(getContext(), R.color.rally_green),
            ContextCompat.getColor(getContext(), R.color.rally_yellow),
            ContextCompat.getColor(getContext(), R.color.rally_orange),
            ContextCompat.getColor(getContext(), R.color.rally_orange_dark),
    };

    private final CardView cardView;
    private final TextView mainContent;
    private final TextView title;
    private final TextView secondContent;
    private final FrameLayout chartContainer;
    private final TextView icon;

    public WidgetViewHolder(View view, FlexAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.title = view.findViewById(R.id.title);
        this.mainContent = view.findViewById(R.id.main_content);
        this.secondContent = view.findViewById(R.id.second_content);
        this.chartContainer = view.findViewById(R.id.chart_container);
        this.icon = view.findViewById(R.id.icon);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final WidgetData data = (WidgetData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            title.setText(data.getTitle().toUpperCase());
            mainContent.setText(data.getMainContent());
            secondContent.setText(data.getSecondContent());
            secondContent.setVisibility(TextUtils.isEmpty(data.getSecondContent()) ? View.GONE : View.VISIBLE);

            if (data.getPerformer() != null) {
                itemView.setClickable(true);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.getPerformer().run();
                    }
                });
            }
            
            initChart(data);

            if (data.getIcon()>0){
                IconUtils.set(icon, data.getIcon());
            }
        }
    }

    private void initChart(WidgetData data) {
        if (data.getTitle().equals("Network")){
            initNetworkChart();
        }
        else if (data.getTitle().equals("Logs")){
            initLogChart();
        }
        else{
            chartContainer.setVisibility(View.GONE);
            //chart.setEnabled(false);
            //chart.setVisibility(View.GONE);
        }
    }

    private void initLogChart() {
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

    private void initNetworkChart() {
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
