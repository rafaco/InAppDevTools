package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;

/**
 * Hide our overlay UI when the app goes to background and restore it when get foreground
 */
public class OverlaySyncWithForegroundSubscriber extends EventSubscriber {

    private static boolean pendingRestoration = false;

    public OverlaySyncWithForegroundSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        final IadtController controller = IadtController.get();
        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (pendingRestoration){
                    controller.restoreAll();
                    pendingRestoration = false;
                }
            }
        });
        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                if (OverlayUIService.isInitialize()
                        && (controller.getCurrentOverlay() != null
                            || controller.getConfig().getBoolean(Config.INVOCATION_BY_ICON))){
                    controller.hideAll();
                    pendingRestoration = true;
                }
            }
        });
    }
}
