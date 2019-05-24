package es.rafaco.inappdevtools.library.view.overlay.screens.network.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//#ifdef MODERN
import androidx.annotation.NonNull;
//#else
//@import android.support.annotation.NonNull;
//#endif

import com.readystatesoftware.chuck.internal.data.HttpTransaction;

import es.rafaco.inappdevtools.library.logic.integrations.wcviewpager.ObjectAtPositionPagerAdapter;

public class NetworkPagerAdapter extends ObjectAtPositionPagerAdapter {

    private HttpTransaction transaction;

    public NetworkPagerAdapter(HttpTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return NetworkPage.values()[position].getTitle();
    }

    @Override
    public int getCount() {
        return NetworkPage.values().length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public Object instantiateItemObject(ViewGroup container, int position) {
        NetworkPage page = NetworkPage.values()[position];

        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(page.getLayoutResId(), container, false);
        container.addView(layout);

        page.getViewHolder().onCreatedView(layout);
        page.getViewHolder().populateUI(transaction);

        return layout;
    }

    @Override
    public void destroyItemObject(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
