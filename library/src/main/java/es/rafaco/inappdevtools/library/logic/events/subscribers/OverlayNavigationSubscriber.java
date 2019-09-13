package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.view.overlay.navigation.NavigationStep;

public class OverlayNavigationSubscriber extends EventSubscriber {

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

        eventManager.subscribe(Event.OVERLAY_HIDDEN, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                //IadtController.get().setCurrentOverlay(null);
                FriendlyLog.log("D", "Iadt", "Hidden", "Overlay hidden");
            }
        });
    }
}
