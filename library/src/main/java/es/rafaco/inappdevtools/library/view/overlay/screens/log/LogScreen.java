package es.rafaco.inappdevtools.library.view.overlay.screens.log;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.tools.ToolHelper;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.friendlylog.ToolBarHelper;

public class LogScreen extends OverlayScreen {


    protected LogLineAdapter adapter;
    protected LogReaderTask logReaderTask = null;
    protected RecyclerView recyclerView;
    private String textFilter = "";

    private TextView outputToast;
    private RelativeLayout outputContainer;
    private Handler removeToastHandler;
    private int selectedLogLevel;
    private ToolBarHelper toolbarHelper;


    public LogScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "LogCat";
    }

    @Override
    public boolean needNestedScroll() {
        return false;
    }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.logcat;
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_log_body; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {

        initToolbar();
        initLogLineAdapter();
        initOutputView();
        startLogReader();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showFilterOutputToast();
            }
        }, 1000);

        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void initToolbar() {
        toolbarHelper = new ToolBarHelper(getToolbar());
        toolbarHelper.initSearchButtons(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textFilter = newText;
                updateFilter();
                return false;
            }
        });


        selectedLogLevel = 0;
        toolbarHelper.showAllMenuItem();
    }

    @Override
    protected void onStop() {
        stopLogReader();
    }

    @Override
    protected void onDestroy() {
        if (logReaderTask!=null){
            logReaderTask.stopTask();
            logReaderTask = null;
        }
    }


    //region [ TOOL BAR ]


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_level) {
            onLevelButton();
        }
        else if (selected == R.id.action_save) {
            onSaveButton();
        }
        else if (selected == R.id.action_delete) {
            onClearLog();
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
                .setSingleChoiceItems(levelsArray, selectedLogLevel, new OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which!=selectedLogLevel) {
                            selectedLogLevel = which;
                            Log.d(DevTools.TAG, "Verbosity level changed to: " + getSelectedVerbosity());
                            updateFilter();
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
        String path = helper.getReportPath();
        DevTools.showMessage("Log stored to " + path);
    }

    public void onClearLog(){
        Log.v(DevTools.TAG, "Logcat showPlaceholder requested");
        logReaderTask.stopTask(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                LogHelper.clearLogcatBuffer();
                startLogReader();
            }
        });
        stopLogReader();
    }

    //endregion

    //region [ OUTPUT LIST ]

    private void initLogLineAdapter() {
        adapter = new LogLineAdapter(this, new ArrayList<LogLine>(), getSelectedConfig());

        recyclerView = getView().findViewById(R.id.output_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void initOutputView() {
        outputContainer = getView().findViewById(R.id.output_container);
        outputToast = getView().findViewById(R.id.output_toast);
    }

    private void startLogReader() {
        stopLogReader();

        //TODO!
        //String command = presetFilters.get(presetSpinner.getSelectedItemPosition()).second;
        //command = String.format(command, selectedLogLevel);
        String command = "logcat -v time";

        logReaderTask = new LogReaderTask(adapter, command);
        if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            logReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            logReaderTask.execute();
        }
    }

    private void stopLogReader() {
        if (logReaderTask!=null) {
            logReaderTask.stopTask();
        }
        if (adapter !=null && adapter.getItemCount() > 0) {
            adapter.clear();
        }
    }

    public void showFilterOutputToast(){
        showOutputToast(String.format("Showing %s of %s", adapter.getFilteredSize(), adapter.getOriginalSize()));
    }

    public void showOutputToast(String text) {
        outputToast.setText(" " + text + " ");

        outputContainer.setLayoutTransition(null);
        outputToast.setVisibility(View.VISIBLE);

        removeToastHandler = new Handler();
        removeToastHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                outputContainer.setLayoutTransition(new LayoutTransition());
                outputToast.setVisibility(View.GONE);
            }
        }, 4000);
    }

    private void updateFilter() {
        LogFilterConfig filterConfig = getSelectedConfig();
        adapter.updateFilter(filterConfig);
    }

    @NonNull
    private LogFilterConfig getSelectedConfig() {
        return new LogFilterConfig(
                    "All",
                    getSelectedVerbosity(),
                    textFilter);
    }

    public String getSelectedVerbosity() {
        String[] levelsArray = getContext().getResources().getStringArray(R.array.log_levels);
        return levelsArray[selectedLogLevel].substring(0, 1).toUpperCase();
    }

    //endregion
}
