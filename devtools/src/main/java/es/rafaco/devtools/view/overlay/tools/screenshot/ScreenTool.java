package es.rafaco.devtools.view.overlay.tools.screenshot;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.db.errors.ScreenDao;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.tools.Tool;
import es.rafaco.devtools.view.overlay.ToolsManager;

public class ScreenTool extends Tool {

    private Button shotButton;
    private RecyclerView recyclerView;
    private ScreenAdapter adapter;
    private ArrayList<Screen> screenList;

    public ScreenTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Screen";
    }

    @Override
    public String getLayoutId() {
        return "tool_screen";
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onStart(View toolView) {
        initView(toolView);
    }


    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {

    }

    @Override
    public DecoratedToolInfo getHomeInfo(){

        final DecoratedToolInfo info = new DecoratedToolInfo(ScreenTool.class,
                getFullTitle(),
                "No screen saved.",
                3,
                ContextCompat.getColor(getContext(), R.color.rally_purple));

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                final int count = DevTools.getDatabase().screenDao().count();
                if (count > 0){
                    getManager().updateHomeInfoContent(ScreenTool.class, count + " screens saved." );
                }
            }
        });

        return  info;
    }

    private void initView(View toolView) {
        initShotButton(toolView);
        initAdapter(toolView);

        requestData();
    }

    private void initShotButton(View toolView) {
        shotButton = toolView.findViewById(R.id.shot_button);
        shotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScreenshotButton();
            }
        });
    }

    private void onScreenshotButton() {
        DevTools.takeScreenshot();
    }



    private void initAdapter(View toolView) {
        recyclerView = (RecyclerView) toolView.findViewById(R.id.recycler_view);

        screenList = new ArrayList<>();
        adapter = new ScreenAdapter(getContext(), screenList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void requestData() {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                ScreenDao screenDao = DevToolsDatabase.getInstance().screenDao();
                final ArrayList<Screen> newScreenList = (ArrayList<Screen>) screenDao.getAll();
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateList(newScreenList);
                    }
                });
                //screenDao.getAllLive().observe(ScreenTool.this, screens -> updateLive(screens));
            }
        });
    }

    /*private void updateLive(List<Screen> screens) {
        adapter.setData(screens);
    }*/

    private void updateList(ArrayList<Screen> screens) {
        adapter.setData(screens);
        //screenList.addAll(screens);
        //adapter.notifyDataSetChanged();
    }
}
