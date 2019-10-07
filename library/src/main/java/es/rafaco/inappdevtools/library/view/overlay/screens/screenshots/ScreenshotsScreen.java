package es.rafaco.inappdevtools.library.view.overlay.screens.screenshots;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

//#ifdef ANDROIDX
//@import androidx.core.view.ViewCompat;
//@import androidx.recyclerview.widget.DefaultItemAnimator;
//@import androidx.recyclerview.widget.GridLayoutManager;
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.ScreenshotDao;
import es.rafaco.inappdevtools.library.logic.utils.ThreadUtils;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

public class ScreenshotsScreen extends Screen {

    private Button shotButton;
    private RecyclerView recyclerView;
    private ScreenshotAdapter adapter;

    public ScreenshotsScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Screens";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_screenshots; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        initView(view);
    }


    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
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
        IadtController.get().takeScreenshot();
    }



    private void initAdapter(View toolView) {
        adapter = new ScreenshotAdapter(getContext(), new ArrayList<Screenshot>());

        recyclerView = toolView.findViewById(R.id.recycler_view);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter.setRecycledView(recyclerView);
        recyclerView.setAdapter(adapter);
    }

    private void requestData() {
        ThreadUtils.runOnBack("Iadt-GetScreenshot",
                new Runnable() {
            @Override
            public void run() {
                ScreenshotDao screenshotDao = DevToolsDatabase.getInstance().screenshotDao();
                final ArrayList<Screenshot> newScreenshotList = (ArrayList<Screenshot>) screenshotDao.getAll();
                ThreadUtils.runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        updateList(newScreenshotList);
                    }
                });
                //screenshotDao.getAllLive().observe(ScreenshotsScreen.this, screens -> updateLive(screens));
            }
        });
    }

    /*private void updateLive(List<Screenshot> screens) {
        adapter.setData(screens);
    }*/

    private void updateList(ArrayList<Screenshot> screenshots) {
        adapter.setData(screenshots);
        //screenList.addAll(screenshots);
        //adapter.notifyDataSetChanged();
    }
}
