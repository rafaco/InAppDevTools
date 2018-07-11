package es.rafaco.devtools.view.overlay.tools.info;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import es.rafaco.devtools.BuildConfig;
import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.utils.FileUtils;

public class InfoHelper {

    private final Context context;

    public InfoHelper(Context context) {
        this.context = context;
    }

    public String buildReport(){

        String reportText = getReport();

        if(FileUtils.isExternalStorageWritable()){

            File file = FileUtils.createNewFile("info", "info_" + System.currentTimeMillis() + ".txt");

            try {
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(reportText);

                myOutWriter.close();
                fOut.flush();
                fOut.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }

            return file.getPath();

        } else if(FileUtils.isExternalStorageReadable() ){
            // only readable
        } else{
            // not accessible
        }

        return null;
    }

    public String getReport() {
        String result = "";
        result += getAppInfo().toString();
        result += "\n";
        result += getDevToolsInfo().toString();
        result += "\n";
        result += getDeviceInfo().toString();
        result += "\n";
        result += getOsInfo().toString();
        result += "\n";
        result += getRunningInfo().toString();
        result += "\n";
        result += "ActivityLog:" + "\n";
        result += DevTools.getActivityLogManager().getLog();
        result += "\n";
        result += getMemInfo();
        result += "\n";
        result += getExtraPackageInfo().toString();
        result += "\n";
        return result;
    }

    public InfoCollection getFullInfoCollection(){
        InfoCollection.Builder collectionBuilder = new InfoCollection.Builder("Hardware and Software")
                .add(getDeviceInfo())
                .add(getOsInfo());
        return collectionBuilder.build();
    }

    public InfoGroup getExtraPackageInfo() {
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

        InfoGroup group = new InfoGroup.Builder("PackageInfo")
                .add("Activities", activities)
                .add("Services", services)
                .add("Permissions", permissions)
                .add("Features", features)
                .add("Instrumentations", instrumentations)
                .build();
        return group;
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
                .add("lastUpdateTime", formatter.format(new Date(pInfo.lastUpdateTime)))
                .add("firstInstallTime", formatter.format(new Date(pInfo.firstInstallTime)))
                .add("Min SDK version", String.valueOf(pInfo.applicationInfo.minSdkVersion))
                .add("Target SDK version", String.valueOf(pInfo.applicationInfo.targetSdkVersion))
                .build();
        return group;
    }

    public InfoGroup getDevToolsInfo() {
        InfoGroup group = new InfoGroup.Builder("DevTools library")
                .add("DevTools Version", BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")")
                .add("Status", "enabled")
                .add("Profile", "developer")
                .build();
        return group;
    }


    public InfoGroup getRunningInfo() {
        InfoGroup group = new InfoGroup.Builder("Currently running")
                .add("Services", getRunningServices(context))
                .add("Tasks", getRunningTasks(context))
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
        String osName = fields[Build.VERSION.SDK_INT + 1].getName();
        return osName;
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

    public String getRunningServices(Context context) {
        String output = "";
        String formatWithoutLabel = "        %s";
        String packageName = context.getPackageName();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getPackageName().equals(packageName)){
                String text = service.service.getShortClassName() + "(" + service.service.getPackageName() + ") ";
                output += String.format(formatWithoutLabel, text) + "\n";
            }
        }
        return output;
    }

    private String getRunningTasks(Context context) {
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

    public static String getMemInfo() {
        StringBuilder meminfo = new StringBuilder();
        try {
            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("cat");
            //commandLine.add("/proc/meminfo");
            //commandLine.add("/proc/stat");
            commandLine.add("/proc/version"); //Linux version multiline very complete
            //commandLine.add("/proc/pid/stat");
            //commandLine.add("adb top -n 1");
            //In adb shell: top -n 1

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
}
