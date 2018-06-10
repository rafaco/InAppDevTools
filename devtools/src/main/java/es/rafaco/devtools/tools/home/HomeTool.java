package es.rafaco.devtools.tools.home;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;

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

        addHomeInfo(array, "Exceptions",
                "Exception handler is activated. \n No exceptions stored.",
                "",
                getColor(R.color.rally_orange));

        addHomeInfo(array, "Send a Report",
                "You choose between bug or a suggestion, as well as what attachments you want to include. Later in gmail you can add additional comments... .",
                "",
                getColor(R.color.rally_green));

        adapter = new HomeInfoAdapter(this, array);
        homeList = getView().findViewById(R.id.home_list);
        homeList.setAdapter(adapter);
    }

    private void addHomeInfo(ArrayList<HomeInfo> array, String title, String message, String action, int color) {
        HomeInfo item = new HomeInfo();
        item.title = title;
        item.message = message;
        item.iconAction = action;
        item.color = color;
        array.add(item);
    }

    private int getColor(int color) {
        return ContextCompat.getColor(getContainer().getContext(), color);
    }

    @Override
    protected void onStop() {
        //TODO!! exe.Executer(cancel previous commmand)
    }

    @Override
    protected void onDestroy() {
    }
}
