package es.rafaco.inappdevtools.library.logic.event;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.DevToolsConfig;

public class EventManager {

    private final Context context;
    private Map<Event, List<OnEventListener>> eventListeners = new HashMap<>();

    public EventManager(Context context) {
        this.context = context;
        init(DevTools.getConfig());
    }

    public void init(DevToolsConfig config) {

    }

    public void subscribe(Event event, OnEventListener listener){
        if (!eventListeners.containsKey(event)){
            eventListeners.put(event, new ArrayList<OnEventListener>());
        }
        List<OnEventListener> currentEventListeners = eventListeners.get(event);
        currentEventListeners.add(listener);
    }

    public void fire(Event event){
        fire(event, null);
    }

    public void fire(Event event, Object param){
        if (eventListeners.containsKey(event)){
            List<OnEventListener> eventListeners = this.eventListeners.get(event);
            for (OnEventListener listener : eventListeners) {
                listener.onEvent(event, param);
            }
        }
        eventListeners.put(event, new ArrayList<OnEventListener>());
    }

    public Context getContext() {
        return context;
    }

    public interface OnEventListener {
        void onEvent(Event event, Object param);
    }
}
