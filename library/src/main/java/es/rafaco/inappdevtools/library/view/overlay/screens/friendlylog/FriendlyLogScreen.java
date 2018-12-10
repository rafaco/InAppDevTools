package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

import android.content.DialogInterface;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.tools.ToolHelper;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogHelper;

public class FriendlyLogScreen extends OverlayScreen {

    private FriendlyLogDataSourceFactory dataSourceFactory;
    private FriendlyLogAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;

    public LiveData<PagedList<Friendly>> logList;
    private final int pageSize = 20;
    private ToolBarHelper toolbarHelper;
    private int selectedLogLevel = 2;

    private enum ScrollStatus { TOP, MIDDLE, BOTTOM }
    private ScrollStatus currentScrollStatus;

    public FriendlyLogScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Friendly Log";
    }

    @Override
    public boolean needNestedScroll() {
        return false;
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_friendlylog_body; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.logcat;
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart(ViewGroup toolHead) {

        initToolbar();
        initView(bodyView);
        initAdapter();

        initScroll();
    }

    private void initView(ViewGroup view) {
        welcome = view.findViewById(R.id.welcome);
        welcome.setVisibility(View.GONE);

        recyclerView = getView().findViewById(R.id.list);
    }

    private void initAdapter(){
        adapter = new FriendlyLogAdapter();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                if (positionStart != 0 && !recyclerView.canScrollVertically(1)){
                    Log.v(DevTools.TAG, "Scrolling onItemRangeInserted( positionStart="+positionStart+" itemCount="+itemCount+" size="+adapter.getItemCount()+" isBottom="+!recyclerView.canScrollVertically(1));
                    scrollToBottom();
                }
            }
        });

        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        PagedList.Config myPagingConfig = new PagedList.Config.Builder()
                .setPageSize(20)
                .setPrefetchDistance(60)
                .build();
        dataSourceFactory = new FriendlyLogDataSourceFactory(dao);
        dataSourceFactory.setText("");
        dataSourceFactory.setLevelString(getSelectedVerbosity());
        logList = new LivePagedListBuilder<>(dataSourceFactory, myPagingConfig).build();
        logList.observe(ProcessLifecycleOwner.get(), adapter::submitList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        //((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDestroy() {

    }

    //region [ TOOL BAR ]

    private void initToolbar() {
        toolbarHelper = new ToolBarHelper(getToolbar());
        toolbarHelper.initSearchButtons(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dataSourceFactory.setText(newText);
                logList.getValue().getDataSource().invalidate();
                return false;
            }
        });
        toolbarHelper.showAllMenuItem();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_level) {
            onLevelButton();
        }
        else if (selected == R.id.action_save) {
            //onSaveButton();
        }
        else if (selected == R.id.action_delete) {
            onClearAll();
        }
        else{
            DevTools.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    private void onLevelButton() {
        String[] levelsArray = getContext().getResources().getStringArray(R.array.log_levels);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Select log level")
                .setCancelable(true)
                .setSingleChoiceItems(levelsArray, selectedLogLevel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which!=selectedLogLevel) {
                            selectedLogLevel = which;
                            dataSourceFactory.setLevelString(getSelectedVerbosity());
                            logList.getValue().getDataSource().invalidate();
                        }
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    public String getSelectedVerbosity() {
        String[] levelsArray = getContext().getResources().getStringArray(R.array.log_levels);
        return levelsArray[selectedLogLevel].substring(0, 1).toUpperCase();
    }

    private void onSaveButton() {
        ToolHelper helper = new LogHelper();
        String path = (String) helper.getReportPath();
        DevTools.showMessage("Log stored to " + path);
    }

    private void onClearAll() {
        DevTools.getDatabase().friendlyDao().deleteAll();
        adapter.notifyDataSetChanged();
    }

    //endregion

    //region [ SCROLL ]

    private void initScroll() {
        //recyclerView.addOnScrollListener(scrollListener);
        //currentScrollStatus = ScrollStatus.BOTTOM;
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (currentScrollStatus != ScrollStatus.BOTTOM && !recyclerView.canScrollVertically(1)) {
                currentScrollStatus = ScrollStatus.BOTTOM;
                Log.v(DevTools.TAG, "Scroll reached bottom ("+dx+","+dy+")");
            }
            else if (currentScrollStatus != ScrollStatus.TOP && !recyclerView.canScrollVertically(-1)) {
                currentScrollStatus = ScrollStatus.TOP;
                Log.v(DevTools.TAG, "Scroll reached top ("+dx+","+dy+")");
            }else{
                Log.v(DevTools.TAG, "Scroll at middle ("+dx+","+dy+")");
                currentScrollStatus = ScrollStatus.MIDDLE;
            }
        }
    };

    private void scrollToBottom() {
        recyclerView.post(() -> recyclerView.scrollToPosition(adapter.getItemCount() - 1));
    }

    //endregion

}
