package es.rafaco.devtools.view.overlay.tools.info;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.tools.OverlayTool;
import es.rafaco.devtools.view.overlay.OverlayToolsManager;
import es.rafaco.devtools.view.overlay.tools.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.tools.commands.ShellExecuter;
import es.rafaco.devtools.utils.OnTouchSelectedListener;


public class InfoTool extends OverlayTool {

    private TextView out;
    private Spinner mainSpinner;
    private ShellExecuter exe;
    private Spinner secondSpinner;
    private InfoHelper helper;

    public InfoTool(OverlayToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_info_body; }

    @Override
    public int getHeadLayoutId() { return R.layout.tool_info_head; }

    @Override
    protected void onCreate() {
        helper = new InfoHelper(getContext());
    }

    @Override
    protected void onStart(ViewGroup view) {
        out = getView().findViewById(R.id.out);
        mainSpinner =  getView().findViewById(R.id.info_main_spinner);
        secondSpinner = getView().findViewById(R.id.info_second_spinner);

        initMainSelector();
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    public DecoratedToolInfo getHomeInfo(){
        DecoratedToolInfo info = new DecoratedToolInfo( InfoTool.class,
                getFullTitle(),
                getHomeInfoMessage(),
                1,
                ContextCompat.getColor(getContext(), R.color.rally_blue));
        return info;
    }

    @Override
    public DecoratedToolInfo getReportInfo(){
        DecoratedToolInfo info = new DecoratedToolInfo(InfoTool.class,
                getFullTitle(),
                "Include all. Brief info is always added",
                1,
                ContextCompat.getColor(getContext(), R.color.rally_blue));
        return info;
    }

    @Override
    public Object getReport(){
        return helper.buildReport();
    }




    private void initMainSelector() {
        ArrayList<String> list = new ArrayList<>();
        list.add("BuildConfig");
        list.add("dumpsys");

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getView().getContext(),
                android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(spinnerAdapter);
        OnTouchSelectedListener listener = new OnTouchSelectedListener() {
            @Override
            public void onTouchSelected(AdapterView<?> parent, View view, int pos, long id) {
                String title = spinnerAdapter.getItem(pos);
                Log.d(DevTools.TAG, "Info - Main selector changed to: " + title);

                if (title.equals("BuildConfig")) {
                    loadBuildConfig();
                } else if (title.equals("dumpsys")) {
                    onDumpSys();
                }
            }
        };
        mainSpinner.setOnItemSelectedListener(listener);
        mainSpinner.setOnTouchListener(listener);

        loadBuildConfig();
    }

    private void loadBuildConfig() {
        String report = helper.buildReport();
        out.setText(report);
        secondSpinner.setVisibility(View.GONE);
    }

    public String getHomeInfoMessage(){
        String out = "";
        out += helper.getAppName() + " "  + helper.getPackageInfo().versionName + " (" + helper.getPackageInfo().versionCode + ")";
        out += "\n";
        out += Build.BRAND + " " + Build.MODEL;
        out += "\n";
        out += "Android " + Build.VERSION.RELEASE + " (" + helper.getVersionCodeName() + ")";
        return out;
    }



    private void onDumpSys() {
        out.setText("List of services at dumpsys: \n\n");

        exe = new ShellExecuter();
        String command = "dumpsys -l";
        String output = exe.Executer(command);

        String[] separated = output.split("\n");
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1 ; i<separated.length; i++) {
            list.add(separated[i]);
        }

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getView().getContext(),
                android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondSpinner.setAdapter(spinnerAdapter);
        secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String title = spinnerAdapter.getItem(position);
                Log.d(DevTools.TAG, "Info - Second selector changed to: " + title);

                exe = new ShellExecuter();
                String command = "dumpsys " + title;
                String output = exe.Executer(command);

                out.setText(output);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        secondSpinner.setVisibility(View.VISIBLE);
    }
}
