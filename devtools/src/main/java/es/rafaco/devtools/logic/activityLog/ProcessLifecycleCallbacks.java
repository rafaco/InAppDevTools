package es.rafaco.devtools.logic.activityLog;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import es.rafaco.devtools.logic.utils.FriendlyLog;

public class ProcessLifecycleCallbacks implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void init() {
        FriendlyLog.log("D", "Process", "ON_CREATE", "Process created");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void LibOnStart() {
        FriendlyLog.log("D", "Process", "ON_START", "Process started");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void LibOnStop() {
        FriendlyLog.log("D", "Process", "ON_STOP", "Process stopped");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void LibOnResume() {
        FriendlyLog.log("D", "Process", "ON_RESUME", "Process resumed");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void LibOnPause() {
        FriendlyLog.log("D", "Process", "ON_PAUSE", "Process paused");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void cleanup() {
        FriendlyLog.log("I", "Process", "ON_DESTROY", "Process destroyed");
    }
}
