package es.rafaco.compat;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;

import es.rafaco.inappdevtools.library.logic.watcher.activityLog.ProcessLifecycleCallbacks;

public class CompatProcessLifecycleCallbacks implements LifecycleObserver {

    public CompatProcessLifecycleCallbacks() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        ProcessLifecycleCallbacks.onCreate();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        ProcessLifecycleCallbacks.onStart();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        ProcessLifecycleCallbacks.onStop();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        ProcessLifecycleCallbacks.onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        ProcessLifecycleCallbacks.onCreate();    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        ProcessLifecycleCallbacks.onDestroy();
    }
}
