package es.rafaco.devtools.view.overlay.tools.home;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.tools.OverlayTool;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.ToolsManager;
import es.rafaco.devtools.view.overlay.tools.errors.ErrorsTool;
import es.rafaco.devtools.view.overlay.tools.info.InfoHelper;
import es.rafaco.devtools.view.overlay.tools.info.InfoTool;
import es.rafaco.devtools.view.overlay.tools.report.ReportTool;
import es.rafaco.devtools.view.overlay.tools.screenshot.ScreenTool;

public class HomeTool extends OverlayTool {

    private DecoratedToolInfoAdapter adapter;
    private ListView homeList;
    private TextView welcome;
    private ArrayList<DecoratedToolInfo> dataList;

    public HomeTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Home";
    }

    @Override
    public String getLayoutId() {
        return "tool_home";
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onStart(View toolView) {
        initView(toolView);
        initAdapter();

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

    private void initAdapter() {
        dataList = new ArrayList<>();
        adapter = new DecoratedToolInfoAdapter(this, dataList);
        homeList = getView().findViewById(R.id.home_list);
        homeList.setAdapter(adapter);
    }

    private void updateList() {
        //TODO: getManager().requestHomeInfos()
        dataList.add(getManager().getTool(InfoTool.class).getHomeInfo());
        dataList.add(getManager().getTool(ErrorsTool.class).getHomeInfo());
        dataList.add(getManager().getTool(ScreenTool.class).getHomeInfo());
        dataList.add(getManager().getTool(ReportTool.class).getHomeInfo());

        adapter.notifyDataSetChanged();
    }

    public void updateContent(Class<?> toolClass, String content){
        for (DecoratedToolInfo info: dataList){
            if (info.getClass().equals(toolClass)){
                info.setMessage(content);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
