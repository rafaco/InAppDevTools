package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//@import androidx.viewpager.widget.ViewPager;
//@import com.google.android.material.tabs.TabLayout;
//#else
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
//#endif

import java.util.Timer;
import java.util.TimerTask;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

public class InfoScreen extends Screen {

    InfoPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int currentPosition;
    private Timer updateTimer;

    public InfoScreen(ScreenManager manager) {
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
        pagerAdapter = new InfoPagerAdapter(getContext());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener());
        currentPosition = 0;
        startUpdateTimer();

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
    }

    @NonNull
    private ViewPager.OnPageChangeListener onPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                currentPosition = position;
                if (position == 0){
                    startUpdateTimer();
                }else{
                    stopUpdateTimer();
                }
            }
        };
    }

    private void startUpdateTimer() {
        final Handler handler = new Handler(Looper.getMainLooper());
        updateTimer = new Timer(false);
        TimerTask updateTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updatePage();
                        startUpdateTimer();
                    }
                });
            }
        };
        updateTimer.schedule(updateTimerTask, 2000);
    }

    private void stopUpdateTimer() {
        updateTimer.cancel();
    }

    private void updatePage() {
        final View pageView = viewPager.findViewWithTag(currentPosition);
        pagerAdapter.updateView(pageView, currentPosition);
    }

    @Override
    protected void onStop() {
        //Deliberately empty
    }

    @Override
    protected void onDestroy() {
        updateTimer.cancel();
    }
}
