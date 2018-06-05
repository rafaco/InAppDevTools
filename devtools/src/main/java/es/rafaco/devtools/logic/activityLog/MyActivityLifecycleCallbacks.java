package es.rafaco.devtools.logic.activityLog;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private static boolean isInBackground = true;
    private final ActivityLogManager manager;
    int currentlyStartedActivities = 0;
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public MyActivityLifecycleCallbacks(ActivityLogManager manager) {
        this.manager = manager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (false){ //activity.getClass() != config.getErrorActivityClass()) {
            // Copied from ACRA:
            // Ignore activityClass because we want the last
            // application Activity that was started so that we can
            // explicitly kill it off.
            manager.lastActivityCreated = new WeakReference<>(activity);
        }
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " created\n");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentlyStartedActivities++;
        isInBackground = (currentlyStartedActivities == 0);
        //Do nothing
    }

    @Override
    public void onActivityResumed(Activity activity) {
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " resumed\n");
    }

    @Override
    public void onActivityPaused(Activity activity) {
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " paused\n");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //Do nothing
        currentlyStartedActivities--;
        isInBackground = (currentlyStartedActivities == 0);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //Do nothing
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " destroyed\n");
    }
}