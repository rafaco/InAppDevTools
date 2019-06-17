package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;

public class ForegroundInitSubscriber extends EventSubscriber {

    public ForegroundInitSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        //TODO: make it one shot?
        eventManager.subscribe(Event.USER_PRESENT, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                DevTools.initForegroundIfPending();
            }
        });
    }
}
