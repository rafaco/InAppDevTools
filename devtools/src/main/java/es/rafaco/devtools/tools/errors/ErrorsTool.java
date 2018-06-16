package es.rafaco.devtools.tools.errors;

import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.errors.Crash;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.tools.DecoratedToolInfo;
import es.rafaco.devtools.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.utils.ThreadUtils;

public class ErrorsTool extends Tool {

    private DecoratedToolInfoAdapter adapter;
    private ListView errorList;
    private TextView welcome;

    private Button crashUiButton;
    private Button crashBackButton;

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

    private void initView(View toolView) {
        welcome = toolView.findViewById(R.id.welcome);
        welcome.setText(getWelcomeMessage());

        initAdapter();

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

    public void onCrashUiButton() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Ups, you throw an exception on the ui thread :)");
            }
        });
    }

    public void onCrashBackButton() {
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
                List<Crash> crashes = db.crashDao().getAll();
                Log.d(DevTools.TAG, "Crash db size is: " + crashes.size());
                replaceList(crashes);
            }
        });
    }

    private void replaceList(List<Crash> crashes) {
        ArrayList<DecoratedToolInfo> array = new ArrayList<>();
        for (Crash crash : crashes){
            array.add(new DecoratedToolInfo(ErrorsTool.class,
                    "Crash " + getElapsedTimeString(crash.getDate()),
                    crash.getException() + " - " + crash.getMessage(),
                    ContextCompat.getColor(getContext(), R.color.rally_orange)));
        }

        adapter.replaceAll(array);
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
