package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;

/**
 * Hide our overlay UI when the app goes to background and restore it when get foreground
 */
public class ForegroundSyncSubscriber extends EventSubscriber {

    private static boolean pendingRestoration = false;

    public ForegroundSyncSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (pendingRestoration){
                    IadtController.get().show();
                    pendingRestoration = false;
                }
            }
        });
        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                boolean hidden = IadtController.get().hide();
                if (hidden) pendingRestoration = true;
            }
        });
    }
}
