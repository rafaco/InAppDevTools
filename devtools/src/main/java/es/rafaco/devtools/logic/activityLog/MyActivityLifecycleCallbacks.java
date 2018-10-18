package es.rafaco.devtools.logic.activityLog;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.logic.shake.ShakeDetector;
import es.rafaco.devtools.logic.utils.FriendlyLog;
import es.rafaco.devtools.view.overlay.screens.friendlylog.FriendlyLogHelper;

public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private final ActivityLogManager manager;
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


    public MyActivityLifecycleCallbacks(ActivityLogManager manager) {
        this.manager = manager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        friendlyLog("I","Created", activity);

        //TODO: Crash Handler
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
        friendlyLog("V","Started", activity);

        manager.currentlyStartedActivities++;
        manager.isInBackground = (manager.currentlyStartedActivities == 0);
        //Do nothing
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        friendlyLog("V","Resumed", activity);

        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " resumed\n");
        manager.setLastActivityResumed(activity.getClass().getSimpleName());

        updateBackgroundStateOnActivityResumed();

        addShakeListener();
        addTouchListener(activity);
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
        friendlyLog("V","Paused", activity);

        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " paused\n");

        updateBackgroundStateOnActivityPaused();
        removeShakeListener();
        removeTouchListener(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        friendlyLog("D","Stopped", activity);

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
        friendlyLog("D","Destroy", activity);

        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " destroyed\n");
    }


    //region [ TOUCH LISTENER ]

    private void addTouchListener(final Activity activity) {
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

    private void removeTouchListener(Activity activity) {
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(DevTools.TAG, "Paused activity without decorView");
            return;
        }
        decorView.setOnTouchListener(null);
    }

    //endregion

    //region [ SHAKE LISTENER ]

    private void addShakeListener() {
        ShakeDetector detector = DevTools.getShakeDetector();
        if (detector!=null){
            detector.registerSensorListener();
        }
    }

    private void removeShakeListener() {
        ShakeDetector detector = DevTools.getShakeDetector();
        if (detector!=null){
            detector.unRegisterSensorListener();
        }
    }

    //endregion


    //region [ BACKGROUND STATE ]

    private boolean mInBackground = true;
    private static final long BACKGROUND_DELAY = 500;
    private final List<BackgroundStateListener> listeners = new ArrayList<>();
    private final Handler mBackgroundDelayHandler = new Handler();
    private Runnable mBackgroundTransition;

    public interface BackgroundStateListener {
        void onBecameForeground();

        void onBecameBackground();
    }

    public void registerListener(BackgroundStateListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(BackgroundStateListener listener) {
        listeners.remove(listener);
    }

    public boolean isInBackground() {
        return mInBackground;
    }

    private void updateBackgroundStateOnActivityResumed() {
        if (mBackgroundTransition != null) {
            mBackgroundDelayHandler.removeCallbacks(mBackgroundTransition);
            mBackgroundTransition = null;
        }

        if (mInBackground) {
            mInBackground = false;
            FriendlyLog.log("I","App", "Foreground", "Application went to foreground");
            notifyOnBecameForeground();
        }
    }

    private void updateBackgroundStateOnActivityPaused() {
        if (!mInBackground && mBackgroundTransition == null) {
            mBackgroundTransition = new Runnable() {
                @Override
                public void run() {
                    mInBackground = true;
                    mBackgroundTransition = null;
                    FriendlyLog.log("I","App", "Background", "Application went to background");
                    notifyOnBecameBackground();
                }
            };
            mBackgroundDelayHandler.postDelayed(mBackgroundTransition, BACKGROUND_DELAY);
        }
    }

    private void notifyOnBecameForeground() {
        for (BackgroundStateListener listener : listeners) {
            try {
                listener.onBecameForeground();
            } catch (Exception e) {
                Log.d(DevTools.TAG, "Listener threw exception!", e);
            }
        }
    }

    private void notifyOnBecameBackground() {
        for (BackgroundStateListener listener : listeners) {
            try {
                listener.onBecameBackground();
            } catch (Exception e) {
                Log.d(DevTools.TAG, "Listener threw exception!", e);
            }
        }
    }

    //endregion

    public static void friendlyLog(String severity, String type, Activity activity) {
        String message = "Activity " + type.toLowerCase() + ": " + activity.getClass().getSimpleName();
        FriendlyLog.log(severity, "Activity", type, message);
    }
}