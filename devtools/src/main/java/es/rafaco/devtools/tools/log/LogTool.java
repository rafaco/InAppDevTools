package es.rafaco.devtools.tools.log;

import android.animation.LayoutTransition;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.utils.OnTouchSelectedListener;

import static es.rafaco.devtools.tools.log.LogLineAdaptor.getLogColor;

public class LogTool extends Tool
                              implements AdapterView.OnItemClickListener {

    public static final String VERBOSE = "Verbose";
    public static final String DEBUG = "Debug";
    public static final String INFO = "Info";
    public static final String WARNING = "Warning";
    public static final String ERROR = "Error";

    protected LogLineAdaptor adapter;
    protected LogReaderTask logReaderTask = null;
    protected ListView outputView;

    protected List<Pair<String, String>> presetFilters;
    protected Spinner presetSpinner;
    private List<Pair<String, String>> levelFilters;
    protected Spinner levelSpinner;
    protected EditText textFilterEditText;
    private String textFilter = "";

    protected String selectedLogLevel = "I";
    private TextView outputToast;
    private RelativeLayout outputContainer;
    private Handler removeToastHandler;


    public LogTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Log";
    }

    @Override
    public String getLayoutId() {
        return "tool_log";
    }

    @Override
    protected void onInit() {
    }

    @Override
    protected void onStart(View toolView) {

        initPresetFilter();
        initLevelFilter();
        initTextFilter();

        initLogLineAdaptor();
        initOutputView();
        startLogReader();

        //Throw exception as playground
        Button searchButton = (Button) getView().findViewById(getResourceId(getView(), "id", "search_button"));
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new NullPointerException();
            }
        });
    }

    private void initLogLineAdaptor() {
        ArrayList<LogLine> logLineArray = new ArrayList<>();
        adapter = new LogLineAdaptor(this, logLineArray, getSelectedConfig());
    }

    private void startLogReader() {

        if (logReaderTask!=null) {
            logReaderTask.stopTask();
        }
        if (adapter !=null && adapter.getCount() > 0) {
            adapter.clear();
        }

        //String command = presetFilters.get(presetSpinner.getSelectedItemPosition()).second;
        //command = String.format(command, selectedLogLevel);
        String command = "logcat -v time";

        logReaderTask = new LogReaderTask(adapter, command);
        if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            //TODO: research!! cordova.getThreadPool().execute()
            logReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            logReaderTask.execute();
        }
    }

    @Override
    protected void onStop() {
        if (logReaderTask!=null){
            logReaderTask.stopTask();
        }
        if (adapter !=null && adapter.getCount() > 0) {
            adapter.clear();
        }
    }

    @Override
    protected void onDestroy() {
        if (logReaderTask!=null){
            logReaderTask.stopTask();
            logReaderTask = null;
        }
        getContainer().removeAllViews();
    }

    private void initOutputView() {
        outputContainer = getView().findViewById(R.id.output_container);
        outputToast = getView().findViewById(R.id.output_toast);
        outputView = getView().findViewById(R.id.output_list);

        outputView.setAdapter(adapter);
        //outputView.setOnItemClickListener(this);
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
        //presetSpinner.setSelection(1);
        presetSpinner.setOnItemSelectedListener(new OnTouchSelectedListener() {
            @Override
            public void onTouchSelected(AdapterView<?> parent, View view, int pos, long id) {
                updateFilter();
            }
        });
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
        //levelSpinner.setSelection(0);
        levelSpinner.setOnItemSelectedListener(new OnTouchSelectedListener() {
            @Override
            public void onTouchSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedLogLevel = getSelectedLevel();

                Log.d(DevTools.TAG, "Verbosity level changed to: " + selectedLogLevel);
                //stop();
                ((TextView) view).setTextColor(getLogColor(selectedLogLevel));
                ((TextView) view).setText(levelFilters.get(levelSpinner.getSelectedItemPosition()).first.toUpperCase());
                updateFilter();
            }
        });
    }

    /*
    private void initLevelFilter() {

        levelButton = (Button)getView().findViewById(getResourceId(getView(),"id", "level_button"));

        popup = new PopupMenu(getView().getContext(), levelButton);
        Menu menu = popup.getMenu();
        menu.addWidget(VERBOSE).setChecked(true);
        menu.addWidget(DEBUG);
        menu.addWidget(INFO);
        menu.addWidget(WARNING);
        menu.addWidget(ERROR);

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
}
