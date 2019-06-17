package es.rafaco.inappdevtools.library.logic.event.reactor;

import android.content.Context;

import es.rafaco.inappdevtools.library.logic.event.EventManager;

public abstract class EventReactor {
    protected EventManager eventManager;

    public EventReactor(EventManager eventManager) {
        this.eventManager = eventManager;
        init();
    }

    public Context getContext () {
        return eventManager.getContext();
    }

    public abstract void init();

}
