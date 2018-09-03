package es.rafaco.devtools.view.overlay.screens.errors;

import android.arch.persistence.room.InvalidationTracker;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Anr;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.DecoratedToolInfoAdapter;
import es.rafaco.devtools.utils.ThreadUtils;

import static es.rafaco.devtools.utils.DateUtils.getElapsedTimeLowered;

public class ErrorsScreen extends OverlayScreen {

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;

    private Button crashUiButton;
    private Button crashBackButton;
    private Button anrButton;

    private InvalidationTracker.Observer observer;
    private InvalidationTracker tracker;
    private Toolbar toolbar;

    public ErrorsScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Error log";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_errors_body; }

    @Override
    public int getHeadLayoutId() { return R.layout.tool_errors_head; }

    @Override
    protected void onCreate() {

    }


    @Override
    protected void onStart(ViewGroup view) {
        initView(view);
        initToolbar(view);
        getErrors();


        observer = new InvalidationTracker.Observer(new String[]{"anr"}){
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                getErrors();
            }
        };
        tracker = DevTools.getDatabase().getInvalidationTracker();
        tracker.addObserver(observer);
    }

    @Override
    protected void onStop() {

        tracker.removeObserver(observer);
        observer = null;
    }

    @Override
    protected void onDestroy() {
    }


    private void initView(View toolView) {
        welcome = toolView.findViewById(R.id.welcome);
        welcome.setText(getWelcomeMessage());
        ImageView refreshButton = getView().findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        ImageView deleteButton = getView().findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearAll();
            }
        });

        initAdapter();

        anrButton = toolView.findViewById(R.id.anr_button);
        anrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnrButton();
            }
        });

        crashUiButton = toolView.findViewById(R.id.crash_ui_button);
        crashUiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashUiButton();
            }
        });

        crashBackButton = toolView.findViewById(R.id.crash_back_button);
        crashBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashBackButton();
            }
        });
    }

    private void onRefresh() {
        getErrors();
    }

    private void onClearAll() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                db.crashDao().deleteAll();
                db.anrDao().deleteAll();

                ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                updateList(array);
            }
        });
    }

    public void onAnrButton() {
        Log.i(DevTools.TAG, "ANR requested, sleeping main thread for a while...");
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    Log.e(DevTools.TAG, "Something wrong happen", e);
                }
            }
        });
    }

    public void onCrashUiButton() {
        Log.i(DevTools.TAG, "UI Exception requested, throwing it...");
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Simulated exception on the UI thread");
            }
        });
    }

    public void onCrashBackButton() {
        Log.i(DevTools.TAG, "Background Exception requested, throwing it...");
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Simulated exception on Background thread");
            }
        });
    }



    public String getWelcomeMessage(){
        return "Crashes, ANRs, memory leaks and exceptions in logcat!";
    }

    private void getErrors(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                final ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                List<Crash> crashes = db.crashDao().getAll();
                for (Crash crash : crashes){
                    NavigationStep step = new NavigationStep(CrashDetailScreen.class, String.valueOf(crash.getUid()));
                    array.add(new DecoratedToolInfo(
                            "Crash " + getElapsedTimeLowered(crash.getDate()),
                            crash.getException() + " - " + crash.getMessage(),
                            R.color.rally_orange,
                            crash.getDate(),
                            step));
                }

                List<Anr> anrs = db.anrDao().getAll();
                for (Anr anr : anrs){
                    //TODO: AnrDetailScreen
                    NavigationStep step = new NavigationStep(AnrDetailScreen.class, String.valueOf(anr.getUid()));
                    array.add(new DecoratedToolInfo(
                            "ANR " + getElapsedTimeLowered(anr.getDate()),
                            anr.getCause(),
                            R.color.rally_blue,
                            anr.getDate(),
                            step));
                }

                Collections.sort(array, new Comparator<DecoratedToolInfo>() {
                    @Override
                    public int compare(DecoratedToolInfo o1, DecoratedToolInfo o2) {
                        return o2.getOrder().compareTo(o1.getOrder());
                    }
                });

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
        recyclerView.setAdapter(adapter);
    }

    private void updateList(List<DecoratedToolInfo> errors) {
        adapter.replaceAll(errors);
        recyclerView.requestLayout();
    }


    //region [ TOOL BAR ]

    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.errors_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onToolbarButtonPressed(item);
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.errors);
    }

    private void onToolbarButtonPressed(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_delete)
        {
            onClearAll();
        }
        else if (selected == R.id.action_simulate)
        {
            //TODO: dialog or popup to choose type of error
            //TODO: show message before crash or anr
            onCrashUiButton();
        }
        else if (selected == R.id.action_send)
        {
            //TODO: send all errors
        }
        else if (selected == R.id.action_refresh)
        {
            onRefresh();
        }
    }

    //endregion
}
