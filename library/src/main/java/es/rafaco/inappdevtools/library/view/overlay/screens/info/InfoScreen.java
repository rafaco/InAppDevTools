package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;


public class InfoScreen extends OverlayScreen {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public InfoScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_info_body; }

    @Override
    protected void onCreate() {
        //Deliberately empty
    }

    @Override
    public int getHeadLayoutId() { return R.layout.tool_info_head; }

    @Override
    protected void onStart(ViewGroup view) {
        viewPager = getView().findViewById(R.id.viewpager);
        tabLayout = getView().findViewById(R.id.sliding_tabs);
        populateToolbar();
    }

    private void populateToolbar() {
        InfoPagerAdapter adapter = new InfoPagerAdapter(getContext());
        viewPager.setAdapter(adapter);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStop() {
        //Deliberately empty
    }

    @Override
    protected void onDestroy() {
        //Deliberately empty
    }
}
