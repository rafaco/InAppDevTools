package es.rafaco.devtools.view.overlay.screens.home;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.crash.SimulatedException;
import es.rafaco.devtools.logic.utils.AppUtils;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.network.HttpBinService;

public class RunScreen extends OverlayScreen {


    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public RunScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Run";
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

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();
        addCustomItems(data);
        addDevToolsItems(data);
        addAndroidItems(data);

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(2, data);
        recyclerView = getView().findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    private void addCustomItems(List<Object> data) {
        data.add("Sample App");
        Map<String, RunnableConfig> allRunnable = DevTools.getAllRunnable();
        data.addAll(allRunnable.values());
    }

    private void addDevToolsItems(List<Object> data) {
        data.add("DevTools");
        data.add(new RunnableConfig("screen",
                "Take Screen",
                R.drawable.ic_add_a_photo_rally_24dp,
                () -> DevTools.takeScreenshot()));

        data.add(new RunnableConfig("breakpoint",
                "Breakpoint",
                R.drawable.ic_pan_tool_white_24dp,
                () -> DevTools.breakpoint(RunScreen.this)));

        data.add(new RunnableConfig("sim_traffic",
                "Simulate Traffic",
                R.drawable.ic_cloud_queue_white_24dp,
                () -> HttpBinService.simulation(DevTools.getOkHttpClient())));

        data.add(new RunnableConfig("sim_anr",
                "Simulate ANR",
                R.drawable.ic_block_white_24dp,
                () ->  onAnrButton()));

        data.add(new RunnableConfig("sim_crash",
                "Simulate Crash",
                R.drawable.ic_bug_report_rally_24dp,
                () -> onCrashUiButton()));
    }


    private void addAndroidItems(List<Object> data) {
        data.add("DevTools");
        data.add(new RunnableConfig("app_info",
                "App Info",
                R.drawable.ic_info_white_24dp,
                () -> {
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.openAppSettings(getContext());
                }));
        data.add(new RunnableConfig("dev_options",
                "Dev Options",
                R.drawable.ic_developer_mode_white_24dp,
                () -> {
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.openDeveloperOptions(getContext());
                }));

        data.add(new RunnableConfig("close",
                "Force close",
                R.drawable.ic_close_white_24dp,
                () -> {
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.killMyProcess();
                }));
        data.add(new RunnableConfig("restart",
                "Restart app",
                R.drawable.ic_replay_white_24dp,
                () -> {
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.fullRestart(getContext());
                }));
    }

    public void onAnrButton() {
        Log.i(DevTools.TAG, "ANR requested, sleeping main thread for a while...");
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    Log.e(DevTools.TAG, "Something wrong happen", e);
                }
            }
        });
    }

    public void onCrashUiButton() {
        Log.i(DevTools.TAG, "Simulated crash on the UI thread...");
        final Exception cause = new TooManyListenersException("The scenic panic make you pressed that button :)");
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                throw new SimulatedException("Simulated crash on the UI thread", cause);
            }
        });
    }
}
