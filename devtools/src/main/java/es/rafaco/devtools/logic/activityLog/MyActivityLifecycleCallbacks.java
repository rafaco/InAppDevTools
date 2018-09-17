package es.rafaco.devtools.logic.activityLog;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.rafaco.devtools.DevTools;

public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private final ActivityLogManager manager;
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public MyActivityLifecycleCallbacks(ActivityLogManager manager) {
        this.manager = manager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //TODO: Crash Handler
        if (false){ //activity.getClass() != config.getErrorActivityClass()) {
            // Copied from ACRA:
            // Ignore activityClass because we want the last
            // application Activity that was started so that we can
            // explicitly kill it off.
            manager.lastActivityCreated = new WeakReference<>(activity);
        }
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " created\n");
        Log.d(DevTools.TAG, "Activity created: " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        manager.currentlyStartedActivities++;
        manager.isInBackground = (manager.currentlyStartedActivities == 0);
        //Do nothing
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " resumed\n");
        manager.setLastActivityResumed(activity.getClass().getSimpleName());

        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(DevTools.TAG, "Resumed activity without decorView");
            return;
        }
        decorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(DevTools.TAG, "Click X:" + event.getX() + " Y:" + event.getY() + " at " + activity.getClass().getSimpleName() +
                    " - " + v.getClass().getSimpleName() + ": " + getResourceName(v, activity));
                return false;
            }
        });
    }

    private String getResourceName(View v, Activity activity) {
        try{
            return activity.getResources().getResourceName(v.getId());
        }catch (Resources.NotFoundException e){
            return "[ not set ]";
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " paused\n");

        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(DevTools.TAG, "Paused activity without decorView");
            return;
        }
        decorView.setOnTouchListener(null);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //Do nothing
        manager.currentlyStartedActivities--;
        manager.isInBackground = (manager.currentlyStartedActivities == 0);
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