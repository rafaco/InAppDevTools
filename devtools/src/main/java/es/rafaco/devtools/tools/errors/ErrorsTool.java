package es.rafaco.devtools.tools.errors;

import android.arch.persistence.room.Database;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
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
import es.rafaco.devtools.tools.DecoratedToolInfo;
import es.rafaco.devtools.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.tools.commands.CommandsTool;
import es.rafaco.devtools.utils.ThreadUtils;

public class ErrorsTool extends Tool {

    private DecoratedToolInfoAdapter adapter;
    private ListView errorList;
    private TextView welcome;

    private Button crashUiButton;
    private Button crashBackButton;
    private Button anrButton;

    public ErrorsTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Errors";
    }

    @Override
    public String getLayoutId() {
        return "tool_errors";
    }

    @Override
    protected void onInit() {

    }


    @Override
    protected void onStart(View toolView) {
        initView(toolView);
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
                "Crash handler activated. \n ANR handler activated.",
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

        if (ThreadUtils.amIOnUiThread()){
            Log.d(DevTools.TAG, "ErrorsTool is on UI thread");
        }else{
            Log.d(DevTools.TAG, "ErrorsTool is NOT ON UI thread");
        }
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
                throw new RuntimeException("Ups, you throw an exception on the ui thread :)");
            }
        });
    }

    public void onCrashBackButton() {
        Log.i(DevTools.TAG, "Background Exception requested, throwing it...");
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Ups, you throw an exception on a background thread :)");
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

    public String getElapsedTimeString(long oldTime){
        CharSequence relativeDate =
                DateUtils.getRelativeTimeSpanString(oldTime,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE);
        return relativeDate.toString();
    }

    private void initAdapter(){
        adapter = new DecoratedToolInfoAdapter(this, new ArrayList<DecoratedToolInfo>());
        errorList = getView().findViewById(R.id.list);
        errorList.setAdapter(adapter);
    }
}
