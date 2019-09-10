package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//#ifdef ANDROIDX
//@import androidx.appcompat.app.AlertDialog;
//@import androidx.appcompat.widget.SearchView;
//@import androidx.recyclerview.widget.RecyclerView;
//@import androidx.recyclerview.widget.LinearLayoutManager;
//@import androidx.lifecycle.LiveData;
//@import androidx.lifecycle.Observer;
//@import androidx.lifecycle.ProcessLifecycleOwner;
//@import androidx.paging.LivePagedListBuilder;
//@import androidx.paging.PagedList;
//#else
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogDataSourceFactory;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterDialog;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterStore;
import es.rafaco.inappdevtools.library.logic.log.filter.LogUiFilter;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.log.reader.LogcatReaderService;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreenHelper;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.layers.OverlayLayer;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatHelper;
import es.rafaco.inappdevtools.library.view.utils.ToolBarHelper;

public class LogScreen extends OverlayScreen {

    private LogDataSourceFactory dataSourceFactory;
    private LogAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;

    public LiveData logList;
    private ToolBarHelper toolbarHelper;
    private LogFilterHelper logFilterHelper;
    private LogFilterDialog filterDialog;

    private enum ScrollStatus { TOP, MIDDLE, BOTTOM }
    private ScrollStatus currentScrollStatus;

    public LogScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Logs";
    }

    @Override
    public boolean needNestedScroll() {
        return false;
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_log_body; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.friendlylog;
    }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup toolHead) {

        initToolbar();
        initView(bodyView);

        initFilterHelper();
        initAdapter();
        initScroll();
    }

    private void initFilterHelper() {
        logFilterHelper = getParams();
    }

    @Override
    protected void onStop() {
        //TODO: removeLifecycleObserver();
        Intent intent = LogcatReaderService.getStartIntent(getContext(), "Update timing");
        LogcatReaderService.enqueueWork(getContext(), intent);

        //updateParams();
        if (filterDialog !=null && filterDialog.getDialog()!=null){
            filterDialog.getDialog().dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }



    private void initView(ViewGroup view) {
        welcome = view.findViewById(R.id.welcome);
        welcome.setVisibility(View.GONE);

        recyclerView = getView().findViewById(R.id.list);
    }

    //region [ ADAPTER ]

    private void initAdapter(){
        adapter = new LogAdapter();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (positionStart != 0 && !recyclerView.canScrollVertically(1)){
                    //Log.v(Iadt.TAG, "Scrolling onItemRangeInserted( positionStart="+positionStart
                    // +" itemCount="+itemCount+" size="+adapter.getItemCount()
                    // +" isBottom="+!recyclerView.canScrollVertically(1) + ")");
                    scrollToBottom();
                }
            }
        });

        PagedList.Config myPagingConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(4)
                .setPageSize(20)
                .setPrefetchDistance(60)
                .build();

        initLiveDataWithFriendlyLog(myPagingConfig);

        logList.observe(ProcessLifecycleOwner.get(), new Observer<PagedList<Friendly>>() {
            @Override
            public void onChanged(PagedList<Friendly> pagedList) {
                //FriendlyLog.log("V", "Iadt", "IadtLiveData", "Observer onChanged");
                adapter.submitList(pagedList);
            }
        });
        //Log.d("IadtLiveData", "Observer registered to PagedList from FriendlyDao");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        Intent intent = LogcatReaderService.getStartIntent(getContext(), "Started from LogScreen");
        LogcatReaderService.enqueueWork(getContext(), intent);
    }

    private void initLiveDataWithFriendlyLog(PagedList.Config myPagingConfig) {
        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        dataSourceFactory = new LogDataSourceFactory(dao, logFilterHelper.getBackFilter());
        logList = new LivePagedListBuilder<>(dataSourceFactory, myPagingConfig).build();
    }

    //endregion

    //region [ TOOL BAR ]

    private void initToolbar() {
        toolbarHelper = new ToolBarHelper(getToolbar());
        toolbarHelper.initSearchFilterButtons(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dataSourceFactory.getConfig().setText(newText);
                adapter.getCurrentList().getDataSource().invalidate();
                return false;
            }
        });
        toolbarHelper.showAllMenuItem();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_tune) {
            //OverlayUIService.performNavigation(AnalysisScreen.class);
            onTuneButton();
        }
        else if (selected == R.id.action_level) {
            onLevelButton();
        }
        else if (selected == R.id.action_save) {
            //onSaveButton();
        }
        else if (selected == R.id.action_delete) {
            onClearButton();
        }
        else{
            Iadt.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    private void onTuneButton() {
        filterDialog = new LogFilterDialog(getContext(), adapter, logFilterHelper);
        filterDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateParams(logFilterHelper);
            }
        });
        filterDialog.getDialog().show();
    }

    private void onLevelButton() {
        String[] levelsArray = getContext().getResources().getStringArray(R.array.log_levels);
        final LogUiFilter filter = logFilterHelper.getUiFilter();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Select log level")
                .setCancelable(true)
                .setSingleChoiceItems(levelsArray, filter.getSeverityInt(), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != filter.getSeverityInt()) {
                            filter.setSeverityInt(which);
                            adapter.getCurrentList().getDataSource().invalidate();
                        }
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(OverlayLayer.getLayoutType());
        alertDialog.show();
    }

    private void onSaveButton() {
        OverlayScreenHelper helper = new LogcatHelper();
        String path = (String) helper.getReportPath();
        Iadt.showMessage("Log stored to " + path);
    }

    private void onClearButton() {
        final boolean[] checked = { false };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Delete all log in DB?")
                //.setMessage("Do you really want to wipe the db with the log history? You will not be able to see it or use it in your reports")
                .setCancelable(true)
                .setMultiChoiceItems(new String[]{"Clear also logcat buffer"}, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        checked[0] = b;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearAll(checked[0]);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(OverlayLayer.getLayoutType());
        alertDialog.show();
    }

    private void clearAll(boolean clearLogcatBuffer) {
        if (clearLogcatBuffer){
            IadtController.cleanSession();
        }
        IadtController.get().getDatabase().friendlyDao().deleteAll();
        FriendlyLog.log("D", "Iadt", "Delete","Friendly log history deleted by user");
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
                Log.v(Iadt.TAG, "Scroll reached bottom ("+dx+","+dy+")");
            }
            else if (currentScrollStatus != ScrollStatus.TOP && !recyclerView.canScrollVertically(-1)) {
                currentScrollStatus = ScrollStatus.TOP;
                Log.v(Iadt.TAG, "Scroll reached top ("+dx+","+dy+")");
            }else{
                Log.v(Iadt.TAG, "Scroll at middle ("+dx+","+dy+")");
                currentScrollStatus = ScrollStatus.MIDDLE;
            }
        }
    };

    private void scrollToBottom() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    //endregion

    //region [ PARAMS ]

    public LogFilterHelper getParams(){
        LogUiFilter logUiFilter = LogFilterStore.get();
        if (logUiFilter == null){
            return new LogFilterHelper(LogFilterHelper.Preset.EVENTS_INFO);
        }
        return new LogFilterHelper(logUiFilter);
    }

    public void updateParams(LogFilterHelper uiFilter){
        LogFilterStore.store(uiFilter.getUiFilter());
        dataSourceFactory.setConfig(uiFilter.getBackFilter());
    }

    //endregion
}
