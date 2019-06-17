package es.rafaco.inappdevtools.library.logic.event.reactor;

import android.content.Intent;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.event.Event;
import es.rafaco.inappdevtools.library.logic.event.EventManager;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;

public class OverlaySyncEventReactor extends EventReactor {

    private static boolean autoclosed = false;

    public OverlaySyncEventReactor(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void init() {
        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.OnEventListener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (autoclosed){
                    Intent intent = OverlayUIService.buildIntentAction(OverlayUIService.IntentAction.SHOW,null);
                    DevTools.getAppContext().startService(intent);
                    autoclosed = false;
                }
            }
        });
        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.OnEventListener() {
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
