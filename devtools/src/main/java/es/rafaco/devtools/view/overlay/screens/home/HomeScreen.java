package es.rafaco.devtools.view.overlay.screens.home;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.commands.CommandsScreen;
import es.rafaco.devtools.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;
import es.rafaco.devtools.view.overlay.screens.report.ReportScreen;
import es.rafaco.devtools.view.overlay.screens.storage.StorageScreen;
import es.rafaco.devtools.view.utils.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.utils.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;

public class HomeScreen extends OverlayScreen {

    private final boolean ICON_STYLE = true;

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;
    private ArrayList<DecoratedToolInfo> dataList;

    public HomeScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "DevTools";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_home_body; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {
        initView(view);

        if (ICON_STYLE){
            view.findViewById(R.id.home_list).setVisibility(View.GONE);
            welcome.setVisibility(View.GONE);

            view.findViewById(R.id.info_button)
                    .setOnClickListener(v ->
                            OverlayUIService.performNavigationStep(new NavigationStep(InfoScreen.class, null)));

            view.findViewById(R.id.run_button)
                    .setOnClickListener(v ->
                            OverlayUIService.performNavigationStep(new NavigationStep(RunScreen.class, null)));

            view.findViewById(R.id.report_button)
                    .setOnClickListener(v ->
                            OverlayUIService.performNavigationStep(new NavigationStep(ReportScreen.class, null)));

            view.findViewById(R.id.friendly_button)
                    .setOnClickListener(v ->
                            OverlayUIService.performNavigationStep(new NavigationStep(FriendlyLogScreen.class, null)));

            view.findViewById(R.id.advanced_button)
                    .setOnClickListener(v ->
                            OverlayUIService.performNavigationStep(new NavigationStep(AdvancedScreen.class, null)));
        }
        else{
            view.findViewById(R.id.home_icons).setVisibility(View.GONE);
            view.findViewById(R.id.home_icons2).setVisibility(View.GONE);
            initAdapter(view);
            updateList();
        }
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    private void initView(View toolView) {
        welcome = toolView.findViewById(R.id.home_welcome);
        welcome.setText(getWelcomeMessage());
    }

    public String getWelcomeMessage(){
        InfoHelper helper = new InfoHelper();
        return "Welcome to " + helper.getAppName() + "'s DevTools";
    }

    private void initAdapter(View view) {
        adapter = new DecoratedToolInfoAdapter(getContext(), new ArrayList<DecoratedToolInfo>());

        recyclerView = view.findViewById(R.id.home_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void updateList() {
        adapter.replaceAll(DevTools.getToolManager().getHomeInfos());
        recyclerView.requestLayout();
    }

    //TODO: datalist not initialized any more!
    public void updateContent(Class<?> toolClass, String content){
        if (dataList!=null && dataList.size()>0){
            for (DecoratedToolInfo info: dataList){
                if (info.getClass().equals(toolClass)){
                    info.setMessage(content);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
