package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.info.InfoReport;
import es.rafaco.inappdevtools.library.logic.info.data.InfoReportData;
import es.rafaco.inappdevtools.library.logic.integrations.wcviewpager.ObjectAtPositionPagerAdapter;

public class InfoPagerAdapter extends ObjectAtPositionPagerAdapter {

    private final Context context;

    public InfoPagerAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Object instantiateItemObject(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.tool_info_page, container, false);
        container.addView(layout);

        InfoReport report = InfoReport.values()[position];
        InfoReportData reportData = IadtController.get().getInfoManager().getReportData(report);
        InfoPageViewHolder viewHolder = new InfoPageViewHolder(reportData);
        viewHolder.onCreatedView(layout);
        viewHolder.populateUI();
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

    public void updateView(final View pageView, int position){
        final InfoReport report = InfoReport.values()[position];

        new AsyncTask<InfoReport, InfoReport, InfoReportData>() {
            @Override
            protected InfoReportData doInBackground(InfoReport... infoReports) {
                return IadtController.get().getInfoManager().getReportData(report);
            }

            @Override
            protected void onPostExecute(InfoReportData result) {
                InfoPageViewHolder viewHolder = (InfoPageViewHolder) pageView.getTag();
                viewHolder.update(result);
                pageView.requestLayout();
            }
        }.execute();
    }
}
