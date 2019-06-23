package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;

public class ForegroundSyncSubscriber extends EventSubscriber {

    private static boolean autoclosed = false;

    public ForegroundSyncSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (autoclosed){
                    IadtController.get().showOverlay(false);
                    autoclosed = false;
                }
            }
        });
        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (isOverlayShown()){
                    IadtController.get().hideOverlay();
                    autoclosed = true;
                }
            }
        });
    }

    private boolean isOverlayShown() {
        //TODO: detect overlay on screen
        return true;
    }
}
