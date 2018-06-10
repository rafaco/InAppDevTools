package es.rafaco.devtools.tools.home;

import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.tools.info.InfoTool;
import es.rafaco.devtools.tools.report.ReportTool;
import es.rafaco.devtools.tools.shell.ShellTool;

public class HomeTool extends Tool {

    private HomeInfoAdapter adapter;
    private ListView homeList;

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
        initAdapter();
    }

    private void initAdapter() {
        ArrayList<HomeInfo> array = new ArrayList<>();
        array.add(getManager().getTool(InfoTool.class).getHomeInfo());
        array.add(getManager().getTool(ShellTool.class).getHomeInfo());
        array.add(getManager().getTool(ReportTool.class).getHomeInfo());

        adapter = new HomeInfoAdapter(this, array);
        homeList = getView().findViewById(R.id.home_list);
        homeList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        //TODO!! exe.Executer(cancel previous commmand)
    }

    @Override
    protected void onDestroy() {
    }
}
