package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.console.ConsoleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AppInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.DeviceInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.report.ReportScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourcesScreen;

public class HomeScreen extends Screen {

    public HomeScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.library_name);
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);

        //TODO: Home icon resize not working on first navigation
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               getScreenManager().getScreenLayer().toggleBackButton(false);
            }
        }, 100);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        AppInfoHelper appHelper = new AppInfoHelper(getContext());
        DeviceInfoHelper deviceHelper = new DeviceInfoHelper(getContext());
        String welcome = appHelper.getFormattedAppLong() + "\n"
                + deviceHelper.getFormattedDeviceLong();
        data.add(welcome);

        data.add(new RunButton("Info",
                R.drawable.ic_info_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(InfoScreen.class);
                    }
                }));

        data.add(new RunButton("Log",
                R.drawable.ic_history_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(LogScreen.class);
                    }
                }));

        RunButton sources = new RunButton("Sources",
                R.drawable.ic_code_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        OverlayService.performNavigation(SourcesScreen.class);
                    }
                });
        if (!IadtController.get().getSourcesManager().canSourceInspection()){
            sources.setColor(R.color.rally_gray);
            sources.setPerformer(new Runnable() {
                @Override
                public void run() {
                    Iadt.showMessage("Source inspection is DISABLED");
                }
            });
        }
        data.add(sources);

        data.add(new RunButton("View",
                R.drawable.ic_layers_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(InspectViewScreen.class);
                    }
                }));

        data.add(new RunButton("Storage",
                R.drawable.ic_storage_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        //OverlayService.performNavigation(StorageScreen.class);
                        HomeScreen.this.getScreenManager().hide();
                        PandoraBridge.storage();
                    }
                }));

        data.add(new RunButton("Console",
                R.drawable.ic_computer_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ConsoleScreen.class);
                    }
                }));
        
        
        data.add(new RunButton("Run",
                R.drawable.ic_run_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(RunScreen.class);
                    }
                }));

        data.add(new RunButton("Report",
                R.drawable.ic_send_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(ReportScreen.class);
                    }
                }));


        data.add(new RunButton("More",
                R.drawable.ic_more_vert_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(MoreScreen.class);
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
