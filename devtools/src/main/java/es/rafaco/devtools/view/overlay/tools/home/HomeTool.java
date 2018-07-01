package es.rafaco.devtools.view.overlay.tools.home;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.tools.OverlayTool;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.OverlayToolsManager;
import es.rafaco.devtools.view.overlay.tools.errors.ErrorsTool;
import es.rafaco.devtools.view.overlay.tools.info.InfoHelper;
import es.rafaco.devtools.view.overlay.tools.info.InfoTool;
import es.rafaco.devtools.view.overlay.tools.report.ReportTool;
import es.rafaco.devtools.view.overlay.tools.screenshot.ScreenTool;

public class HomeTool extends OverlayTool {

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;
    private ArrayList<DecoratedToolInfo> dataList;

    public HomeTool(OverlayToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Home";
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
        InfoHelper helper = new InfoHelper(getContext());
        return "Welcome to " + helper.getAppName() + "'s DevTools";
    }

    private void initAdapter(View view) {
        adapter = new DecoratedToolInfoAdapter(this, new ArrayList<DecoratedToolInfo>());

        recyclerView = view.findViewById(R.id.home_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void updateList() {
        //TODO: getManager().requestHomeInfos()
        adapter.add(getManager().getTool(InfoTool.class).getHomeInfo());
        adapter.add(getManager().getTool(ErrorsTool.class).getHomeInfo());
        adapter.add(getManager().getTool(ScreenTool.class).getHomeInfo());
        adapter.add(getManager().getTool(ReportTool.class).getHomeInfo());

        adapter.notifyDataSetChanged();
        recyclerView.requestLayout();
    }

    //TODO: datalist not initialized any more!
    public void updateContent(Class<?> toolClass, String content){
        for (DecoratedToolInfo info: dataList){
            if (info.getClass().equals(toolClass)){
                info.setMessage(content);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
