package es.rafaco.devtools.logic.activityLog;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.Deque;

public class ActivityLogManager {

    private static final int MAX_ACTIVITIES_IN_LOG = 50;
    protected static final Deque<String> activityLog = new ArrayDeque<>(MAX_ACTIVITIES_IN_LOG);
    protected static WeakReference<Activity> lastActivityCreated = new WeakReference<>(null);
    private static boolean isInBackground = true;
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
        while (!activityLog.isEmpty()) {
            activityLogStringBuilder.append(activityLog.poll());
        }
        return activityLogStringBuilder.toString();
    }
}
