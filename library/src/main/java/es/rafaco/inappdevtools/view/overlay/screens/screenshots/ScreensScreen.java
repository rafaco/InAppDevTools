package es.rafaco.inappdevtools.view.overlay.screens.screenshots;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import es.rafaco.inappdevtools.DevTools;
import es.rafaco.inappdevtools.R;
import es.rafaco.inappdevtools.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.storage.db.entities.Screen;
import es.rafaco.inappdevtools.storage.db.entities.ScreenDao;
import es.rafaco.inappdevtools.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.view.overlay.screens.OverlayScreen;

public class ScreensScreen extends OverlayScreen {

    private Button shotButton;
    private RecyclerView recyclerView;
    private ScreenAdapter adapter;

    public ScreensScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Screens";
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

        adapter.setRecycledView(recyclerView);
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
                //screenDao.getAllLive().observe(ScreensScreen.this, screens -> updateLive(screens));
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