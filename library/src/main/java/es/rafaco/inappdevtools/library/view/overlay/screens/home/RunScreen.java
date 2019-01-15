package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.RunnableConfig;
import es.rafaco.inappdevtools.library.logic.watcher.crash.SimulatedException;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.components.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.HttpBinService;

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

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();
        addCustomItems(data);
        addDevToolsItems(data);
        addAndroidItems(data);

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(2, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    private void addCustomItems(List<Object> data) {
        data.add("Sample App");
        data.addAll(DevTools.getCustomRunnables());
    }

    private void addDevToolsItems(List<Object> data) {
        data.add("DevTools");
        data.add(new RunnableConfig( "Take Screen",
                R.drawable.ic_add_a_photo_rally_24dp,
                () -> DevTools.takeScreenshot()));

        data.add(new RunnableConfig("Breakpoint",
                R.drawable.ic_pan_tool_white_24dp,
                () -> DevTools.breakpoint(RunScreen.this)));

        data.add(new RunnableConfig("Simulate...",
                R.drawable.ic_input_white_24dp,
                () -> onSimulateButton()));
    }


    private void addAndroidItems(List<Object> data) {
        data.add("Android");
        data.add(new RunnableConfig("App Info",
                R.drawable.ic_info_white_24dp,
                () -> {
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.openAppSettings(getContext());
                }));
        data.add(new RunnableConfig("Dev Options",
                R.drawable.ic_developer_mode_white_24dp,
                () -> {
                    OverlayUIService.runAction(OverlayUIService.IntentAction.ICON, null);
                    AppUtils.openDeveloperOptions(getContext());
                }));

        data.add(new RunnableConfig("Force close",
                R.drawable.ic_close_white_24dp,
                () -> DevTools.forceCloseApp(false)));
        data.add(new RunnableConfig("Restart app",
                R.drawable.ic_replay_white_24dp,
                () -> DevTools.restartApp(false)));
    }

    private void onSimulateButton() {
        String[] optionsArray = getContext().getResources().getStringArray(R.array.simulations);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Simulations")
                .setCancelable(true)
                .setSingleChoiceItems(optionsArray, 0, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which){
                            case 0: //Traffic
                                HttpBinService.simulation(DevTools.getOkHttpClient());
                                break;
                            case 1: //Anr
                                onAnrButton();
                                break;
                            case 2: //Crash UI
                                onCrashUiButton();
                                break;
                            case 3: //Crash BG
                                onCrashBackButton();
                                break;
                        }
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    public void onAnrButton() {
        Log.i(DevTools.TAG, "ANR requested, sleeping main thread for a while...");
        ThreadUtils.runOnUiThread(() -> {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                Log.e(DevTools.TAG, "Something wrong happen", e);
            }
        });
    }

    public void onCrashUiButton() {
        Log.i(DevTools.TAG, "Simulated crash on the UI thread...");
        final Exception cause = new TooManyListenersException("The scenic panic make you pressed that button :)");
        ThreadUtils.runOnUiThread(() -> {
            throw new SimulatedException("Simulated crash on the UI thread", cause);
        });
    }

    public void onCrashBackButton() {
        Log.i(DevTools.TAG, "Simulated crash on a background thread...");
        final Exception cause = new TooManyListenersException("The scenic panic make you pressed that button :)");
        ThreadUtils.runOnBackThread(() -> {
            throw new SimulatedException("Simulated crash on a background thread", cause);
        });
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
