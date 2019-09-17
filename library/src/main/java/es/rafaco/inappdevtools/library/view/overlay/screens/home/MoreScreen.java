package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreenshotsScreen;

public class MoreScreen extends Screen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public MoreScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "More";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
    }
    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        data.add("Playgrounds and old screens (pending to remove):\n" +
                " - All items has been mixed with logcat logs at Log Screen\n");

        data.add(new RunButton("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(NetworkScreen.class);
                    }
                }));


        data.add(new RunButton("Screens",
                R.drawable.ic_photo_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ScreenshotsScreen.class);
                    }
                }));

        data.add(new RunButton("Errors",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ErrorsScreen.class);
                    }
                }));

        /*data.add(new RunButton("Analysis",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(AnalysisScreen.class);
                    }
                }));*/

        data.add(new RunButton("Logcat",
                R.drawable.ic_android_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(LogcatScreen.class);
                    }
                }));

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(3, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
