package es.rafaco.devtools.tools.log;

import android.animation.LayoutTransition;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.DecoratedToolInfo;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.utils.OnTouchSelectedListener;

public class LogTool extends Tool implements AdapterView.OnItemClickListener {

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

    protected LogLineAdaptor adapter;
    protected LogReaderTask logReaderTask = null;
    protected ListView outputView;

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
        initSearchButton();
        initDeleteButton();
        initSaveButton();

        initLogLineAdaptor();
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

    private void initLogLineAdaptor() {
        ArrayList<LogLine> logLineArray = new ArrayList<>();
        adapter = new LogLineAdaptor(this, logLineArray, getSelectedConfig());
    }

    private void startLogReader() {
        stopLogReader();

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

    private void stopLogReader() {
        if (logReaderTask!=null) {
            logReaderTask.stopTask();
        }
        if (adapter !=null && adapter.getCount() > 0) {
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
        getContainer().removeAllViews();
    }

    private void initOutputView() {
        outputContainer = getView().findViewById(R.id.output_container);
        outputToast = getView().findViewById(R.id.output_toast);
        outputView = getView().findViewById(R.id.output_list);

        outputView.setAdapter(adapter);
        //outputView.setOnItemClickListener(this);
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
                String path = saveLogcatToFile();
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


    @Override
    public DecoratedToolInfo getHomeInfo(){
        DecoratedToolInfo info = new DecoratedToolInfo( LogTool.class,
                getFullTitle(),
                "Live log is available. Automatic log to disk coming soon.",
                2,
                ContextCompat.getColor(getContext(), R.color.rally_white));
        return info;
    }

    @Override
    public DecoratedToolInfo getReportInfo(){
        DecoratedToolInfo info = new DecoratedToolInfo(LogTool.class,
                getFullTitle(),
                "Include full log.",
                2,
                ContextCompat.getColor(getContext(), R.color.rally_white));
        return info;
    }

    @Override
    public Object getReport(){

        return saveLogcatToFile();

        /*ShellExecuter exe = new ShellExecuter();
        String command = "logcat -d *:V";
        String output = exe.Executer(command);
        return  output;*/
    }


    public String saveLogcatToFile(){
        if(isExternalStorageWritable()){

            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/DevTools");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat_" + System.currentTimeMillis() + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                process = Runtime.getRuntime().exec("logcat -d -f " + logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return logFile.getPath();

        } else if(isExternalStorageReadable() ){
            // only readable
        } else{
            // not accessible
        }

        return null;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

    public void onClearLog(){
        Log.v(DevTools.TAG, "Logcat clear requested");
        logReaderTask.stopTask(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                clearLogcatBuffer();
            }
        });
        stopLogReader();

    }

    private void clearLogcatBuffer() {
        String[] fullCommand = new String[] { LogReaderTask.BASH_PATH, LogReaderTask.BASH_ARGS, "logcat -c"};
        Process process;
        try {
            new ProcessBuilder()
                    .command(fullCommand)//"logcat", "-c")
                    .redirectErrorStream(true)
                    .start();
            //process = Runtime.getRuntime().exec(fullCommand);
            Log.i(DevTools.TAG, "LogcatBuffer cleared");
        } catch (IOException e) {
            Log.e(DevTools.TAG, "LogcatBuffer clear has failed :(");
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTraceString = sw.toString();
            Log.e(DevTools.TAG, stackTraceString);
        } finally {
            Log.d(DevTools.TAG, "LogcatBuffer restarted with clear request");
            startLogReader();
        }
    }
}
