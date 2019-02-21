package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.commands.ShellExecuter;


public class InfoScreen extends OverlayScreen {

    private TextView out;
    private Spinner mainSpinner;
    private ShellExecuter exe;
    private Spinner secondSpinner;
    private InfoHelper helper;

    public InfoScreen(MainOverlayLayerManager manager) {
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
        helper = new InfoHelper();
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




    private void initMainSelector() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Static info");
        list.add("App Status");
        list.add("/proc/meminfo");
        list.add("/proc/stat");
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

                if (title.equals("Static info")) {
                    setReport(helper.getStaticInfo());
                }
                else if (title.equals("App Status")) {
                    setReport(helper.getAppStatus());
                }
                else if (title.equals("/proc/stat")) {
                    setReport(helper.getProcStat());
                }
                else if (title.equals("/proc/meminfo")) {
                    setReport(helper.getMemInfo());
                }
                else if (title.equals("dumpsys")) {
                    onDumpSys();
                }
            }
        };
        mainSpinner.setOnItemSelectedListener(listener);
        mainSpinner.setOnTouchListener(listener);

        setReport(helper.getStaticInfo());
    }

    private void setReport(String report) {
        out.setText(report);
        secondSpinner.setVisibility(View.GONE);
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
