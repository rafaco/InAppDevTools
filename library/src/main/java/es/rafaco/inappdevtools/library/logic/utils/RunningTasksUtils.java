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
import android.content.ComponentName;
import android.content.Context;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
import es.rafaco.inappdevtools.library.logic.info.data.InfoEntryData;
import es.rafaco.inappdevtools.library.logic.info.data.InfoGroupData;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningTasksUtils {

    private static String OLD_LAUNCHER_ACTIVITY_CLASS = "com.google.android.launcher.GEL";
    private static String NEW_LAUNCHER_ACTIVITY_CLASS = "com.google.android.apps.nexuslauncher.NexusLauncherActivity";

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

    public static int getCount() {
        int count = 0;
        List<ActivityManager.RunningTaskInfo> tasks = getList();

        for(ActivityManager.RunningTaskInfo task : tasks){
            if (!isLauncherTask(task)) {
                count ++;
            }
        }
        return count;
    }

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

    //TODO: It don't seem the best way
    public static String getTopActivityStatus() {
        ActivityEventDetector activityWatcher = (ActivityEventDetector) IadtController.get().getEventManager()
                .getEventDetectorsManager().get(ActivityEventDetector.class);
        return activityWatcher.isInBackground() ? "Background" : "Foreground";
    }

    public static String getTopActivityClassName() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo topTaskInfo = runningTaskInfoList.get(0);
        ComponentName topActivity = topTaskInfo.topActivity;

        return topActivity.getClassName();
    }

    public static List<InfoEntryData> getTopActivityInfo() {
        return getActivityInfo(0, true);
    }

    private static List<InfoEntryData> getActivityInfo(int taskPosition, boolean isTop) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(taskPosition+1);
        ActivityManager.RunningTaskInfo topTaskInfo = runningTaskInfoList.get(taskPosition);
        ComponentName activityComponent = isTop ? topTaskInfo.topActivity : topTaskInfo.baseActivity;
        InfoGroupData data = new InfoGroupData.Builder()
                .add("PackageName", activityComponent.getPackageName())
                .add("ShortClassName", activityComponent.getShortClassName())
                //.add("Name", Humanizer.getLastPart(activityComponent.getClassName(), "."))
                //.add("ClassName", activityComponent.getClassName())
                //.add("describeContents", activityComponent.describeContents())
                .build();

        return data.getEntries();
    }

    private static List<ActivityManager.RunningTaskInfo> getList() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =  manager.getRunningTasks(Integer.MAX_VALUE);
        return runningTaskInfoList;
    }

    private static boolean isLauncherTask(ActivityManager.RunningTaskInfo task) {
        String baseActivityName = task.baseActivity.getClassName();
        return baseActivityName.equals(NEW_LAUNCHER_ACTIVITY_CLASS) || baseActivityName.equals(OLD_LAUNCHER_ACTIVITY_CLASS);
    }
}
