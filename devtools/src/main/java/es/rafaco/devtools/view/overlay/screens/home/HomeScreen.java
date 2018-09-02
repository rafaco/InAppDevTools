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
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;

public class HomeScreen extends OverlayScreen {

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
        initAdapter(view);
        updateList();
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
