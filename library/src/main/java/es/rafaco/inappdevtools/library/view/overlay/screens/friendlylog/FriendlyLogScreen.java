package es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
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

import com.google.gson.Gson;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogDataSourceFactory;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterDialog;
import es.rafaco.inappdevtools.library.logic.log.filter.LogUiFilter;
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

public class FriendlyLogScreen extends OverlayScreen {

    private LogDataSourceFactory dataSourceFactory;
    private FriendlyLogAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;

    public LiveData logList;
    private ToolBarHelper toolbarHelper;
    private LogcatReaderService myService;
    private LogUiFilter logUiFilter;
    private LogFilterDialog filterDialog;
    private boolean isBind;

    private enum ScrollStatus { TOP, MIDDLE, BOTTOM }
    private ScrollStatus currentScrollStatus;

    public FriendlyLogScreen(MainOverlayLayerManager manager) {
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
    public int getBodyLayoutId() { return R.layout.tool_friendlylog_body; }

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

        //bindService();
    }

    private void initFilterHelper() {
        logUiFilter = getParams();
    }

    @Override
    protected void onStop() {
        //TODO: removeLifecycleObserver();
        LogcatReaderService.start(getContext(), "Update");

        //updateParams();
        if (filterDialog !=null && filterDialog.getDialog()!=null){
            filterDialog.getDialog().dismiss();
        }

        if (isBind) {
            getContext().unbindService(serviceConnection);
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

    //region [ BINDING]

    private void bindService() {
        Intent intent = new Intent(getContext(), LogcatReaderService.class);
        getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogcatReaderService.LocalService localService = (LogcatReaderService.LocalService) service;
            myService = localService.getService();
            onServiceBind(name, service);
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };

    private void onServiceBind(ComponentName name, IBinder service) {
        myService.performAction(LogcatReaderService.START_ACTION, "scanner");
    }

    //endregion

    //region [ ADAPTER ]

    private void initAdapter(){
        adapter = new FriendlyLogAdapter();

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
        Log.d("IadtLiveData", "Observer registered to PagedList from FriendlyDao");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        LogcatReaderService.start(getContext(), "Initialize from LogScreen");
    }

    private void initLiveDataWithFriendlyLog(PagedList.Config myPagingConfig) {
        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        dataSourceFactory = new LogDataSourceFactory(dao, logUiFilter.getFilter());
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
        filterDialog = new LogFilterDialog(getContext(), adapter, logUiFilter);
        filterDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateParams();
            }
        });
        filterDialog.getDialog().show();
    }

    private void onLevelButton() {
        String[] levelsArray = getContext().getResources().getStringArray(R.array.log_levels);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Select log level")
                .setCancelable(true)
                .setSingleChoiceItems(levelsArray, logUiFilter.getSeverityInt(), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which != logUiFilter.getSeverityInt()) {
                            logUiFilter.setSeverityInt(which);
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

    public LogUiFilter getParams(){
        if (TextUtils.isEmpty(getParam())){
            //TODO: should it be Log Filter????
          return new LogUiFilter();
        }
        Gson gson = new Gson();
        return gson.fromJson(super.getParam(), LogUiFilter.class);
    }

    public void updateParams(){
        super.updateParam(buildParams(logUiFilter));
    }

    public static String buildParams(LogUiFilter filterHelper){
        Gson gson = new Gson();
        return gson.toJson(filterHelper);
    }

    //endregion
}
