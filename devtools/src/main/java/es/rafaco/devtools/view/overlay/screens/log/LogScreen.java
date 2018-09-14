package es.rafaco.devtools.view.overlay.screens.log;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.tools.ToolHelper;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.utils.OnTouchSelectedListener;

public class LogScreen extends OverlayScreen implements AdapterView.OnItemClickListener {

    /*public static final String VERBOSE = "Verbose";
    public static final String DEBUG = "Debug";
    public static final String INFO = "Info";
    public static final String WARNING = "Warning";
    public static final String ERROR = "Error";*/

    public static final String VERBOSE = "V";
    public static final String DEBUG = "D";
    public static final String INFO = "I";
    public static final String WARNING = "W";
    public static final String ERROR = "E";

    protected LogLineAdapter adapter;
    protected LogReaderTask logReaderTask = null;
    protected RecyclerView recyclerView;

    protected List<Pair<String, String>> presetFilters;
    protected Spinner presetSpinner;
    private List<Pair<String, String>> levelFilters;
    protected Spinner levelSpinner;
    protected EditText textFilterEditText;
    private String textFilter = "";

    private TextView outputToast;
    private RelativeLayout outputContainer;
    private Handler removeToastHandler;
    private Process process;
    private Toolbar toolbar;


    public LogScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "LogCat";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_log_body; }

    @Override
    public int getHeadLayoutId() { return R.layout.tool_toolbar; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {

        initToolbar(headView);

        initPresetFilter();
        initLevelFilter();
        initTextFilter();
        initSearchButton();
        initDeleteButton();
        initSaveButton();

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

        if (process != null)
            process.destroy();
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

        if (process != null)
            process.destroy();
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

    private void initPresetFilter() {
        //TODO: get from external config
        presetFilters = new ArrayList<>();
        presetFilters.add(new Pair<>("All", "All"));
        presetFilters.add(new Pair<>("My app", "My app"));
        //presetFilters.add(new Pair<>("Cordova", "Cordova"));
        //presetFilters.add(new Pair<>("Native", "Native"));
        presetFilters.add(new Pair<>("DevTools", "DevTools"));

        /*presetFilters.add(new Pair<>("All", "logcat *:%s"));
        presetFilters.add(new Pair<>("My app", "logcat *:%s | grep -F \"`shell ps | grep es.aena.mobile | cut -c10-15`\""));// + String.valueOf(myPid)));
        presetFilters.add(new Pair<>("Cordova", "logcat chromium:%s *:S"));
        presetFilters.add(new Pair<>("Native", "logcat chromium:S *:%s"));
        presetFilters.add(new Pair<>("DevTools", "logcat DevTools:%s *:S"));*/

        ArrayList<String> adapterList = new ArrayList<>();
        for (Pair<String,String> pair: presetFilters) {
            adapterList.add(pair.first);
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getView().getContext(),
                android.R.layout.simple_spinner_item, adapterList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        presetSpinner = getView().findViewById(R.id.log_options_spinner);
        presetSpinner.setAdapter(spinnerAdapter);

        OnTouchSelectedListener listener = new OnTouchSelectedListener() {
            @Override
            public void onTouchSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateFilter();
            }
        };
        presetSpinner.setOnItemSelectedListener(listener);
        presetSpinner.setOnTouchListener(listener);
        presetSpinner.setSelection(1);
    }

    private void initLevelFilter() {
        levelFilters = new ArrayList<Pair<String, String>>();
        levelFilters.add(new Pair<>(VERBOSE, "V"));
        levelFilters.add(new Pair<>(DEBUG, "D"));
        levelFilters.add(new Pair<>(INFO, "I"));
        levelFilters.add(new Pair<>(WARNING, "W"));
        levelFilters.add(new Pair<>(ERROR, "E"));

        ArrayList<String> adapterList = new ArrayList<>();
        for (Pair<String,String> pair: levelFilters) {
            adapterList.add(pair.first);
        }
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getView().getContext(),
                android.R.layout.simple_spinner_item, adapterList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        levelSpinner = getView().findViewById(R.id.log_level_spinner);
        levelSpinner.setAdapter(spinnerAdapter);

        OnTouchSelectedListener listener = new OnTouchSelectedListener() {
            @Override
            public void onTouchSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedLogLevel = getSelectedLevel();

                Log.d(DevTools.TAG, "Verbosity level changed to: " + selectedLogLevel);
                //stop();
                ((TextView) view).setTextColor(LogLine.getLogColor(view.getContext(), selectedLogLevel));
                ((TextView) view).setText(levelFilters.get(levelSpinner.getSelectedItemPosition()).first.toUpperCase());
                updateFilter();
            }
        };
        levelSpinner.setOnItemSelectedListener(listener);
        levelSpinner.setOnTouchListener(listener);

        levelSpinner.setSelection(2);
    }

    /*
    private void initLevelFilter() {

        levelButton = (Button)getView().findViewById(getResourceId(getView(),"id", "level_button"));

        popup = new PopupMenu(getView().getContext(), levelButton);
        Menu menu = popup.getMenu();
        menu.addLayer(VERBOSE).setChecked(true);
        menu.addLayer(DEBUG);
        menu.addLayer(INFO);
        menu.addLayer(WARNING);
        menu.addLayer(ERROR);

        levelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String title = (String) item.getTitle();
                if (title.equals(VERBOSE)) selectedLogLevel = "V";
                else if (title.equals(DEBUG)) selectedLogLevel = "D";
                else if (title.equals(INFO)) selectedLogLevel = "I";
                else if (title.equals(WARNING)) selectedLogLevel = "W";
                else if (title.equals(ERROR)) selectedLogLevel = "E";

                Log.d(es.rafaco.devtools.DevTools.TAG, "Verbosity level changed to: " + selectedLogLevel);
                //stop();
                levelButton.setText(selectedLogLevel);
                levelButton.setTextColor(getLogColor(selectedLogLevel));
                start();
                return true;
            }
        });

        levelButton.setTextColor(getLogColor("I"));
        levelButton.setText("I");
    }
    */

    private void initTextFilter(){

        textFilterEditText = getView().findViewById(R.id.filter_edit_text);

        textFilterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textFilter = s.toString();
                updateFilter();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initSearchButton() {
        ImageView searchButton = getView().findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: REMOVE Throw exception as playground
                throw new NullPointerException("Ups, search button has simulated an exception for you :)");
            }
        });
    }

    private void initDeleteButton() {
        ImageView deleteButton = getView().findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearLog();
            }
        });
    }

    private void initSaveButton() {
        ImageView saveButton = getView().findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolHelper helper = new LogHelper();
                String path = (String) helper.getReportPath();
                DevTools.showMessage("Log stored to " + path);
            }
        });
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(DevTools.TAG, "onItemClick");
    }



    private void updateFilter() {
        LogFilterConfig filterConfig = getSelectedConfig();
        adapter.updateFilter(filterConfig);
    }

    @NonNull
    private LogFilterConfig getSelectedConfig() {
        return new LogFilterConfig(
                    getSelectedPreset(),
                    getSelectedLevel(),
                    getSelectedText());
    }

    @NonNull
    private String getSelectedText() {
        return textFilter;
    }

    private String getSelectedPreset() {
        return presetFilters.get(presetSpinner.getSelectedItemPosition()).second;
    }

    private String getSelectedLevel() {
        return levelFilters.get(levelSpinner.getSelectedItemPosition()).second;
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


    //region [ TOOL BAR ]

    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.tool_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onToolbarButtonPressed(item);
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.logcat);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toolbar.requestLayout();
    }

    private void onToolbarButtonPressed(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_search) {
            onSearchButton();
        }
        else if (selected == R.id.action_level) {
            onLevelButton();
        }
        else if (selected == R.id.action_filter) {
            onFilterButton();
        }
        else if (selected == R.id.action_save) {
            onSaveButton();
        }
        else if (selected == R.id.action_delete) {
            onClearLog();
        } else{
            DevTools.showMessage("Not already implemented");
        }
    }

    private void onSaveButton() {

    }

    private void onFilterButton() {

    }

    private void onLevelButton() {
        String[] stringArray = getContext().getResources().getStringArray(R.array.log_levels);
        final int initialSelection = 0;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getView().getContext())
                .setTitle("Select log level")
                .setCancelable(true)
                .setSingleChoiceItems(stringArray, initialSelection, new OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which!=initialSelection){
                            dialog.dismiss();
                        }
                        DevTools.showMessage("Not already implemented");
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    private void onSearchButton() {

    }

    //endregion
}
