package es.rafaco.devtools.view.overlay.screens.friendlylog;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.activityLog.ProcessLifecycleCallbacks;
import es.rafaco.devtools.logic.utils.FriendlyLog;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.storage.db.DevToolsDatabase;
import es.rafaco.devtools.storage.db.entities.Friendly;
import es.rafaco.devtools.storage.db.entities.FriendlyDao;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.errors.CrashDetailScreen;
import es.rafaco.devtools.view.utils.DecoratedToolInfo;
import es.rafaco.devtools.view.utils.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.utils.RecyclerViewUtils;

public class FriendlyLogScreen extends OverlayScreen {

    private DecoratedToolInfoAdapter adapter;
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

        recyclerView = getView().findViewById(R.id.errors_list);
        emptyView = getView().findViewById(R.id.empty_errors_list);

        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        logList = new LivePagedListBuilder<>(dao.getAllProvider(), pageSize).build();
        //ConcertViewModel viewModel = ViewModelProviders.of(this).get(ConcertViewModel.class);
        FriendlyLogAdapter adapter = new FriendlyLogAdapter();
        LifecycleOwner lifecycleOwner = ProcessLifecycleOwner.get();
        //ProcessLifecycleOwner.get().getLifecycle().addObserver(new ProcessLifecycleCallbacks());
        logList.observe(lifecycleOwner, adapter::submitList);
        recyclerView.setAdapter(adapter);

        initView(bodyView);
        adapter.notifyDataSetChanged();
        //getData();
    }

    private void initView(ViewGroup view) {
        welcome = view.findViewById(R.id.welcome);
        welcome.setText("Awesome Friendly Log!");

        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        //initAdapter();
    }

    private void getData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                final ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                List<Friendly> logs = db.friendlyDao().getAll();
                for (Friendly log : logs){
                    NavigationStep step = new NavigationStep(CrashDetailScreen.class, String.valueOf(log.getUid()));
                    array.add(new DecoratedToolInfo(
                            null,
                            log.getMessage(),
                            FriendlyLog.getColor(log),
                            FriendlyLog.getIcon(log),
                            log.getDate(),
                            step));
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateList(array);
                    }
                });
            }
        });
    }

    private void initAdapter(){

        adapter = new DecoratedToolInfoAdapter(getContext(), new ArrayList<DecoratedToolInfo>());

        recyclerView = getView().findViewById(R.id.errors_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        emptyView = getView().findViewById(R.id.empty_errors_list);
    }

    private void updateList(List<DecoratedToolInfo> errors) {
        adapter.replaceAll(errors);
        RecyclerViewUtils.updateEmptyState(recyclerView, emptyView, errors);
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

                ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                updateList(array);
            }
        });
    }
    //endregion
}
