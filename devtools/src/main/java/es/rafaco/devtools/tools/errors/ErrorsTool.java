package es.rafaco.devtools.tools.errors;

import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.db.Crash;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.logic.PermissionActivity;
import es.rafaco.devtools.tools.DecoratedToolInfo;
import es.rafaco.devtools.tools.DecoratedToolInfoAdapter;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;

public class ErrorsTool extends Tool {

    private DecoratedToolInfoAdapter adapter;
    private ListView errorList;
    private TextView welcome;
    private Button crashButton;

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

        crashButton = toolView.findViewById(R.id.crash_button);
        crashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashButton();
            }
        });
    }

    private void onCrashButton() {
        throw new RuntimeException("Ups, crash button has throw an exception for you :)");
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
                initAdapter(crashes);
            }
        });
    }

    private void initAdapter(List<Crash> crashes) {

        ArrayList<DecoratedToolInfo> array = new ArrayList<>();
        for (Crash crash : crashes){
            array.add(new DecoratedToolInfo(ErrorsTool.class, crash.getException(), crash.getMessage(),
                    ContextCompat.getColor(getContext(), R.color.rally_orange)));
        }

        adapter = new DecoratedToolInfoAdapter(this, array);
        errorList = getView().findViewById(R.id.list);
        errorList.setAdapter(adapter);
    }
}
