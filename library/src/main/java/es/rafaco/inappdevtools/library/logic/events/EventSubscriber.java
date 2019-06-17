package es.rafaco.inappdevtools.library.logic.events;

import android.content.Context;

public abstract class EventSubscriber {

    protected EventManager eventManager;

    public EventSubscriber(EventManager eventManager) {
        this.eventManager = eventManager;
        subscribe();
    }


    public Context getContext () {
        return eventManager.getContext();
    }

    /**
     * Called after creation
     */
    public abstract void subscribe();

}
