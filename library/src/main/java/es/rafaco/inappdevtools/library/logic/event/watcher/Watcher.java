package es.rafaco.inappdevtools.library.logic.event.watcher;

import android.content.Context;

import es.rafaco.inappdevtools.library.logic.event.EventManager;

public abstract class Watcher {
    protected EventManager eventManager;

    public Watcher(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public Context getContext () {
        return eventManager.getContext();
    }

    public abstract void init();
    public abstract void start();
    public abstract void stop();

}
