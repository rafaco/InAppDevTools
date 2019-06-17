package es.rafaco.inappdevtools.library.logic.events;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.DevToolsConfig;

public class EventManager {

    private final Context context;
    private EventDetectorsManager eventDetectorsManager;
    private EventSubscribersManager eventSubscribersManager;

    private Map<Event, List<Listener>> eventListeners = new HashMap<>();

    public EventManager(Context context) {
        this.context = context;
        init(DevTools.getConfig());
    }

    public void init(DevToolsConfig config) {
        eventDetectorsManager = new EventDetectorsManager(this);
        eventSubscribersManager = new EventSubscribersManager(this);
    }

    public void subscribe(Event event, Listener listener){
        if (!eventListeners.containsKey(event)){
            eventListeners.put(event, new ArrayList<Listener>());
        }
        List<Listener> currentOnEventListeners = eventListeners.get(event);
        currentOnEventListeners.add(listener);
    }

    public void fire(Event event){
        fire(event, null);
    }

    public void fire(Event event, Object param){
        if (eventListeners.containsKey(event)){
            List<Listener> listeners = eventListeners.get(event);
            List<Listener> toRemove = new ArrayList<>();
            for (Listener listener : listeners) {
                listener.onEvent(event, param);
                if (listener instanceof OneShotListener)
                    toRemove.add(listener);
            }

            if (!toRemove.isEmpty()){
                listeners.removeAll(toRemove);
                eventListeners.put(event, listeners);
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public EventDetectorsManager getEventDetectorsManager() {
        return eventDetectorsManager;
    }

    public void destroy() {
        eventListeners.clear();
        eventDetectorsManager.destroy();
    }


    public interface Listener {
        void onEvent(Event event, Object param);
    }

    public interface OneShotListener extends Listener {
    }
}
