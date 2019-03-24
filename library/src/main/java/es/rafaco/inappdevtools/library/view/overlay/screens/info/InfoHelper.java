package es.rafaco.inappdevtools.library.view.overlay.screens.info;

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

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.AppInfoUtils;
import es.rafaco.inappdevtools.library.logic.utils.BuildConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.logic.watcher.activityLog.ActivityLogManager;
import es.rafaco.inappdevtools.library.tools.ToolHelper;

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
        result += getApkReport();
        result += "\n";
        result += getDeviceReport();
        result += "\n";
        return result;
    }

    //region [ REPORT BUILDER ]

    @NonNull
    public String getApkReport() {
        String result = "";
        result += getAppInfo().toString();
        result += "\n";
        result += getDevToolsInfo().toString();
        result += "\n";
        result += AppInfoUtils.getSigningInfo(context);
        result += "\n";
        result += getPackageInfoInfo().toString();
        result += "\n";
        return result;
    }

    public String getDeviceReport() {
        String result = "";
        result += getDeviceInfo().toString();
        result += "\n";
        result += getOsInfo().toString();
        result += "\n";
        result += getLinuxInfo();
        result += "\n";
        result += "//TODO: Screen info";
        result += "\n";
        return result;
    }

    public InfoGroup getAppInfo() {
        PackageInfo pInfo = getPackageInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        CompileConfig buildConfig = new CompileConfig(context);
        InfoGroup group = new InfoGroup.Builder("Host app")
                .add("App name", getAppName())
                .add("Package name", getPackageName())
                //.add("ApplicationID", AppBuildConfig.getStringValue(context, "APPLICATION_ID"))
                .add("App Version", pInfo.versionName + " (" + pInfo.versionCode + ")")
                .add("Build time", buildConfig.getString(CompileConfigFields.BUILD_TIME))
                .add("Build time", buildConfig.getString(CompileConfigFields.BUILD_TIME_UTC))
                .add("Build type", AppBuildConfig.getStringValue(context, BuildConfigFields.BUILD_TYPE))
                .add("isDebug", AppBuildConfig.getBooleanValue(context, BuildConfigFields.DEBUG))
                .add("Flavor", AppBuildConfig.getStringValue(context, BuildConfigFields.FLAVOR))
                .add("Min SDK version", getMinSdkVersion(pInfo))
                .add("Target SDK version", String.valueOf(pInfo.applicationInfo.targetSdkVersion))
                .add("lastUpdateTime", formatter.format(new Date(pInfo.lastUpdateTime)))
                .add("firstInstallTime", formatter.format(new Date(pInfo.firstInstallTime)))
                .build();
        return group;
    }

    public InfoGroup getDevToolsInfo() {
        CompileConfig buildConfig = new CompileConfig(context);
        InfoGroup group = new InfoGroup.Builder("DevTools library")
                .add("DevTools version", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")
                .add("Build type", BuildConfig.BUILD_TYPE)
                .add("Flavor", BuildConfig.FLAVOR)
                .add(CompileConfigFields.EXT_ENABLED, buildConfig.getString(CompileConfigFields.EXT_ENABLED))
                .add(CompileConfigFields.EXT_EMAIL, buildConfig.getString(CompileConfigFields.EXT_EMAIL))
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
        result += "App on " + (logManager.isInBackground() ? "Background" : "Foreground");
        result += "\n";
        result += "Top activity is " + getTopActivity();
        result += "\n";
        result += logManager.getStartedActivitiesCount() + " activities started";
        result += "\n";
        result += DevTools.getActivityLogManager().getLog();
        result += "\n";
        result += getRunningInfo().toString();
        result += "\n";
        result += DevTools.getDatabase().getOverview();
        result += "\n";
        return result;
    }

    public InfoGroup getRunningInfo() {
        return new InfoGroup.Builder("Currently running")
                .add("Services", getRunningServices())
                .add("Tasks", getRunningTasks())
                .build();
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

    public String getConfig() {
        String result = "";
        result += "Compile config:";
        result += "\n";
        result += new CompileConfig(context).getAll();
        result += "\n";
        result += "\n";
        result += "Git config:";
        result += "\n";
        result += DevTools.getSourcesManager().getContent("assets/inappdevtools/git_config.json");
        result += "\n";
        //result += "Git diff:";
        //result += "\n";
        //result += DevTools.getSourcesManager().getContent(SourcesManager.ASSETS, "inappdevtools/git.diff");
        return result;
    }

    //endregion

    //region [ PROPERTY EXTRACTORS ]

    public String getAppName() {
        PackageInfo pInfo = getPackageInfo();
        return pInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
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
        } catch (PackageManager.NameNotFoundException e) {
            FriendlyLog.logException("Exception", e);
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
                    PackageManager.GET_SERVICES |
                    PackageManager.GET_INSTRUMENTATION);
        } catch (PackageManager.NameNotFoundException e) {
            FriendlyLog.logException("Exception", e);
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
        }
        return result;
    }

    public String getRunningServices() {
        String result = "\n";

        String packageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (info.service.getPackageName().equals(packageName)) {
                String className = info.service.getShortClassName();
                String name = className.substring(className.lastIndexOf(".")+1);
                String elapsed = DateUtils.getElapsedTimeLowered(Calendar.getInstance().getTimeInMillis() - info.activeSince);
                String date = DateUtils.format(info.activeSince);
                String text = name + " from " + elapsed + "(" + date + ")";
                result += text + "\n";
            }
        }
        return result;
    }

    private String getRunningTasks() {
        String output = "\n";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(10);
        Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
        while(itr.hasNext()){
            ActivityManager.RunningTaskInfo runningTaskInfo = itr.next();
            int id = runningTaskInfo.id;
            int numOfActivities = runningTaskInfo.numActivities;
            String topActivity = runningTaskInfo.topActivity.getShortClassName();
            String text = String.valueOf(id) + " - " + topActivity + " top of " + numOfActivities;
            output += text + "\n";
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
        return getAppName() + " "  + getPackageInfo().versionName;
    }

    public String capitalize(String s){
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
    //endregion
}
