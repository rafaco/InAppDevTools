package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;

public class HomeScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public HomeScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "DevTools";
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

        InfoHelper helper = new InfoHelper();
        String welcome = helper.getFormattedAppLong() + "\n" + helper.getFormattedDeviceLong();
        data.add(welcome);

        data.add(new RunnableConfig("Run",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayUIService.performNavigation(RunScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Steps",
                R.drawable.ic_history_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayUIService.performNavigation(FriendlyLogScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Report",
                R.drawable.ic_send_rally_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayUIService.performNavigation(ReportScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Info",
                R.drawable.ic_info_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayUIService.performNavigation(InfoScreen.class);
                    }
                }));

        data.add(new RunnableConfig("Config",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        DevTools.showMessage("TODO");
                    }
                }));

        data.add(new RunnableConfig("Inspect",
                R.drawable.ic_developer_mode_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayUIService.performNavigation(InspectScreen.class);
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
