package es.rafaco.devtools.view.overlay.screens.info;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import es.rafaco.devtools.BuildConfig;
import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.storage.files.DevToolsFiles;
import es.rafaco.devtools.logic.activityLog.ActivityLogManager;
import es.rafaco.devtools.tools.ToolHelper;

public class InfoHelper extends ToolHelper {

    @Override
    public String getReportPath() {
        String filePath = DevToolsFiles.storeInfo(getReportContent(), System.currentTimeMillis());
        return filePath;
    }


    @Override
    public String getReportContent() {
        String result = "";
        result += getAppStatus();
        result += "\n";
        result += getStaticInfo();
        result += "\n";
        return result;
    }

    //region [ REPORT BUILDER ]

    @NonNull
    public String getStaticInfo() {
        String result = "";
        result += getAppInfo().toString();
        result += "\n";
        result += getDevToolsInfo().toString();
        result += "\n";
        result += getDeviceInfo().toString();
        result += "\n";
        result += getOsInfo().toString();
        result += "\n";
        result += getLinuxInfo();
        result += "\n";
        getPackageInfoInfo().toString();
        result += "\n";
        return result;
    }

    public InfoGroup getAppInfo() {
        PackageInfo pInfo = getPackageInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        InfoGroup group = new InfoGroup.Builder("Host app")
                .add("App name", getAppName())
                .add("Package name", getPackageName())
                .add("App Version", pInfo.versionName + " (" + pInfo.versionCode + ")")
                .add("Build type", BuildConfig.BUILD_TYPE)
                .add("Flavor", BuildConfig.FLAVOR)
                .add("Min SDK version", getMinSdkVersion(pInfo))
                .add("Target SDK version", String.valueOf(pInfo.applicationInfo.targetSdkVersion))
                .add("lastUpdateTime", formatter.format(new Date(pInfo.lastUpdateTime)))
                .add("firstInstallTime", formatter.format(new Date(pInfo.firstInstallTime)))
                .build();
        return group;
    }

    public InfoGroup getDevToolsInfo() {
        InfoGroup group = new InfoGroup.Builder("DevTools library")
                .add("DevTools version", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")
                .add("Status", "enabled")
                .add("Profile", "developer")
                .build();
        return group;
    }

    public InfoGroup getDeviceInfo() {
        InfoGroup group = new InfoGroup.Builder("Device")
                .add(context.getString(R.string.manufacturer), Build.MANUFACTURER)
                .add(context.getString(R.string.brand), Build.BRAND)
                .add(context.getString(R.string.model), Build.MODEL)
                .add(context.getString(R.string.board), Build.BOARD)
                .add(context.getString(R.string.id), Build.ID)
                .add(context.getString(R.string.serial), Build.SERIAL)
                .add("IS VIRTUAL", isVirtual().toString())
                .build();
        return group;
    }

    public InfoGroup getOsInfo() {
        InfoGroup group = new InfoGroup.Builder("OS")
                .add("VERSION NAME", getVersionCodeName())
                .add(context.getString(R.string.versioncode), Build.VERSION.RELEASE)
                .add(context.getString(R.string.sdk), String.valueOf(Build.VERSION.SDK_INT))
                .add(context.getString(R.string.base), Build.VERSION.BASE_OS)
                .add(context.getString(R.string.incremental), Build.VERSION.INCREMENTAL)
                //.add(c.getString(R.string.type), Build.TYPE)
                //.add(c.getString(R.string.user), Build.USER)
                //.add(c.getString(R.string.host), Build.HOST)
                //.add(c.getString(R.string.fingerprint), Build.FINGERPRINT)
                .build();
        return group;
    }

    public String getAppStatus() {
        String result = "";
        ActivityLogManager logManager = DevTools.getActivityLogManager();
        result += "Currently on " + (logManager.isInBackground() ? "Background" : "Foreground");
        result += "\n";
        result += "Top activity is " + getTopActivity();
        result += "\n";
        result += "\n";
        result += DevTools.getDatabase().getOverview();
        result += "\n";
        result += logManager.getStartedActivitiesCount() + " activities started";
        result += "\n";
        result += DevTools.getActivityLogManager().getLog();
        result += "\n";
        result += getRunningInfo().toString();
        result += "\n";
        return result;
    }

    public InfoGroup getRunningInfo() {
        InfoGroup group = new InfoGroup.Builder("Currently running")
                .add("Services", getRunningServices())
                .add("Services 2", getRunningServices2())
                .add("Tasks", getRunningTasks())
                .build();
        return group;
    }

    public InfoGroup getPackageInfoInfo() {
        PackageInfo pInfo = getPackageInfo();
        String activities = parsePackageInfoArray(pInfo.activities);
        String services = parsePackageInfoArray(pInfo.services);
        String permissions = parsePackageInfoArray(pInfo.permissions);
        String features = "No available";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //features = parsePackageInfoArray(pInfo.featureGroups.fe);
            features = "No implemented already";
        }
        String instrumentations = parsePackageInfoArray(pInfo.instrumentation);

        InfoGroup group = new InfoGroup.Builder("PackageInfo:")
                .add("Activities", activities)
                .add("Services", services)
                .add("Permissions", permissions)
                .add("Features", features)
                .add("Instrumentations", instrumentations)
                .add("Libraries", "Coming soon")
                .build();
        return group;
    }

    //endregion

    //region [ PROPERTY EXTRACTORS ]

    public String getAppName() {
        PackageInfo pInfo = getPackageInfo();
        return pInfo.applicationInfo.labelRes == 0
                ? pInfo.applicationInfo.nonLocalizedLabel.toString()
                : context.getString(pInfo.applicationInfo.labelRes);
    }

    public String getPackageName(){
        return context.getPackageName();
    }

    public String getVersionCodeName(){
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT].getName();
        return osName;
    }

    @NonNull
    private String getMinSdkVersion(PackageInfo pInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return String.valueOf(pInfo.applicationInfo.minSdkVersion);
        }
        //TODO: get minSDK for api < 24
        return "Unavailable";
    }

    public static String getAppTimeStamp(Context context) {
        long time = getAppBuildTime(context);

        if (time == -1) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timeStamp = formatter.format(time);
        return timeStamp;
    }

    public static long getAppBuildTime(Context context){
        long time;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            time = new File(appFile).lastModified();
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
            time = -1;
        }

        return time;
    }

    private Boolean isVirtual() {
        return Build.FINGERPRINT.contains("generic") ||
                Build.PRODUCT.contains("sdk");
    }

    public PackageInfo getPackageInfo() {
        PackageInfo pInfo = new PackageInfo();
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES |
                    PackageManager.GET_ACTIVITIES |
                    PackageManager.GET_SERVICES |
                    PackageManager.GET_INSTRUMENTATION);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    private String parsePackageInfoArray(PackageItemInfo[] infos) {
        String result;
        if (infos == null){
            return "Unavailable";
        }
        result = "[" + infos.length + "]"+ "\n";
        if (infos.length > 0){
            for (PackageItemInfo info: infos) {
                result += info.name + "\n";
            }
            //result = result.substring(0, result.length() - 2);
            //result += ".";
        }
        return result;
    }

    public String getRunningServices() {
        String output = "";
        String formatWithoutLabel = "        %s";
        String packageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getPackageName().equals(packageName)) {
                String text = service.service.getShortClassName() + "(" + service.service.getPackageName() + ") ";
                output += String.format(formatWithoutLabel, text) + "\n";
            }
        }
        return output;
    }

    public String getRunningServices2() {
        String output = "";
        String packageName = context.getPackageName();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        long currentMillis = Calendar.getInstance().getTimeInMillis();
        Calendar cal = Calendar.getInstance();

        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.getPackageName().equals(packageName)) {
                cal.setTimeInMillis(currentMillis - info.activeSince);
                output += String.format("Process %s has been running since: %d ms \n", info.process, info.activeSince);
            }
        }
        return output;
    }

    private String getRunningTasks() {
        String output = "";
        String formatWithoutLabel = "        %s";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(10);
        Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
        while(itr.hasNext()){
            ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo)itr.next();
            int id = runningTaskInfo.id;
            CharSequence desc= runningTaskInfo.description;
            int numOfActivities = runningTaskInfo.numActivities;
            String topActivity = runningTaskInfo.topActivity.getShortClassName();
            String text = String.valueOf(id) + "(" + desc + ") top of " + String.valueOf(numOfActivities) + " is " + topActivity;
            output += String.format(formatWithoutLabel, text) + "\n";
        }
        return output;
    }

    private String getTopActivity() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo firstTaskInfo = runningTaskInfoList.get(0);
        return firstTaskInfo.topActivity.getShortClassName();
    }

    public static String getLinuxInfo() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/version");
        return runCommandLine(commandLine);
    }

    public static String getMemInfo() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/meminfo");
        return runCommandLine(commandLine);
    }

    public static String getProcStat() {
        ArrayList<String> commandLine = new ArrayList<String>();
        commandLine.add("cat");
        commandLine.add("/proc/stat");
        //commandLine.add("/proc/pid/stat");
        //commandLine.add("adb top -n 1");
        //In adb shell: top -n 1
        return runCommandLine(commandLine);
    }

    @NonNull
    private static String runCommandLine(ArrayList<String> commandLine) {
        StringBuilder meminfo = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                meminfo.append(line);
                meminfo.append("\n");
            }
        } catch (IOException e) {
            Log.e(DevTools.TAG, "Could not read /proc/meminfo", e);
        }

        return meminfo.toString();
    }

    public String getFormattedAppName() {
        PackageInfo packageInfo = getPackageInfo();
        String environment = "DEBUG"; //TODO: Environment selector propagation
        String version = packageInfo.versionName + " (" + packageInfo.versionCode + ")";
        return String.format("%s %s %s", getAppName(), environment, version);
    }

    public String getFormattedModeAndVersion() {
        PackageInfo packageInfo = getPackageInfo();
        String mode = "Developer"; //TODO: Environment selector propagation
        String version = packageInfo.versionName + " (" + packageInfo.versionCode + ")";
        return String.format("%s version %s", mode, version);
    }

    public String getFormattedDevice() {
        return String.format("%s %s %s", Build.BRAND, Build.MODEL, isVirtual()? "Emulated!" : "");
    }

    @NonNull
    public String getFormattedDeviceLong() {
        return String.format("%s %s with Android %s", capitalize(Build.BRAND), Build.MODEL, Build.VERSION.RELEASE); //getVersionCodeName()
    }

    @NonNull
    public String getFormattedAppLong() {
        return getAppName() + " "  + getPackageInfo().versionName;// + " (" + getPackageInfo().versionCode + ")";
    }

    public String capitalize(String s){
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
    //endregion
}
