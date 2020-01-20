/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.documents.generators.info;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Build;

//#ifdef ANDROIDX
//@import androidx.annotation.NonNull;
//#else
import android.support.annotation.NonNull;
//#endif

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.generators.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.Document;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.utils.AppBuildConfig;
import es.rafaco.inappdevtools.library.logic.utils.AppInfoUtils;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import github.nisrulz.easydeviceinfo.base.EasyAppMod;

public class AppInfoDocumentGenerator extends AbstractDocumentGenerator {

    private long sessionId;
    EasyAppMod easyAppMod;

    public AppInfoDocumentGenerator(Context context, Document report, long param) {
        super(context, report, param);
        this.sessionId = param;
        easyAppMod = new EasyAppMod(context);
    }

    @Override
    public String getTitle() {
        return getDocument().getName() + " Info from session " + sessionId;
    }

    @Override
    public String getSubfolder() {
        return "session/" + sessionId;
    }

    @Override
    public String getFilename() {
        return "info_" + getDocument().getName().toLowerCase() + "_" + sessionId + ".txt";
    }

    @Override
    public String getOverview() {
        return getAppNameAndVersions() + "\n"
                + "Updated " + Humanizer.getElapsedTimeLowered(
                        new Date(getPackageInfo().lastUpdateTime).getTime())
                + (easyAppMod.getStore().equals("unknown") ? "" : " from " + easyAppMod.getStore());
    }

    @Override
    public DocumentData getData() {
        return new DocumentData.Builder(getTitle())
                .setOverview(getOverview())
                .add(getApkInfo())
                .add(getInstallInfo())
                .add(getSigningInfo())
                .add(getManifestInfo())
        .build();
    }

    private DocumentSectionData getSigningInfo() {
        DocumentSectionData group = new DocumentSectionData.Builder("Sign Certificate")
                .setIcon(R.string.gmd_security)
                .add(AppInfoUtils.getSigningInfo(context))
                .build();
        return group;
    }


    public DocumentSectionData getApkInfo() {
        PackageInfo pInfo = getPackageInfo();
        DocumentSectionData group = new DocumentSectionData.Builder("APK")
                .setIcon(R.string.gmd_apps)
                .setOverview(easyAppMod.getAppName() + " " + easyAppMod.getAppVersion())
                .add("App Name", easyAppMod.getAppName())
                .add("Package Name", easyAppMod.getPackageName())
                .add("Internal Package", getInternalPackageName())
                //.add("Activity Name", easyAppMod.getActivityName())
                .add("App version", easyAppMod.getAppVersion())
                .add("App versioncode", easyAppMod.getAppVersionCode())
                //.add("Does app have Camera permission?",String.valueOf(easyAppMod.isPermissionGranted(Manifest.permission.CAMERA)))
                .add("Min SDK", getMinSdkVersion(pInfo))
                .add("Target SDK", String.valueOf(pInfo.applicationInfo.targetSdkVersion))
                .build();
        return group;
    }

    public DocumentSectionData getInstallInfo() {
        PackageInfo pInfo = getPackageInfo();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        DocumentSectionData group = new DocumentSectionData.Builder("Installation")
                .setIcon(R.string.gmd_cloud_download)
                .setOverview(Humanizer.getElapsedTime(new Date(pInfo.lastUpdateTime).getTime()))
                .add("Store", easyAppMod.getStore())
                .add("Last Update", Humanizer.getElapsedTime(new Date(pInfo.lastUpdateTime).getTime()))
                .add("Last Update Time", formatter.format(new Date(pInfo.lastUpdateTime)))
                .add("First Install", Humanizer.getElapsedTime(new Date(pInfo.firstInstallTime).getTime()))
                .add("First Install Time", formatter.format(new Date(pInfo.firstInstallTime)))
                .build();
        return group;
    }

    public DocumentSectionData getManifestInfo() {
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

        DocumentSectionData group = new DocumentSectionData.Builder("Manifest")
                .setIcon(R.string.gmd_widgets)
                .add("Activities", activities)
                .add("Services", services)
                .add("Permissions", permissions)
                .add("Features", features)
                .add("Instrumentations", instrumentations)
                .add("Libraries", "Coming soon")
                .addButton(new RunButton("Original",
                        R.drawable.ic_local_library_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                String params = SourceDetailScreen.buildParams(IadtPath.SOURCES
                                        + "/AndroidManifest.xml");
                                OverlayService.performNavigation(SourceDetailScreen.class, params);
                            }
                        }))
                .addButton(new RunButton("Merged",
                        R.drawable.ic_local_library_white_24dp,
                        new Runnable() {
                            @Override
                            public void run() {
                                String params = SourceDetailScreen.buildParams(IadtPath.GENERATED
                                        + "/merged_manifests/AndroidManifest.xml");
                                OverlayService.performNavigation(SourceDetailScreen.class, params);
                            }
                        }))
                .build();
        return group;
    }


    public static String getAppBuildTimeNice(Context context){
        return Humanizer.getElapsedTimeLowered(getAppBuildTime(context));
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

    public String getInternalPackageName(){
        return AppBuildConfig.getNamespace(context);
    }

    public String getAppNameAndVersions() {
        PackageInfo packageInfo = getPackageInfo();
        return String.format("%s v%s (%s)", getAppName(),  packageInfo.versionName, packageInfo.versionCode);
    }

    @NonNull
    public String getFormattedAppLong() {
        return getAppName() + " "  + getPackageInfo().versionName;
    }

    public String getFormattedVersionLong() {
        PackageInfo packageInfo = getPackageInfo();
        return String.format("Version %s (%s)", packageInfo.versionName, packageInfo.versionCode);
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
