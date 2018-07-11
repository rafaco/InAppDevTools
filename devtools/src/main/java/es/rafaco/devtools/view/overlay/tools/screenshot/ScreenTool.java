package es.rafaco.devtools.view.overlay.tools.screenshot;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.errors.Screen;
import es.rafaco.devtools.db.errors.ScreenDao;
import es.rafaco.devtools.utils.ThreadUtils;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.tools.OverlayTool;
import es.rafaco.devtools.view.overlay.OverlayToolsManager;
import es.rafaco.devtools.view.overlay.tools.info.InfoTool;

public class ScreenTool extends OverlayTool {

    private Button shotButton;
    private RecyclerView recyclerView;
    private ScreenAdapter adapter;

    public ScreenTool(OverlayToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Screen";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_screen; }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart(ViewGroup view) {
        initView(view);
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

    @Override
    public DecoratedToolInfo getReportInfo(){
        DecoratedToolInfo info = new DecoratedToolInfo(InfoTool.class,
                getFullTitle(),
                "Included last one of " + DevTools.getDatabase().screenDao().count(),
                3,
                ContextCompat.getColor(getContext(), R.color.rally_purple));
        return info;
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
        adapter = new ScreenAdapter(getContext(), new ArrayList<Screen>());

        recyclerView = toolView.findViewById(R.id.recycler_view);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
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
