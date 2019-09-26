package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.logic.integrations.wcviewpager.ObjectAtPositionPagerAdapter;

public class InfoPagerAdapter extends ObjectAtPositionPagerAdapter {

    private final Context context;
    private List<List<Boolean>> expandedStates;

    public InfoPagerAdapter(Context context) {
        super();
        this.context = context;
        this.expandedStates = new ArrayList<>();
        for (InfoReport report: InfoReport.values()) {
            expandedStates.add(new ArrayList<Boolean>());
        }
    }

    @Override
    public Object instantiateItemObject(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.tool_info_page, container, false);
        container.addView(layout);

        InfoReport report = InfoReport.values()[position];
        InfoReportData reportData = IadtController.get().getInfoManager().getReportData(report);
        updateExpandedState(reportData, position);

        InfoPageViewHolder viewHolder = new InfoPageViewHolder();
        viewHolder.onCreatedView(layout);
        viewHolder.update(reportData);
        layout.setTag(viewHolder);

        return layout;
    }

    @Override
    public void destroyItemObject(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return InfoReport.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return InfoReport.values()[position].getTitle();
    }

    public void updateView(final View pageView, final int position){
        final InfoReport report = InfoReport.values()[position];

        new AsyncTask<InfoReport, InfoReport, InfoReportData>() {
            @Override
            protected InfoReportData doInBackground(InfoReport... infoReports) {
                return IadtController.get().getInfoManager().getReportData(report);
            }

            @Override
            protected void onPostExecute(InfoReportData result) {
                if (pageView != null){
                    InfoPageViewHolder viewHolder = (InfoPageViewHolder) pageView.getTag();
                    updateExpandedState(result, position);
                    viewHolder.update(result);
                    //pageView.requestLayout();
                }
            }
        }.execute();
    }

    private void updateExpandedState(InfoReportData reportData, int position) {
        if (expandedStates!=null && position < expandedStates.size()){
            List<Boolean> reportState = expandedStates.get(position);
            if (reportState!=null && !reportState.isEmpty()){
                for (int i = 0; i < reportState.size(); i++){
                    reportData.getEntries().get(i).setExpanded(true);//TODO: reportState.get(i));
                }
            }
        }
    }

    private void setExpandedState(int reportPosition, int groupPosition, boolean value) {
        if (expandedStates!=null && reportPosition < expandedStates.size()) {
            List<Boolean> reportState = expandedStates.get(reportPosition);
        }
        else{
        }
    }
}
