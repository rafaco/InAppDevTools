package es.rafaco.devtools.logic.activityLog;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;

public class ActivityLogManager {

    private final int MAX_ACTIVITIES_IN_LOG = 100;
    protected final Deque<String> activityLog = new ArrayDeque<>(MAX_ACTIVITIES_IN_LOG);
    protected WeakReference<Activity> lastActivityCreated = new WeakReference<>(null);
    protected boolean isInBackground = true;
    protected String lastActivityResumed = "";
    protected int currentlyStartedActivities = 0;
    private final Context context;

    public ActivityLogManager(Context context) {
        this.context = context;
        init();
    }

    private void init(){
        Application app = (Application)context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks(this));
    }

    public String getLog(){
        StringBuilder activityLogStringBuilder = new StringBuilder();
        activityLogStringBuilder.append("ActivityLog:"+"\n");
        while (!activityLog.isEmpty()) {
            //TODO: this is deleting the log every time
            activityLogStringBuilder.append(activityLog.poll());
        }
        return activityLogStringBuilder.toString();
    }

    public boolean isInBackground(){
        return isInBackground;
    }

    public int getStartedActivitiesCount() {
        return currentlyStartedActivities;
    }

    public String getLastActivityResumed() {
        return lastActivityResumed;
    }

    public void setLastActivityResumed(String lastActivityResumed) {
        this.lastActivityResumed = lastActivityResumed;
    }
}
