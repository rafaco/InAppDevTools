package es.rafaco.inappdevtools.library.logic.events;

import android.content.Context;

public class EventManager {

    public EventManager(Context context) {
    }

    public void subscribe(Event event, Listener listener){
    }

    public void fire(Event event){}

    public void fire(Event event, Object param){}

    public Context getContext() {
        return null;
    }

    public void destroy() {}


    public interface Listener {
        void onEvent(Event event, Object param);
    }

    public interface OneShotListener extends Listener {
    }
}
