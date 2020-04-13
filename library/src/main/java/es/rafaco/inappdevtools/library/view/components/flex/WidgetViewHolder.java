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

package es.rafaco.inappdevtools.library.view.components.flex;

import android.content.Context;
import android.os.Build;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.generators.detail.SessionDocumentGenerator;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummaryDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionAnalysis;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class WidgetViewHolder extends FlexibleViewHolder {

    private final CardView cardView;
    private final TextView mainContent;
    private final TextView title;
    private final TextView secondContent;
    private final FrameLayout chartContainer;

    public WidgetViewHolder(View view, FlexibleAdapter adapter) {
        super(view, adapter);
        this.cardView = view.findViewById(R.id.card_view);
        this.title = view.findViewById(R.id.title);
        this.mainContent = view.findViewById(R.id.main_content);
        this.secondContent = view.findViewById(R.id.second_content);
        this.chartContainer = view.findViewById(R.id.chart_container);
    }

    @Override
    public void bindTo(Object abstractData, final int position) {
        final WidgetData data = (WidgetData) abstractData;
        if (data!=null){

            itemView.setActivated(true);

            title.setText(data.getTitle().toUpperCase());
            title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_header));

            mainContent.setText(data.getMainContent());
            mainContent.setText(data.getMainContent());

            secondContent.setVisibility(TextUtils.isEmpty(data.getSecondContent()) ? View.GONE : View.VISIBLE);
            secondContent.setText(data.getSecondContent());
            secondContent.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));

            if (data.getPerformer() != null) {
                itemView.setClickable(true);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.getPerformer().run();
                    }
                });
            }
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.material_surface_medium));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(UiUtils.getPixelsFromDp(itemView.getContext(), 3));
            }
            
            initChart(data);
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
        Context context = itemView.getContext();
        /*FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                (int) UiUtils.getPixelsFromDp(context, 100));
        BarChart chart = new BarChart(context);
        chart.setLayoutParams(params);
        chartContainer.addView(chart);
        chartContainer.setVisibility(View.VISIBLE);*/

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                (int) UiUtils.getPixelsFromDp(context, 20));
        HorizontalBarChart chart = new HorizontalBarChart(context);
        chart.setLayoutParams(params);
        chartContainer.addView(chart);
        chartContainer.setVisibility(View.VISIBLE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        chart.setDescription(null);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        chart.animateY(1000);
        chart.animateX(2000);

        int[] LOG_COLORS = {
                ContextCompat.getColor(context, R.color.rally_white),
                ContextCompat.getColor(context, R.color.rally_blue),
                ContextCompat.getColor(context, R.color.rally_green),
                ContextCompat.getColor(context, R.color.rally_yellow),
                ContextCompat.getColor(context, R.color.rally_orange),
                ContextCompat.getColor(context, R.color.rally_orange),
        };

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        /*for (int i = (int) 0; i < 5; i++) {
            float val = (float) (Math.random());
            yVals1.add(new BarEntry(i, val * 1000));
        }*/
        Session currentSession = IadtController.get().getSessionManager().getCurrent();
        SessionDocumentGenerator generator = new SessionDocumentGenerator(context, DocumentType.SESSION, currentSession);
        SessionAnalysis analysis = generator.getAnalysis();

        yVals1.add(new BarEntry(1, new float[]{
                analysis.getLogcatVerbose() + analysis.getEventVerbose(),
                analysis.getLogcatDebug() + analysis.getEventDebug(),
                analysis.getLogcatInfo() + analysis.getEventInfo(),
                analysis.getLogcatWarning() + analysis.getEventWarning(),
                analysis.getLogcatError() + analysis.getEventError(),
                analysis.getLogcatFatal() + analysis.getEventFatal(),
        }));

        /*yVals1.add(new BarEntry(1, analysis.getLogcatVerbose()));
        yVals1.add(new BarEntry(2, analysis.getLogcatDebug()));
        yVals1.add(new BarEntry(3, analysis.getLogcatInfo()));
        yVals1.add(new BarEntry(4, analysis.getLogcatWarning()));
        yVals1.add(new BarEntry(5, analysis.getLogcatError()));
        yVals1.add(new BarEntry(6, analysis.getLogcatFatal()));*/

        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "Verbosity");
        set1.setColors(LOG_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        chart.setDrawValueAboveBar(false);
        data.setValueTextSize(0f);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.rally_white));
        data.setBarWidth(0.8f);
        
        chart.setData(data);
    }

    private void initNetworkChart() {
        Context context = itemView.getContext();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                (int) UiUtils.getPixelsFromDp(context, 20));
        HorizontalBarChart chart = new HorizontalBarChart(context);
        chart.setLayoutParams(params);
        chartContainer.addView(chart);
        chartContainer.setVisibility(View.VISIBLE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        chart.setDescription(null);
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        chart.animateY(1000);
        chart.animateX(2000);

        int[] NET_COLORS = {
                ContextCompat.getColor(context, R.color.rally_green),
                ContextCompat.getColor(context, R.color.rally_gray),
                ContextCompat.getColor(context, R.color.rally_orange),
        };

        NetSummaryDao netSummaryDao = IadtController.getDatabase().netSummaryDao();
        long currentSession = IadtController.get().getSessionManager().getCurrentUid();
        long errorsCount = netSummaryDao.countBySessionAndStatus(currentSession, NetSummary.Status.ERROR);
        long successCount = netSummaryDao.countBySessionAndStatus(currentSession, NetSummary.Status.COMPLETE);
        long pendingCount = netSummaryDao.countBySessionAndStatus(currentSession, NetSummary.Status.REQUESTING);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        yVals1.add(new BarEntry(1, new float[]{
                successCount,
                pendingCount,
                errorsCount,
        }));

        BarDataSet set1 = new BarDataSet(yVals1, "Verbosity");
        set1.setColors(NET_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        chart.setDrawValueAboveBar(false);
        data.setValueTextSize(0f);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.rally_white));
        data.setBarWidth(1f);

        chart.setData(data);
    }

    /*private void initChart1() {
        chart.setMaxVisibleValueCount(5);
        //chart.setOnChartValueSelectedListener(this);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        //chart.setAxDrawYLabels(false);

        ArrayList NoOfEmp = new ArrayList();
        NoOfEmp.add(new BarEntry(945f, 0));
        NoOfEmp.add(new BarEntry(1040f, 1));
        NoOfEmp.add(new BarEntry(1133f, 2));
        NoOfEmp.add(new BarEntry(1240f, 3));
        NoOfEmp.add(new BarEntry(1369f, 4));

        ArrayList<String> year = new ArrayList();
        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");

        BarDataSet empdataset = new BarDataSet(NoOfEmp, "No Of Employee");
        //BarDataSet yeardataset = new BarDataSet(year, "Years");
        chart.animateY(1000);
        BarData data = new BarData(empdataset);
        data.setBarWidth(100F);
        empdataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);



        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = chart.getLegend();
        l.setEnabled(false);
    }*/
}
