package es.rafaco.inappdevtools.library.logic.event;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.DevToolsConfig;
import es.rafaco.inappdevtools.library.logic.event.reactor.EventReactor;
import es.rafaco.inappdevtools.library.logic.event.reactor.OverlaySyncEventReactor;
import es.rafaco.inappdevtools.library.logic.event.watcher.WatcherManager;

public class EventManager {

    private final Context context;
    private WatcherManager watcherManager;
    private Map<Event, List<OnEventListener>> eventListeners = new HashMap<>();
    private List<EventReactor> eventReactors = new ArrayList<>();

    public EventManager(Context context) {
        this.context = context;
        init(DevTools.getConfig());
    }

    public void init(DevToolsConfig config) {
        watcherManager = new WatcherManager(this);
        eventReactors.add(new OverlaySyncEventReactor(this));
    }

    public void subscribe(Event event, OnEventListener listener){
        if (!eventListeners.containsKey(event)){
            eventListeners.put(event, new ArrayList<OnEventListener>());
        }
        List<OnEventListener> currentOnEventListeners = eventListeners.get(event);
        currentOnEventListeners.add(listener);
    }

    public void fire(Event event){
        fire(event, null);
    }

    public void fire(Event event, Object param){
        if (eventListeners.containsKey(event)){
            List<OnEventListener> onEventListeners = this.eventListeners.get(event);
            for (OnEventListener listener : onEventListeners) {
                listener.onEvent(event, param);
            }
        }
    }

    public Context getContext() {
        return context;
    }

    public WatcherManager getWatcherManager() {
        return watcherManager;
    }

    public void destroy() {
        eventListeners.clear();
        watcherManager.destroy();
    }

    public interface OnEventListener {
        void onEvent(Event event, Object param);
    }
}
