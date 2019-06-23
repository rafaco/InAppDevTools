package es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayDeque;
import java.util.Deque;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class ActivityEventDetector extends EventDetector implements Application.ActivityLifecycleCallbacks {

    protected final Deque<String> activityLog = new ArrayDeque<>(100);
    //protected WeakReference<Activity> lastActivityCreated = new WeakReference<>(null);
    protected int currentlyStartedActivities = 0;
    protected String lastActivityResumed = "";
    protected boolean isInBackground = true;

    public ActivityEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.ACTIVITY_ON_CREATE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                friendlyLog("D","Create", (Activity) param);
            }
        });

        eventManager.subscribe(Event.ACTIVITY_ON_START, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                friendlyLog("D","Start", (Activity) param);
            }
        });

        eventManager.subscribe(Event.ACTIVITY_ON_RESUME, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                friendlyLog("V","Resume", (Activity) param);
            }
        });

        eventManager.subscribe(Event.ACTIVITY_OPEN, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I", "App", "Navigation",
                        "Open " + ((Activity) param).getClass().getSimpleName());
            }
        });

        eventManager.subscribe(Event.ACTIVITY_ON_PAUSE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                friendlyLog("V","Pause", (Activity) param);
            }
        });

        eventManager.subscribe(Event.ACTIVITY_ON_STOP, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                friendlyLog("D","Stop", (Activity) param);
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_SAVE_INSTANCE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                friendlyLog("V","SaveInstanceState", (Activity) param);
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_DESTROY, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                friendlyLog("D","Destroy", (Activity) param);
            }
        });

    }

    @Override
    public void start() {
        Application app = (Application)getContext().getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void stop() {
        Application app = (Application)getContext().getApplicationContext();
        app.unregisterActivityLifecycleCallbacks(this);
    }


    //region [ DETECTION ]

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        eventManager.fire(Event.ACTIVITY_ON_CREATE, activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentlyStartedActivities++;
        isInBackground = (currentlyStartedActivities == 0);
        eventManager.fire(Event.ACTIVITY_ON_START, activity);
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        eventManager.fire(Event.ACTIVITY_ON_RESUME, activity);
        if (!lastActivityResumed.equals(activity.getClass().getSimpleName())){
            eventManager.fire(Event.ACTIVITY_OPEN, activity);
        }
        lastActivityResumed = activity.getClass().getSimpleName();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        eventManager.fire(Event.ACTIVITY_ON_PAUSE, activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        currentlyStartedActivities--;
        isInBackground = (currentlyStartedActivities == 0);
        eventManager.fire(Event.ACTIVITY_ON_STOP, activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        eventManager.fire(Event.ACTIVITY_ON_SAVE_INSTANCE, activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        eventManager.fire(Event.ACTIVITY_ON_DESTROY, activity);
    }

    //endregion


    public static void friendlyLog(String severity, String type, Activity activity) {
        String message = "Activity " + type.toLowerCase() + ": " + activity.getClass().getSimpleName();
        FriendlyLog.log(severity, "Activity", type, message);
    }


    //TODO: seems not used, check if work better than the used one
    public boolean isInBackground(){
        return isInBackground;
    }

    public void setInBackground(boolean inBackground) {
        isInBackground = inBackground;
    }

    public String getLog(){
        StringBuilder activityLogStringBuilder = new StringBuilder();
        activityLogStringBuilder.append("ActivityLog:"+"\n");
        while (!activityLog.isEmpty()) {
            activityLogStringBuilder.append(activityLog.peek());
        }
        return activityLogStringBuilder.toString();
    }

    public String getLastActivityResumed() {
        return lastActivityResumed;
    }

    public void setLastActivityResumed(String lastActivityResumed) {
        this.lastActivityResumed = lastActivityResumed;
    }
}
