package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.AppInfoUtils;
import es.rafaco.inappdevtools.library.logic.utils.BuildConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfig;
import es.rafaco.inappdevtools.library.logic.utils.CompileConfigFields;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.structs.InfoReport;
import github.nisrulz.easydeviceinfo.base.EasyAppMod;

public class AppInfoHelper {

    Context context;

    public AppInfoHelper(Context context) {
        this.context = context;
    }

    @NonNull
    public InfoReport getReport() {
        return new InfoReport.Builder("")
                .add(getAppInfo())
                .add(getAppInfo2())
                .add(getInstallInfo())
                .add()
                .add(AppInfoUtils.getSigningInfo(context))
                .add(getPackageInfoInfo().toString())
        .build();
    }


    public InfoGroup getAppInfo() {
        PackageInfo pInfo = getPackageInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        CompileConfig buildConfig = new CompileConfig(context);
        InfoGroup group = new InfoGroup.Builder("")
                .add("Name", getAppName())
                .add("Version", pInfo.versionName + " (" + pInfo.versionCode + ")")
                .add("Package", getPackageName())
                .add("Min SDK", getMinSdkVersion(pInfo))
                .add("Target SDK", String.valueOf(pInfo.applicationInfo.targetSdkVersion))
                .add()
                .add("isDebug", AppBuildConfig.getBooleanValue(context, BuildConfigFields.DEBUG))
                .add("Flavor", AppBuildConfig.getStringValue(context, BuildConfigFields.FLAVOR))
                .add("Build time", buildConfig.getString(CompileConfigFields.BUILD_TIME))
                .add("Build time", buildConfig.getString(CompileConfigFields.BUILD_TIME_UTC))
                .add("Build type", AppBuildConfig.getStringValue(context, BuildConfigFields.BUILD_TYPE))
                .add()
                .add("lastUpdateTime", formatter.format(new Date(pInfo.lastUpdateTime)))
                .add("firstInstallTime", formatter.format(new Date(pInfo.firstInstallTime)))
                .build();
        return group;
    }

    public InfoGroup getAppInfo2() {
        EasyAppMod easyAppMod = new EasyAppMod(context);
        InfoGroup app = new InfoGroup.Builder("App")
                .add("Installer Store", easyAppMod.getStore())
                .add("App Name", easyAppMod.getAppName())
                .add("Package Name", easyAppMod.getPackageName())
                .add("Activity Name", easyAppMod.getActivityName())
                .add("App version", easyAppMod.getAppVersion())
                .add("App versioncode", easyAppMod.getAppVersionCode())
                .add("Does app have Camera permission?",
                        String.valueOf(easyAppMod.isPermissionGranted(Manifest.permission.CAMERA)))
                .build();
        return app;
    }

    public InfoGroup getInstallInfo() {
        EasyAppMod easyAppMod = new EasyAppMod(context);
        PackageInfo pInfo = getPackageInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        InfoGroup group = new InfoGroup.Builder("")
                .add("Store", easyAppMod.getStore())
                .add("First Install", DateUtils.getElapsedTime(new Date(pInfo.firstInstallTime).getTime()))
                .add("First Install Time", formatter.format(new Date(pInfo.firstInstallTime)))
                .add("Last Update", DateUtils.getElapsedTime(new Date(pInfo.lastUpdateTime).getTime()))
                .add("Last Update Time", formatter.format(new Date(pInfo.lastUpdateTime)))
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

    public String getAppName() {
        PackageInfo pInfo = getPackageInfo();
        return pInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
    }

    public String getPackageName(){
        return context.getPackageName();
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

    @NonNull
    public String getFormattedAppLong() {
        return getAppName() + " "  + getPackageInfo().versionName;
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

}
