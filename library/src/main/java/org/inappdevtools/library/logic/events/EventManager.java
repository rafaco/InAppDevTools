/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2022 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inappdevtools.library.logic.events;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.inappdevtools.library.logic.log.FriendlyLog;

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

    public void unsubscribe(Event event, Listener listener){
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
