package es.rafaco.devtools.view.overlay.screens.network;

import android.content.ContentUris;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readystatesoftware.chuck.internal.data.ChuckContentProvider;
import com.readystatesoftware.chuck.internal.data.HttpTransaction;
import com.readystatesoftware.chuck.internal.data.LocalCupboard;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.utils.DateUtils;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.storage.db.entities.Anr;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.AnrHelper;
import es.rafaco.devtools.view.overlay.screens.info.InfoCollection;

public class NetworkDetailScreen extends OverlayScreen {

    private Anr anr;
    private TextView out;
    private AnrHelper helper;
    private TextView title;
    private TextView subtitle;
    private TextView console;
    private HttpTransaction transaction;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public NetworkDetailScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Anr detail";
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
        out = toolHead.findViewById(R.id.out);

        title = toolHead.findViewById(R.id.detail_title);
        subtitle = toolHead.findViewById(R.id.detail_subtitle);
        console = toolHead.findViewById(R.id.detail_console);

        viewPager = bodyView.findViewById(R.id.viewpager);
        tabLayout = bodyView.findViewById(R.id.sliding_tabs);


        populateUI();

        if (!TextUtils.isEmpty(getParam())){
            final long transactionId = Long.parseLong(getParam());

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO

                    //Cursor cursor = getContext().getContentResolver().query(ContentUris.withAppendedId(ChuckContentProvider.TRANSACTION_URI, transactionId), null, null, null, null);
                    //transaction = LocalCupboard.getInstance().withCursor(cursor).get(HttpTransaction.class);

                }
            });
        }
    }

    private void populateUI() {
        NetworkPageAdapter adapter = new NetworkPageAdapter(getContext(), transaction);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void updateOutput() {
        helper = new AnrHelper();
        InfoCollection report = helper.parseToInfoGroup(anr);
        report.removeGroupEntries(1);

        title.setText(anr.getMessage() + " " + DateUtils.getElapsedTimeLowered(anr.getDate()));
        subtitle.setText(anr.getCause());
        out.setText(report.toString());
        console.setText(anr.getStacktrace());
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }
}
