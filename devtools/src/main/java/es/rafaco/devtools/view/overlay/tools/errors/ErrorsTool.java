package es.rafaco.devtools.view.overlay.tools.errors;

import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Anr;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.tools.OverlayTool;
import es.rafaco.devtools.view.overlay.OverlayToolsManager;
import es.rafaco.devtools.view.overlay.tools.commands.CommandsTool;
import es.rafaco.devtools.utils.ThreadUtils;

import static es.rafaco.devtools.utils.DateUtils.getElapsedTimeString;

public class ErrorsTool extends OverlayTool {

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;

    private Button crashUiButton;
    private Button crashBackButton;
    private Button anrButton;

    public ErrorsTool(OverlayToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Errors";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_errors; }

    @Override
    protected void onCreate() {

    }


    @Override
    protected void onStart(ViewGroup view) {
        initView(view);
        getErrors();
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }


    @Override
    public DecoratedToolInfo getHomeInfo(){
        DecoratedToolInfo info = new DecoratedToolInfo(CommandsTool.class,
                getFullTitle(),
                "Crash handler activated." + "\n" + "ANR handler activated.",
                4,
                ContextCompat.getColor(getContext(), R.color.rally_orange));
        return  info;
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
                replaceList(array);
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
                ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                List<Crash> crashes = db.crashDao().getAll();
                for (Crash crash : crashes){
                    array.add(new DecoratedToolInfo(ErrorsTool.class,
                            "Crash " + getElapsedTimeString(crash.getDate()),
                            crash.getException() + " - " + crash.getMessage(),
                            crash.getDate(),
                            ContextCompat.getColor(getContext(), R.color.rally_orange)));
                }

                List<Anr> anrs = db.anrDao().getAll();
                for (Anr anr : anrs){
                    array.add(new DecoratedToolInfo(ErrorsTool.class,
                            "ANR " + getElapsedTimeString(anr.getDate()),
                            anr.getCause(),
                            anr.getDate(),
                            ContextCompat.getColor(getContext(), R.color.rally_blue)));
                }

                Collections.sort(array, new Comparator<DecoratedToolInfo>() {
                    @Override
                    public int compare(DecoratedToolInfo o1, DecoratedToolInfo o2) {
                        return o2.getOrder().compareTo(o1.getOrder());
                    }
                });

                replaceList(array);
            }
        });
    }

    private void replaceList(List<DecoratedToolInfo> errors) {
        adapter.replaceAll(errors);
    }

    private void initAdapter(){

        adapter = new DecoratedToolInfoAdapter(getContext(), new ArrayList<DecoratedToolInfo>()){
            @Override
            protected void onItemClick(DecoratedToolInfo data) {
                super.onItemClick(data);
            }
        };

        recyclerView = getView().findViewById(R.id.errors_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
