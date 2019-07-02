package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.view.ViewGroup;

//#ifdef MODERN
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableItem;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.ConsoleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.AnalysisScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreensScreen;

public class MoreScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public MoreScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "More";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_flexible; }

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

        data.add(new RunnableItem("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(NetworkScreen.class);
                    }
                }));


        data.add(new RunnableItem("Screens",
                R.drawable.ic_photo_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(ScreensScreen.class);
                    }
                }));

        data.add(new RunnableItem("Errors",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(ErrorsScreen.class);
                    }
                }));

        data.add(new RunnableItem("Console",
                R.drawable.ic_computer_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(ConsoleScreen.class);
                    }
                }));

        data.add(new RunnableItem("Config",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.showMessage("TODO");
                    }
                }));

        /*data.add(new RunnableItem("Analysis",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(AnalysisScreen.class);
                    }
                }));*/

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
