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

package org.inappdevtools.library.logic.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import org.inappdevtools.library.logic.documents.data.DocumentEntryData;
import org.inappdevtools.library.logic.documents.data.DocumentSectionData;

import java.util.List;

import org.inappdevtools.library.IadtController;
import org.inappdevtools.library.view.utils.Humanizer;

public class RunningTasksUtils {

    private static String NEW_LAUNCHER_ACTIVITY_CLASS = "com.google.android.apps.nexuslauncher.NexusLauncherActivity";
    private static String OLD_LAUNCHER_ACTIVITY_CLASS = "com.google.android.launcher.GEL";
    private static String OLDER_LAUNCHER_ACTIVITY_CLASS = "com.android.launcher2.Launcher";

    private RunningTasksUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static String getString() {
        String output = Humanizer.newLine();
        List<ActivityManager.RunningTaskInfo> tasks = getList();

        for(ActivityManager.RunningTaskInfo task : tasks){
            int id = task.id;
            int numOfActivities = task.numActivities;
            String topActivity = task.topActivity.getShortClassName();
            String text = id + " - " + topActivity + " top of " + numOfActivities + "(" + task.numRunning +" running)";
            output += text + Humanizer.newLine();
        }
        return output;
    }

    public static List<ActivityManager.RunningTaskInfo> getList() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(Integer.MAX_VALUE);
        return runningTaskInfoList;
    }

    public static int getCount() {
        return getList().size();
    }

    public static String getTopClassName(ActivityManager.RunningTaskInfo info) {
        return info.topActivity.getClassName();
    }

    public static String getBaseClassName(ActivityManager.RunningTaskInfo info) {
        return info.baseActivity.getClassName();
    }

    public static String getTitle(ActivityManager.RunningTaskInfo info) {
        String tail = isLauncherTask(info) ? " (Launcher)" : "";
        return "Task " + info.id + tail;
    }

    public static String getContent(ActivityManager.RunningTaskInfo info) {
        StringBuilder contentBuffer = new StringBuilder();

        contentBuffer.append("ID: " + info.id);
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Activities: " + info.numActivities);
        contentBuffer.append(Humanizer.newLine());

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            contentBuffer.append("Running activities: " + info.numRunning);
            contentBuffer.append(Humanizer.newLine());
        }

        if (!TextUtils.isEmpty(info.description)){
            contentBuffer.append("Description: " + info.description);
            contentBuffer.append(Humanizer.newLine());
        }

        contentBuffer.append("Base activity: " + info.baseActivity.getShortClassName());
        contentBuffer.append(Humanizer.newLine());

        contentBuffer.append("Top activity: " + info.topActivity.getShortClassName());
        contentBuffer.append(Humanizer.newLine());

        return contentBuffer.toString();
    }

    private static boolean isLauncherTask(ActivityManager.RunningTaskInfo task) {
        String baseActivityName = task.baseActivity.getClassName();
        return baseActivityName.equals(NEW_LAUNCHER_ACTIVITY_CLASS)
                || baseActivityName.equals(OLD_LAUNCHER_ACTIVITY_CLASS)
                || baseActivityName.equals(OLDER_LAUNCHER_ACTIVITY_CLASS);
    }

    public static int getNotLauncherCount() {
        int count = 0;
        List<ActivityManager.RunningTaskInfo> tasks = getList();
        for(ActivityManager.RunningTaskInfo task : tasks){
            if (!isLauncherTask(task)) {
                count ++;
            }
        }
        return count;
    }


    //TODO: review following methods, used externally by LiveInfoDocumentGenerator

    public static int getActivitiesCount() {
        int count = 0;
        List<ActivityManager.RunningTaskInfo> tasks = getList();

        for(ActivityManager.RunningTaskInfo task : tasks){
            if (!isLauncherTask(task)) {
                count += task.numActivities;
            }
        }
        return count;
    }

    public static String getTopActivity() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo firstTaskInfo = runningTaskInfoList.get(0);
        String shortClassName = firstTaskInfo.topActivity.getShortClassName();
        int lastDot = shortClassName.lastIndexOf(".");
        if (lastDot > 0){
            return shortClassName.substring(lastDot + 1);
        }
        return shortClassName;
    }

    public static String getTopActivityStatus() {
        return IadtController.get().getActivityTracker().isInBackground()
                ? "Background" : "Foreground";
    }

    public static String getTopActivityClassName() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo topTaskInfo = runningTaskInfoList.get(0);
        ComponentName topActivity = topTaskInfo.topActivity;

        return topActivity.getClassName();
    }

    public static List<DocumentEntryData> getTopActivityInfo() {
        return getActivityInfo(0, true);
    }

    private static List<DocumentEntryData> getActivityInfo(int taskPosition, boolean isTop) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(taskPosition+1);
        ActivityManager.RunningTaskInfo topTaskInfo = runningTaskInfoList.get(taskPosition);
        ComponentName activityComponent = isTop ? topTaskInfo.topActivity : topTaskInfo.baseActivity;
        DocumentSectionData data = new DocumentSectionData.Builder()
                .add("PackageName", activityComponent.getPackageName())
                .add("ShortClassName", activityComponent.getShortClassName())
                //.add("Name", Humanizer.getLastPart(activityComponent.getClassName(), "."))
                //.add("ClassName", activityComponent.getClassName())
                //.add("describeContents", activityComponent.describeContents())
                .build();

        return data.getEntries();
    }
}
