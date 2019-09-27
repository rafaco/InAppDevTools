package es.rafaco.inappdevtools.library.logic.events.subscribers;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.config.BuildConfig;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.events.EventSubscriber;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class ConfigChangeSubscriber extends EventSubscriber {

    public ConfigChangeSubscriber(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {

        eventManager.subscribe(Event.CONFIG_CHANGED, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                BuildConfig target = (BuildConfig) param;

                FriendlyLog.log("D", "Iadt", "BuildConfig",
                        "BuildConfig changed: " + target.getKey() + " to " + IadtController.get().getConfig().get(target));

                /*if (target.getKey().equals(BuildConfig.ENABLED.getKey())){
                    //TODO: restart library instead of app
                    Iadt.showMessage("Restart needed");
                    IadtController.get().restartApp(false);
                }*/
            }
        });
    }
}
