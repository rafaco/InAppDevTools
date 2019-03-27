package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.logic.watcher.activityLog.ActivityLogManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;

public class LiveInfoHelper extends AbstractInfoHelper {

    public LiveInfoHelper(Context context) {
        super(context);
    }

    @Override
    public String getOverview() {
        return "";
    }

    @Override
    public InfoReport getInfoReport() {
        return new InfoReport.Builder("")
                .add(getActivityInfo())
                .add()
                .add(getRunningInfo())
                .build();
    }

    public InfoGroup getActivityInfo() {
        ActivityLogManager logManager = DevTools.getActivityLogManager();
        return new InfoGroup.Builder("")
                .add("App on " + (logManager.isInBackground() ? "Background" : "Foreground"))
                .add("Top activity is " + getTopActivity())
                .build();
    }

    public InfoGroup getRunningInfo() {
        return new InfoGroup.Builder("")
                .add("Services", getRunningServices())
                .add("Tasks", getRunningTasks())
                .build();
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
}
