package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.rafaco.inappdevtools.library.R;
import nevet.me.wcviewpager.ObjectAtPositionPagerAdapter;

public class InfoPagerAdapter extends ObjectAtPositionPagerAdapter {

    private final Context context;

    public InfoPagerAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Object instantiateItemObject(ViewGroup container, int position) {
        InfoPage page = InfoPage.values()[position];

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.tool_info_page, container, false);
        layout.setTag(position);
        container.addView(layout);

        page.getViewHolder().onCreatedView(layout);
        page.getViewHolder().populateUI();

        return layout;
    }

    @Override
    public void destroyItemObject(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return InfoPage.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return InfoPage.values()[position].getTitle();
    }

    public void updateView(View pageView, int position){
        final InfoPage page = InfoPage.values()[position];

        new AsyncTask<InfoPage, InfoPage, InfoPage>() {
            @Override
            protected InfoPage doInBackground(InfoPage... infoPages) {
                page.updateFromHelper();
                return page;
            }

            @Override
            protected void onPostExecute(InfoPage result) {
                result.getViewHolder().updateUI(
                        result.getOverview(),
                        result.getContent());
            }
        }.execute();

    }
}
