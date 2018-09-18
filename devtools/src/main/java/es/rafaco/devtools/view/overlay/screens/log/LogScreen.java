package es.rafaco.devtools.view.overlay.screens.log;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.ToolHelper;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public class LogScreen extends OverlayScreen {


    protected LogLineAdapter adapter;
    protected LogReaderTask logReaderTask = null;
    protected RecyclerView recyclerView;
    private String textFilter = "";

    private TextView outputToast;
    private RelativeLayout outputContainer;
    private Handler removeToastHandler;
    private int selectedLogLevel;


    public LogScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "LogCat";
    }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.logcat;
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_log_body; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {

        initSearchButtons();
        selectedLogLevel = 0;
        showAllMenuItem(getToolbar().getMenu());

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

    private void initSearchButtons() {
        final MenuItem searchItem = getToolbar().getMenu().findItem(R.id.action_search);
        final MenuItem filterItem = getToolbar().getMenu().findItem(R.id.action_filter);
        if (searchItem != null && filterItem != null) {
            MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    hideOthersMenuItem(getToolbar().getMenu(), item);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    showAllMenuItem(getToolbar().getMenu());
                    return true;
                }
            };
            searchItem.setOnActionExpandListener(onActionExpandListener);
            filterItem.setOnActionExpandListener(onActionExpandListener);

            final SearchView searchView = (SearchView) searchItem.getActionView();
            final SearchView filterView = (SearchView) filterItem.getActionView();
            if (searchView != null && filterView != null) {

                searchView.setQueryHint("Search...");
                filterView.setQueryHint("Filter...");
                int searchImgId = android.support.v7.appcompat.R.id.search_button; // I used the explicit layout ID of searchview's ImageView
                ImageView v = filterView.findViewById(searchImgId);
                v.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_filter_list_rally_24dp));
                filterView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
            }
        }
    }

    private void showAllMenuItem(Menu menu) {
        for(int i = 0; i<menu.size(); i++ ){
            MenuItem current = menu.getItem(i);
            current.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
    }

    private void hideOthersMenuItem(Menu menu, MenuItem filterItem) {
        for(int i = 0; i<menu.size(); i++ ){
            MenuItem current = menu.getItem(i);
            if(current.getItemId() != filterItem.getItemId()){
                current.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
    }

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
        String path = (String) helper.getReportPath();
        DevTools.showMessage("Log stored to " + path);
    }

    public void onClearLog(){
        Log.v(DevTools.TAG, "Logcat clear requested");
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
        adapter = new LogLineAdapter(this,
                new ArrayList<LogLine>(), getSelectedConfig());

        recyclerView = getView().findViewById(R.id.output_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
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
