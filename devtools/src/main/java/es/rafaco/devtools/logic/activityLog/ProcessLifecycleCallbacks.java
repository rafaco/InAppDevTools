package es.rafaco.devtools.logic.activityLog;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import es.rafaco.devtools.logic.utils.FriendlyLog;

public class ProcessLifecycleCallbacks implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void init() {
        FriendlyLog.log("W", "Lifecycle", "ON_CREATE", "Lifecycle.Event.ON_CREATE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void LibOnStart() {
        FriendlyLog.log("W", "Lifecycle", "ON_START", "Lifecycle.Event.ON_START");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void LibOnStop() {
        FriendlyLog.log("W", "Lifecycle", "ON_STOP", "Lifecycle.Event.ON_STOP");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void LibOnResume() {
        FriendlyLog.log("W", "Lifecycle", "ON_RESUME", "Lifecycle.Event.ON_RESUME");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void LibOnPause() {
        FriendlyLog.log("W", "Lifecycle", "ON_PAUSE", "Lifecycle.Event.ON_PAUSE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void cleanup() {
        FriendlyLog.log("W", "Lifecycle", "ON_DESTROY", "Lifecycle.Event.ON_DESTROY");
    }
}
