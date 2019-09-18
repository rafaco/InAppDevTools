package es.rafaco.inappdevtools.library.logic.utils;

import android.app.ActivityManager;
import android.content.Context;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle.ActivityEventDetector;
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
            String text = id + " - " + topActivity + " top of " + numOfActivities;
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
