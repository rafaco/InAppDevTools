package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import es.rafaco.inappdevtools.library.BuildConfig;
import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.watcher.activityLog.ActivityLogManager;
import es.rafaco.inappdevtools.library.storage.files.DevToolsFiles;
import es.rafaco.inappdevtools.library.tools.ToolHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.AppInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.BuildInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.DeviceInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.pages.OSInfoHelper;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoGroup;

//TODO: refactor this hole file! across multiples files

public class InfoHelper extends ToolHelper {

    @Override
    public String getReportPath() {
        String filePath = DevToolsFiles.storeInfo(getReportContent(), System.currentTimeMillis());
        return filePath;
    }


    @Override
    public String getReportContent() {
        String result = "";
        result += getStatusReportContent();
        result += "\n";
        result += getApkReportContent();
        result += "\n";
        result += getDeviceReportContent();
        result += "\n";
        return result;
    }


    //region [ REPORTS (return String) ]

    public String getApkReportContent() {
        AppInfoHelper infoHelper = new AppInfoHelper(context);
        return infoHelper.getReport().toString();
    }

    public String getDeviceReportContent() {
        DeviceInfoHelper infoHelper = new DeviceInfoHelper(context);
        return infoHelper.getReport().toString();
    }

    public String getOSReportContent() {
        OSInfoHelper infoHelper = new OSInfoHelper(context);
        return infoHelper.getReport().toString();
    }

    public String getBuildReportContent() {
        BuildInfoHelper infoHelper = new BuildInfoHelper(context);
        return infoHelper.getReport().toString();
    }



    public String getStatusReportContent() {
        String result = "";
        ActivityLogManager logManager = DevTools.getActivityLogManager();
        result += "App on " + (logManager.isInBackground() ? "Background" : "Foreground");
        result += "\n";
        result += "Top activity is " + getTopActivity();
        result += "\n";
        result += "\n";
        result += getRunningInfo().toString();
        result += "\n";
        return result;
    }

    public String getToolsReportContent() {
        String result = "";
        result += getDevToolsInfo().toString();
        result += "\n";
        result += "Compile config: ";
        result += "\n\n";
        result += new CompileConfig(context).getAll();
        result += "\n";
        result += DevTools.getDatabase().getOverview();
        result += "\n";
        return result;
    }

    //endregion

    //region [ GROUPS (return InfoGroup) ]


    public InfoGroup getDevToolsInfo() {
        CompileConfig buildConfig = new CompileConfig(context);
        InfoGroup group = new InfoGroup.Builder("InAppDevTools")
                .add("Version", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")
                .add("Build type", BuildConfig.BUILD_TYPE)
                .add("Flavor", BuildConfig.FLAVOR)
                .add(CompileConfigFields.EXT_ENABLED, buildConfig.getString(CompileConfigFields.EXT_ENABLED))
                .add(CompileConfigFields.EXT_EMAIL, buildConfig.getString(CompileConfigFields.EXT_EMAIL))
                .build();
        return group;
    }

    public InfoGroup getRunningInfo() {
        return new InfoGroup.Builder("")
                .add("Services", getRunningServices())
                .add("Tasks", getRunningTasks())
                .build();
    }

    //endregion

    //region [ PROPERTY EXTRACTORS ]

    private Boolean isVirtual() {
        return Build.FINGERPRINT.contains("generic") ||
                Build.PRODUCT.contains("sdk");
    }


    public String getRunningServices() {
        String result = "\n";

        String packageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (info.service.getPackageName().equals(packageName)) {
                String className = info.service.getShortClassName();
                String name = className.substring(className.lastIndexOf(".")+1);
                long startTimeMillis = Calendar.getInstance().getTimeInMillis() - info.activeSince;
                String elapsed = DateUtils.getElapsedTimeLowered(startTimeMillis);
                String date = DateUtils.format(startTimeMillis);
                String text = name + " started " + elapsed + " (" + date + ")";
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
    //endregion
}
