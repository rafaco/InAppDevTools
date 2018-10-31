package es.rafaco.devtools.view.overlay.screens.home;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


    public RunScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Run";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_run; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {
        initView(view);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    private void initView(View view) {

        view.findViewById(R.id.env_button)
                .setOnClickListener(v ->
                        DevTools.showMessage("Not already implemented"));

        view.findViewById(R.id.screen_button)
                .setOnClickListener(v ->
                        DevTools.takeScreenshot());

        view.findViewById(R.id.breakpoint_button)
                .setOnClickListener(v ->
                        DevTools.breakpoint(this));

        view.findViewById(R.id.sim_traffic_button)
                .setOnClickListener(v ->
                        HttpBinService.simulation(DevTools.getOkHttpClient()));

        view.findViewById(R.id.anr_button)
                .setOnClickListener(v ->
                        onAnrButton());

        view.findViewById(R.id.crash_button)
                .setOnClickListener(v ->
                        onCrashUiButton());

        view.findViewById(R.id.app_info_button)
                .setOnClickListener(v ->{
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.openAppSettings(getContext());
                });

        view.findViewById(R.id.dev_options_button)
                .setOnClickListener(v ->{
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.openDeveloperOptions(getContext());
                });

        view.findViewById(R.id.close_button)
                .setOnClickListener(v -> {
                        OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                        AppUtils.killMyProcess();
                });

        view.findViewById(R.id.restart_button)
                .setOnClickListener(v -> {
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.fullRestart(getContext());
                });
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
