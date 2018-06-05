package es.rafaco.devtools.tools.info;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.rafaco.devtools.BuildConfig;
import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.tools.Tool;
import es.rafaco.devtools.tools.ToolsManager;
import es.rafaco.devtools.tools.shell.ShellExecuter;


public class InfoTool extends Tool {

    private TextView out;
    private Spinner mainSpinner;
    private ShellExecuter exe;
    private Spinner secondSpinner;

    public InfoTool(ToolsManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @Override
    public String getLayoutId() {
        return "tool_info";
    }

    @Override
    protected void onInit() {

    }

    @Override
    protected void onStart(View toolView) {
        out = (TextView) getView().findViewById(getResourceId(getView(), "id", "out"));
        mainSpinner = (Spinner) getView().findViewById(getResourceId(getView(),"id", "info_main_spinner"));
        secondSpinner = (Spinner) getView().findViewById(getResourceId(getView(),"id", "info_second_spinner"));

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
        list.add("BuildConfig");
        list.add("dumpsys");

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getView().getContext(),
                android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(spinnerAdapter);
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String title = spinnerAdapter.getItem(position);
                Log.d(DevTools.TAG, "Info - Main selector changed to: " + title);

                if (title.equals("BuildConfig")) {
                    loadBuildConfig();
                }
                else if (title.equals("dumpsys")) {
                    onDumpSys();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //commandSpinner.setSelection(0);
    }

    private void loadBuildConfig() {
        out.setText("");

        out.append(getAppInfo());
        out.append("\n");
        out.append(DevTools.getActivityLogManager().getLog());
        out.append("\n");
        out.append(getHardwareAndSoftwareInfo());
        out.append("\n");

        secondSpinner.setVisibility(View.GONE);
    }

    private String getAppInfo() {
        Context context = DevTools.getAppContext();
        String packageName = context.getPackageName();
        PackageInfo pInfo = new PackageInfo();
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);/* ||
                    PackageManager.GET_ACTIVITIES ||
                    PackageManager.GET_SERVICES ||
                    PackageManager.GET_INSTRUMENTATION);*/
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String services = "";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getPackageName().equals(packageName)){
                services += service.service.getShortClassName() + "(" + service.service.getPackageName() + ") ";
            }
        }

        String tasks = "";
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(10);
        Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
        while(itr.hasNext()){
            ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo)itr.next();
            int id = runningTaskInfo.id;
            CharSequence desc= runningTaskInfo.description;
            int numOfActivities = runningTaskInfo.numActivities;
            String topActivity = runningTaskInfo.topActivity.getShortClassName();
            tasks += String.valueOf(id) + desc + String.valueOf(numOfActivities) + topActivity + "\n";
        }

        String appName = pInfo.applicationInfo.labelRes == 0 ? pInfo.applicationInfo.nonLocalizedLabel.toString() : context.getString(pInfo.applicationInfo.labelRes);
        String permissions = parsePackageInfoArray(pInfo.permissions);
        String activities = parsePackageInfoArray(pInfo.activities);
        //String services = parsePackageInfoArray(pInfo.services);
        String instrumentations = parsePackageInfoArray(pInfo.instrumentation);
        String features = "No available";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //features = parsePackageInfoArray(pInfo.featureGroups.fe);
            features = "No implemented already";
        }
        return  "\n" + "App: " + appName + " (" + packageName + ")" + "\n" +
                "App Version: " + pInfo.versionName + " (" + pInfo.versionCode + ")" + "\n" +
                "DevTools Version: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")" + "\n" +
                //"BUILD_TYPE: " + BuildConfig.BUILD_TYPE + "\n" +
                //"FLAVOR: " + BuildConfig.FLAVOR + "\n" +
                "\n" + "PackageInfo:" + "\n" +
                "Activities: " + activities + "\n" +
                "Services: " + services + "\n" +
                "Tasks: " + tasks + "\n" +
                "Permissions: " + permissions + "\n" +
                "Features: " + features + "\n" +
                "Instrumentations: " + instrumentations + "\n";
    }

    private String parsePackageInfoArray(PackageItemInfo[] infos) {
        String result;
        if (infos == null){
            return "Unavailable";
        }
        result = "[" + infos.length + "] ";
        if (infos.length > 0){
            for (PackageItemInfo info: infos) {
                result += info.name + "\n";
            }
            //result = result.substring(0, result.length() - 2);
            //result += ".";
        }
        return result;
    }

    private String getHardwareAndSoftwareInfo() {
        Context c = getContainer().getContext();
        return  "\n" + "Device: " + "\n" +
                c.getString(R.string.manufacturer) + " " + Build.MANUFACTURER + "\n" +
                c.getString(R.string.brand) + " " + Build.BRAND + "\n" +
                c.getString(R.string.model) + " " + Build.MODEL + "\n" +
                c.getString(R.string.board) + " " + Build.BOARD + "\n" +
                c.getString(R.string.id) + " " + Build.ID + "\n" +
                c.getString(R.string.serial) + " " + Build.SERIAL + "\n" +
                "IS VIRTUAL: " + isVirtual().toString() + "\n" +
                "PRODUCT: " + isVirtual().toString() + "\n" +
                "\n" + "OS: " + "\n" +
                "VERSION NAME: " + getVersionCodeName() + "\n" +
                c.getString(R.string.versioncode) + " " + Build.VERSION.RELEASE + "\n" +
                c.getString(R.string.sdk) + " " + Build.VERSION.SDK_INT + "\n" +
                c.getString(R.string.base) + " " + Build.VERSION.BASE_OS + "\n" +
                c.getString(R.string.incremental) + " " + Build.VERSION.INCREMENTAL + "\n" +
                c.getString(R.string.type) + " " + Build.TYPE + "\n" +
                c.getString(R.string.user) + " " + Build.USER + "\n" +
                c.getString(R.string.host) + " " + Build.HOST + "\n" +
                c.getString(R.string.fingerprint) + " " + Build.FINGERPRINT + "\n";
    }

    public static String getVersionCodeName(){
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT + 1].getName();
        return osName;
    }

    public Boolean isVirtual() {
        return Build.FINGERPRINT.contains("generic") ||
                Build.PRODUCT.contains("sdk");
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
