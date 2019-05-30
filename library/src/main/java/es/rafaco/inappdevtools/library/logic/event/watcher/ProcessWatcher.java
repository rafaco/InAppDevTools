package es.rafaco.inappdevtools.library.logic.event.watcher;


//#ifdef MODERN
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
//#else
//@import android.arch.lifecycle.Lifecycle;
//@import android.arch.lifecycle.LifecycleObserver;
//@import android.arch.lifecycle.OnLifecycleEvent;
//@import android.arch.lifecycle.ProcessLifecycleOwner;
//#endif

import es.rafaco.inappdevtools.library.logic.event.Event;
import es.rafaco.inappdevtools.library.logic.event.EventManager;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class ProcessWatcher extends Watcher implements LifecycleObserver {

    public ProcessWatcher(EventManager manager) {
        super(manager);
    }

    @Override
    public void init() {
        eventManager.subscribe(Event.PROCESS_ON_CREATE, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Create", "Process created");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_START, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Start", "Process started");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_STOP, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Stop", "Process stopped");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_RESUME, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Resume", "Process resumed");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_PAUSE, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("D", "Process", "Pause", "Process paused");
            }
        });

        eventManager.subscribe(Event.PROCESS_ON_DESTROY, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I", "Process", "Destroy", "Process destroyed");
            }
        });
    }@Override
    public void start() {
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public void stop() {
        //ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);
    }



    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        eventManager.fire(Event.PROCESS_ON_CREATE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        eventManager.fire(Event.PROCESS_ON_START);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        eventManager.fire(Event.PROCESS_ON_STOP);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        eventManager.fire(Event.PROCESS_ON_RESUME);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        eventManager.fire(Event.PROCESS_ON_PAUSE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        eventManager.fire(Event.PROCESS_ON_DESTROY);
    }
}
