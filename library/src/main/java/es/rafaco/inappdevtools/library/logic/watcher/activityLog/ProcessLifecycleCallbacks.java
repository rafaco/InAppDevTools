package es.rafaco.inappdevtools.library.logic.watcher.activityLog;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class ProcessLifecycleCallbacks implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void init() {
        FriendlyLog.log("D", "Process", "Create", "Process created");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void LibOnStart() {
        FriendlyLog.log("D", "Process", "Start", "Process started");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void LibOnStop() {
        FriendlyLog.log("D", "Process", "Stop", "Process stopped");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void LibOnResume() {
        FriendlyLog.log("D", "Process", "Resume", "Process resumed");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void LibOnPause() {
        FriendlyLog.log("D", "Process", "Pause", "Process paused");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void cleanup() {
        FriendlyLog.log("I", "Process", "Destroy", "Process destroyed");
    }
}
