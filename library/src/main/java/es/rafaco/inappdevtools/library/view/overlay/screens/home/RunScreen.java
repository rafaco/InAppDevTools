package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.content.DialogInterface;
import android.util.Log;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableItem;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.activities.WelcomeDialogActivity;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.layers.Layer;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.HttpBinService;

public class RunScreen extends Screen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public RunScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Run";
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
        data.add("Your App");
        data.addAll(IadtController.get().getRunnableManager().getAll());
    }

    private void addDevToolsItems(List<Object> data) {
        data.add("Iadt");
        data.add(new RunnableItem( "Take Screenshot",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().takeScreenshot();
                    }
                }));

        data.add(new RunnableItem("Codepoint",
                R.drawable.ic_pan_tool_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        Iadt.codepoint(RunScreen.this);
                    }
                }));

        data.add(new RunnableItem("Simulate...",
                R.drawable.ic_input_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        RunScreen.this.onSimulateButton();
                    }
                }));

        data.add(new RunnableItem("DISABLE...",
                R.drawable.ic_power_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().showIcon();
                        WelcomeDialogActivity.open(WelcomeDialogActivity.IntentAction.DISABLE,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Iadt.showMessage("Developer tools disabled!");
                                    }
                                },
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Iadt.showMessage("Developer tools NOT disabled");
                                    }
                                });
                    }
                }));
    }


    private void addAndroidItems(List<Object> data) {
        data.add("Android");
        data.add(new RunnableItem("App Info",
                R.drawable.ic_info_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().showIcon();
                        AppUtils.openAppSettings(RunScreen.this.getContext());
                    }
                }));
        data.add(new RunnableItem("Dev Options",
                R.drawable.ic_developer_mode_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().showIcon();
                        AppUtils.openDeveloperOptions(RunScreen.this.getContext());
                    }
                }));

        data.add(new RunnableItem("Restart app",
                R.drawable.ic_replay_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().restartApp(false);
                    }
                }));

        data.add(new RunnableItem("Force close",
                R.drawable.ic_power_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().forceCloseApp(false);
                    }
                }));
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
                                HttpBinService.simulation(Iadt.getOkHttpClient());
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
        alertDialog.getWindow().setType(Layer.getLayoutType());
        alertDialog.show();
    }

    public void onAnrButton() {
        Log.i(Iadt.TAG, "ANR requested, sleeping main thread for a while...");
        ThreadUtils.runOnMain(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((long)10 * 1000);
                } catch (InterruptedException e) {
                    Log.e(Iadt.TAG, "Something wrong happen", e);
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void onCrashUiButton() {
        IadtController.get().crashUiThread();
    }

    public void onCrashBackButton() {
        IadtController.get().crashBackgroundThread();
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
