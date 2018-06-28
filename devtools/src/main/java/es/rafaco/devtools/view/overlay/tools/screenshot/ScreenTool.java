package es.rafaco.devtools.view.overlay.tools.screenshot;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
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
import es.rafaco.devtools.view.overlay.tools.ToolsManager;

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
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void requestData() {

        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                ScreenDao screenDao = DevToolsDatabase.getInstance().screenDao();
                ArrayList<Screen> newScreenList = (ArrayList<Screen>) screenDao.getAll();
                updateList(newScreenList);
                //postViewModel.getAllPosts().observe(this, posts -> postsAdapter.setData(posts));
            }
        });
    }

    private void updateList(ArrayList<Screen> screens) {
        screenList.addAll(screens);

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getContext().getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
