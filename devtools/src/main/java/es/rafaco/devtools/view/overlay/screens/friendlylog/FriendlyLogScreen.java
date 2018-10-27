package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.DialogInterface;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.storage.db.DevToolsDatabase;
import es.rafaco.devtools.storage.db.entities.Friendly;
import es.rafaco.devtools.storage.db.entities.FriendlyDao;
import es.rafaco.devtools.tools.ToolHelper;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogHelper;

public class FriendlyLogScreen extends OverlayScreen {

    private FriendlyLogDataSourceFactory dataSourceFactory;
    private FriendlyLogAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;


    public LiveData<PagedList<Friendly>> logList;
    private final int pageSize = 20;
    private ToolBarHelper toolbarHelper;
    private int selectedLogLevel = 0;

    public FriendlyLogScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Friendly Log";
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


        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void initView(ViewGroup view) {
        welcome = view.findViewById(R.id.welcome);
        welcome.setVisibility(View.GONE);

        recyclerView = getView().findViewById(R.id.list);
    }

    private void initAdapter(){
        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        PagedList.Config myPagingConfig = new PagedList.Config.Builder()
                .setPageSize(20)
                .setPrefetchDistance(60)
                .build();
        dataSourceFactory = new FriendlyLogDataSourceFactory(dao);
        dataSourceFactory.setText("");
        dataSourceFactory.setLevelString("D");
        logList = new LivePagedListBuilder<>(dataSourceFactory, myPagingConfig).build();

        //ConcertViewModel viewModel = ViewModelProviders.of(this).get(ConcertViewModel.class);
        adapter = new FriendlyLogAdapter();
        LifecycleOwner lifecycleOwner = ProcessLifecycleOwner.get();
        //ProcessLifecycleOwner.get().getLifecycle().addObserver(new ProcessLifecycleCallbacks());
        logList.observe(lifecycleOwner, adapter::submitList);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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
                            String stringLevel = getSelectedVerbosity();
                            Log.d(DevTools.TAG, "Verbosity level changed to: " + stringLevel);
                            dataSourceFactory.setLevelString(stringLevel);
                            logList.getValue().getDataSource().invalidate();
                        }
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
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

    public String getSelectedVerbosity() {
        String[] levelsArray = getContext().getResources().getStringArray(R.array.log_levels);
        return levelsArray[selectedLogLevel].substring(0, 1).toUpperCase();
    }


}
