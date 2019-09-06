package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.Config;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;
import es.rafaco.inappdevtools.library.view.activities.PermissionActivity;

/**
 * Hide our overlay UI when the app goes to background and restore it when get foreground
 */
public class ForegroundSyncSubscriber extends EventSubscriber {

    private static boolean pendingRestoration = false;
    private static boolean pendingInitialization = false;

    public ForegroundSyncSubscriber(EventManager eventManager) {
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
                if (PermissionActivity.check(PermissionActivity.IntentAction.OVERLAY)
                        //&& AppUtils.isForegroundImportance(getContext())
                        && (controller.getCurrentOverlay() != null
                            || controller.getConfig().getBoolean(Config.INVOCATION_BY_ICON))){
                    controller.hideAll();
                    pendingRestoration = true;
                }
            }
        });
    }
}
