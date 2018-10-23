package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.storage.db.DevToolsDatabase;
import es.rafaco.devtools.storage.db.entities.Friendly;
import es.rafaco.devtools.storage.db.entities.FriendlyDao;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class FriendlyLogScreen extends OverlayScreen {

    private FriendlyLogAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;
    private TextView emptyView;

    public LiveData<PagedList<Friendly>> logList;
    private final int pageSize = 20;

    public FriendlyLogScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Friendly Log";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_errors_body; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.errors;
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onStart(ViewGroup toolHead) {

        initView(bodyView);
        initAdapter();
    }

    private void initView(ViewGroup view) {
        welcome = view.findViewById(R.id.welcome);
        welcome.setVisibility(View.GONE);
        //welcome.setText("Awesome Friendly Log!");

        recyclerView = getView().findViewById(R.id.errors_list);
        emptyView = getView().findViewById(R.id.empty_errors_list);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        initAdapter();
    }

    private void initAdapter(){
        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        PagedList.Config myPagingConfig = new PagedList.Config.Builder()
                .setPageSize(20)
                .setPrefetchDistance(60)
                .build();

        logList = new LivePagedListBuilder<>(dao.getAllProvider(), myPagingConfig).build();
        //ConcertViewModel viewModel = ViewModelProviders.of(this).get(ConcertViewModel.class);
        adapter = new FriendlyLogAdapter();
        LifecycleOwner lifecycleOwner = ProcessLifecycleOwner.get();
        //ProcessLifecycleOwner.get().getLifecycle().addObserver(new ProcessLifecycleCallbacks());
        logList.observe(lifecycleOwner, adapter::submitList);

        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        ((LinearLayoutManager) mLayoutManager).setReverseLayout(true);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_delete)
        {
            onClearAll();
        }
        else if (selected == R.id.action_simulate)
        {
            DevTools.showMessage("Not already implemented");
            adapter.notifyDataSetChanged();
        }
        else if (selected == R.id.action_send)
        {
            //TODO: send all errors
            DevTools.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    private void onClearAll() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                db.friendlyDao().deleteAll();
                adapter.notifyDataSetChanged();
            }
        });
    }
    //endregion
}
