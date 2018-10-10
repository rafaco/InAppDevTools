package es.rafaco.devtools.view.overlay.screens.network;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

public class NetworkPageAdapter extends PagerAdapter {

    private HttpTransaction mTransaction;
    private Context mContext;

    public NetworkPageAdapter(Context context, HttpTransaction transaction) {
        mContext = context;
        mTransaction = transaction;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        NetworkPage page = NetworkPage.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(page.getLayoutResId(), collection, false);
        page.getViewHolder().onCreatedView(layout);
        page.getViewHolder().populateUI(mTransaction);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return NetworkPage.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        NetworkPage customPagerEnum = NetworkPage.values()[position];
        return customPagerEnum.getTitle();
    }
/*
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        //PageInfo item = (PageInfo) object;
        NetworkPage page = NetworkPage.values()[position];
        int width = page.getViewHolder().getContentView().getMeasuredWidth();
        int height = page.getViewHolder().getContentView().getMeasuredHeight();

        ViewPager pager = (ViewPager) container;
        pager.setLayoutParams(new LinearLayout.LayoutParams(width, Math.max(height, 400)));
    }*/

}