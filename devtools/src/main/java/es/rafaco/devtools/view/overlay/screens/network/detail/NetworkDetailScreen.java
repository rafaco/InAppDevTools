package es.rafaco.devtools.view.overlay.screens.network.detail;

import android.content.ContentUris;
import android.database.Cursor;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.readystatesoftware.chuck.internal.data.ChuckContentProvider;
import com.readystatesoftware.chuck.internal.data.HttpTransaction;
import com.readystatesoftware.chuck.internal.data.LocalCupboard;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class NetworkDetailScreen extends OverlayScreen {

    private HttpTransaction transaction;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public NetworkDetailScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Network detail";
    }

    @Override
    public int getBodyLayoutId() {
        return R.layout.tool_network_detail_body;
    }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.crash_detail;
    }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup toolHead) {

        viewPager = getView().findViewById(R.id.viewpager);
        tabLayout = getView().findViewById(R.id.sliding_tabs);

        if (!TextUtils.isEmpty(getParam())){
            final long transactionId = Long.parseLong(getParam());
            Cursor cursor = getContext().getContentResolver().query(ContentUris.withAppendedId(ChuckContentProvider.TRANSACTION_URI, transactionId), null, null, null, null);
            transaction = LocalCupboard.getInstance().withCursor(cursor).get(HttpTransaction.class);

            populateUI();
        }
    }

    private void populateUI() {
        NetworkPagerAdapter adapter = new NetworkPagerAdapter(transaction);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
