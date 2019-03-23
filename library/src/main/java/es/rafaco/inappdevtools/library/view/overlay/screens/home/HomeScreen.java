package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreensScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;

public class HomeScreen extends OverlayScreen {

    public HomeScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.library_name);
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_flexible; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        InfoHelper helper = new InfoHelper();
        String welcome = helper.getFormattedAppLong() + "\n" + helper.getFormattedDeviceLong();
        data.add(welcome);

        data.add(new RunnableConfig("Info",
                R.drawable.ic_info_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(InfoScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Run",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(RunScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Report",
                R.drawable.ic_send_rally_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(ReportScreen.class);
                    }
                }));


        data.add(new RunnableConfig("History",
                R.drawable.ic_history_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(FriendlyLogScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Storage",
                R.drawable.ic_storage_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        //OverlayUIService.performNavigation(StorageScreen.class);
                        HomeScreen.this.getScreenManager().hide();
                        PandoraBridge.storage();
                    }
                }));

        data.add(new RunnableConfig("View",
                R.drawable.ic_layers_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(InspectViewScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Logcat",
                R.drawable.ic_android_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(LogScreen.class);
                    }
                }));


        data.add(new RunnableConfig("Code",
                R.drawable.ic_code_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(SourcesScreen.class);
                    }
                }));

        data.add(new RunnableConfig("More",
                R.drawable.ic_more_vert_rally_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(MoreScreen.class);
                    }
                }));

        return data;
    }

    private void initAdapter(List<Object> data) {
        FlexibleAdapter adapter = new FlexibleAdapter(3, data);
        RecyclerView recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }
}
