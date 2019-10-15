package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.text.TextUtils;
import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.integrations.PandoraBridge;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.InfoScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class InspectViewScreen extends Screen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public InspectViewScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Inspect View";
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

        String viewOverview = "";
        viewOverview += "App on " + RunningTasksUtils.getTopActivityStatus();
        viewOverview += Humanizer.newLine();
        viewOverview += RunningTasksUtils.getCount() + " tasks with " + RunningTasksUtils.getActivitiesCount() + " activities";
        viewOverview += Humanizer.newLine();
        viewOverview += "Top activity is " + RunningTasksUtils.getTopActivity();

        data.add(new CardData("Info",
                viewOverview,
                R.string.gmd_view_carousel,
                new Runnable() {
                    @Override
                    public void run() { OverlayService.performNavigation(InfoScreen.class, "0");
                    }
                }));


        final String pathFromClassName = IadtController.get().getSourcesManager()
                .getPathFromClassName(RunningTasksUtils.getTopActivityClassName());
        if (!TextUtils.isEmpty(pathFromClassName)) {
            data.add(new RunButton("View sources",
                    R.drawable.ic_code_white_24dp, new Runnable() {
                @Override
                public void run() {
                    OverlayService.performNavigation(SourceDetailScreen.class,
                            SourceDetailScreen.buildParams(pathFromClassName, -1));
                }
            }));
        }

        data.add(new RunButton( "Take Screenshot",
                R.drawable.ic_add_a_photo_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().takeScreenshot();
                    }
                }));


        data.add("Layout inspector");

        data.add(new RunButton("Select element",
                R.drawable.ic_touch_app_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        InspectViewScreen.this.getScreenManager().hide();
                        PandoraBridge.select();
                    }
                }));

        data.add(new RunButton("Browse hierarchy",
                R.drawable.ic_layers_white_24dp, new Runnable() {
            @Override
            public void run() {
                InspectViewScreen.this.getScreenManager().hide();
                PandoraBridge.hierarchy();
            }
        }));

        data.add(new RunButton("Take Measure",
                R.drawable.ic_format_line_spacing_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        InspectViewScreen.this.getScreenManager().hide();
                        PandoraBridge.measure();
                    }
                }));

        data.add(new RunButton("Show gridline",
                R.drawable.ic_grid_on_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        PandoraBridge.grid();
                    }
                }));

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(2, data);
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
