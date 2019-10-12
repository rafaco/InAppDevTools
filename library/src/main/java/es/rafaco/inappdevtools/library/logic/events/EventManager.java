package es.rafaco.inappdevtools.library.logic.events;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class EventManager {

    private final Context context;
    private EventDetectorsManager eventDetectorsManager;
    private EventSubscribersManager eventSubscribersManager;

    private Map<Event, List<Listener>> eventListeners = new HashMap<>();

    public EventManager(Context context) {
        this.context = context;
        eventDetectorsManager = new EventDetectorsManager(this);
        eventSubscribersManager = new EventSubscribersManager(this);
    }

    public void subscribe(Event event, Listener listener){
        if (!eventListeners.containsKey(event)){
            eventListeners.put(event, new ArrayList<Listener>());
        }
        List<Listener> listenerForEvent = eventListeners.get(event);
        if (listenerForEvent.contains(listener)){
            FriendlyLog.log("W", "Iadt", "EventManager", "Listener object already added!! "+listener.toString()+" skipped for " + event.getName());
            return;
        }
        listenerForEvent.add(listener);
    }

    public void unSubscribe(Event event, Listener listener){
        List<Listener> listeners = eventListeners.get(event);
        if (listeners!=null && listeners.size()>0){
            for (Listener existingListener : listeners) {
                if (existingListener.equals(listener)){
                    listeners.remove(listener);
                    return;
                }
            }
        }
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
