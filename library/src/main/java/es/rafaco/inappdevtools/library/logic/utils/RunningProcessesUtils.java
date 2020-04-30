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

package es.rafaco.inappdevtools.library.logic.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningProcessesUtils {

    private RunningProcessesUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static String getString() {
        StringBuilder result = new StringBuilder("\n");
        List<ActivityManager.RunningAppProcessInfo> processes = getList();
        
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            result.append(getContent(info));
            result.append(Humanizer.newLine());
        }
        return result.toString();
    }

    public static List<ActivityManager.RunningAppProcessInfo> getList() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();

        //Older API levels return all processes running in device,
        // we need to filter them by packageName
        String myPackageName = getContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = new ArrayList<>();
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
                for (String processPackageName : process.pkgList) {
                    if (processPackageName.equals(myPackageName)){
                        appProcesses.add(process);
                    }
                }
            }
        }
        return appProcesses;
    }

    public static int getCount() {
        return getList().size();
    }

    public static String getClassName(ActivityManager.RunningAppProcessInfo info) {
        return Humanizer.emptyString();
    }

    public static String getTitle(ActivityManager.RunningAppProcessInfo info) {
        return info.pid + " - " + info.processName;
    }

    public static String getContent(ActivityManager.RunningAppProcessInfo info) {
        StringBuffer contentBuffer = new StringBuffer();

        contentBuffer.append("PID: " + info.pid);
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("UID: " + info.uid);
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Importance: " + getImportanceString(info));
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Name: " + info.processName);
        contentBuffer.append(Humanizer.newLine());

        for (String pkg : info.pkgList) {
            contentBuffer.append("Pkg: ").append(pkg).append(Humanizer.newLine());
        }

        contentBuffer.append("Memory:");
        contentBuffer.append(Humanizer.newLine());
        contentBuffer.append(getMemoryInfoFormatted(info));

        return contentBuffer.toString();
    }

    private static String getImportanceString(ActivityManager.RunningAppProcessInfo info) {
        String importance = info.importance + " ";
        if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            importance += "Foreground";
        }else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
            importance += "Service";
        }
        else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
            importance += "Cached";
        }
        else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE) {
            importance += "Gone";
        }else{
            importance += "Other";
        }
        return importance;
    }

    private static String getMemoryInfoFormatted(ActivityManager.RunningAppProcessInfo processInfo) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo[] processMemoryInfo;
        try {
            processMemoryInfo = manager.getProcessMemoryInfo(new int[]{processInfo.pid});
            if (processMemoryInfo!=null && processMemoryInfo.length>0){
                return getMemoryInfoFormatted(processMemoryInfo[0]);
            }
        }
        catch (Exception e) {
            //TODO: research why is always null
            return "No Info " + e.getMessage();
        }
        return "No Info ";
    }

    private static String getMemoryInfoFormatted(Debug.MemoryInfo debugMemoryInfo) {
        StringBuffer resultBuffer = new StringBuffer();
        resultBuffer.append(String.format("  Dalvik: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.dalvikPss),
                Humanizer.parseKb(debugMemoryInfo.dalvikSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.dalvikPrivateDirty)));
        resultBuffer.append(Humanizer.newLine());
        resultBuffer.append(String.format("  Native: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.nativePss),
                Humanizer.parseKb(debugMemoryInfo.nativeSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.nativePrivateDirty)));
        resultBuffer.append(Humanizer.newLine());
        resultBuffer.append(String.format("  Other: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.otherPss),
                Humanizer.parseKb(debugMemoryInfo.otherSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.otherPrivateDirty)));
        resultBuffer.append(Humanizer.newLine());
        return resultBuffer.toString();
    }
}
