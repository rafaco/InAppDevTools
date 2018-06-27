package es.rafaco.devtools.view.overlay.tools.home;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.tools.Tool;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.tools.ToolsManager;
import es.rafaco.devtools.view.overlay.tools.errors.ErrorsTool;
import es.rafaco.devtools.view.overlay.tools.info.InfoHelper;
import es.rafaco.devtools.view.overlay.tools.info.InfoTool;
import es.rafaco.devtools.view.overlay.tools.report.ReportTool;
import es.rafaco.devtools.view.overlay.tools.commands.CommandsTool;

public class HomeTool extends Tool {

    private DecoratedToolInfoAdapter adapter;
    private ListView homeList;
    private TextView welcome;

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
        ArrayList<DecoratedToolInfo> array = new ArrayList<>();
        array.add(getManager().getTool(InfoTool.class).getHomeInfo());
        array.add(getManager().getTool(ErrorsTool.class).getHomeInfo());
        array.add(getManager().getTool(ReportTool.class).getHomeInfo());

        adapter = new DecoratedToolInfoAdapter(this, array);
        homeList = getView().findViewById(R.id.home_list);
        homeList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
