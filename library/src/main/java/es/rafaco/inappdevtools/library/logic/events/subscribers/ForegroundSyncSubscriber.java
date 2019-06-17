package es.rafaco.inappdevtools.library.logic.events.subscribers;

import android.content.Intent;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;

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
                    Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.SHOW,null);
                    DevTools.getAppContext().startService(intent);
                    autoclosed = false;
                }
            }
        });
        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (isOverlayShown()){
                    Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.HIDE,null);
                    DevTools.getAppContext().startService(intent);
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
