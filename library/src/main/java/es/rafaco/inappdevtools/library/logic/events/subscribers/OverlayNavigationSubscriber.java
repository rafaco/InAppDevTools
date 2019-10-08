package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.navigation.NavigationStep;

public class OverlayNavigationSubscriber extends EventSubscriber {

    private boolean isForeground = false;
    private boolean isSuspended = false;

    public OverlayNavigationSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.OVERLAY_NAVIGATION, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                NavigationStep step = (NavigationStep) param;
                //IadtController.get().setCurrentOverlay(step.getStringClassName());
                FriendlyLog.log("D", "Iadt", "Navigation", "Overlay navigation to " + step.getStringClassName());
            }
        });

        eventManager.subscribe(Event.OVERLAY_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                isForeground = false;
                FriendlyLog.log("D", "Iadt", "Background", "Overlay to background");
            }
        });

        eventManager.subscribe(Event.OVERLAY_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                isForeground = true;
                FriendlyLog.log("D", "Iadt", "Foreground", "Overlay to foreground");
            }
        });

        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (!isForeground && isSuspended){
                    isForeground = true;
                    isSuspended = false;
                    eventManager.fire(Event.OVERLAY_FOREGROUND);
                }
            }
        });

        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (isForeground) {
                    isForeground = false;
                    isSuspended = true;
                    eventManager.fire(Event.OVERLAY_BACKGROUND);
                }
            }
        });
    }
}
